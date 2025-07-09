# Script de inicio para el Sistema de Monitoreo Médico Distribuido (Windows)
# Autor: Limones & Munarco

Write-Host "🏥 Iniciando Sistema de Monitoreo Médico Distribuido..." -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green

function Write-Status {
    param($Message)
    Write-Host "[INFO] $Message" -ForegroundColor Blue
}

function Write-Success {
    param($Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

function Write-Warning {
    param($Message)
    Write-Host "[WARNING] $Message" -ForegroundColor Yellow
}

function Write-Error {
    param($Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

# Verificar si Docker está instalado
if (!(Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Error "Docker no está instalado. Por favor instale Docker Desktop primero."
    exit 1
}

# Verificar si Docker Compose está disponible
if (!(Get-Command docker-compose -ErrorAction SilentlyContinue)) {
    Write-Error "Docker Compose no está disponible. Verificando si está integrado en Docker..."
    if (!(docker compose version)) {
        Write-Error "Docker Compose no está disponible. Por favor instale Docker Compose."
        exit 1
    }
}

# Verificar si Maven está instalado
if (!(Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Error "Maven no está instalado. Por favor instale Maven primero."
    exit 1
}

# Verificar si Java está instalado
if (!(Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Error "Java no está instalado. Por favor instale Java 21+ primero."
    exit 1
}

Write-Status "Verificando versión de Java..."
$javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_.Line }
Write-Success "Java: $javaVersion"

# Crear directorio de logs si no existe
if (!(Test-Path "logs")) {
    New-Item -ItemType Directory -Name "logs" | Out-Null
}

# Paso 1: Iniciar servicios de infraestructura
Write-Status "Paso 1: Iniciando servicios de infraestructura..."
docker-compose up -d

if ($LASTEXITCODE -eq 0) {
    Write-Success "Servicios de infraestructura iniciados correctamente"
} else {
    Write-Error "Error al iniciar servicios de infraestructura"
    exit 1
}

# Esperar a que los servicios estén listos
Write-Status "Esperando a que los servicios estén listos..."
Start-Sleep -Seconds 30

# Paso 2: Compilar microservicios
Write-Status "Paso 2: Compilando microservicios..."

$microservices = @("ms-eureka-server", "ms-gateway-server", "PatientDataCollector", "HealthAnalyzer", "CareNotifier", "sincronizacion")

foreach ($service in $microservices) {
    if (Test-Path $service) {
        Write-Status "Compilando $service..."
        Set-Location $service
        mvn clean compile -q
        if ($LASTEXITCODE -eq 0) {
            Write-Success "$service compilado correctamente"
        } else {
            Write-Error "Error compilando $service"
            Set-Location ..
            exit 1
        }
        Set-Location ..
    } else {
        Write-Warning "Directorio $service no encontrado, saltando..."
    }
}

# Paso 3: Iniciar microservicios
Write-Status "Paso 3: Iniciando microservicios..."

# Iniciar Eureka Server
Write-Status "Iniciando Eureka Server..."
Set-Location ms-eureka-server
Start-Process -FilePath "cmd" -ArgumentList "/c", "mvn spring-boot:run > ..\logs\eureka.log 2>&1" -WindowStyle Hidden
Set-Location ..
Start-Sleep -Seconds 20

# Iniciar API Gateway
Write-Status "Iniciando API Gateway..."
Set-Location ms-gateway-server
Start-Process -FilePath "cmd" -ArgumentList "/c", "mvn spring-boot:run > ..\logs\gateway.log 2>&1" -WindowStyle Hidden
Set-Location ..
Start-Sleep -Seconds 15

# Iniciar microservicios de negocio
$businessServices = @("PatientDataCollector", "HealthAnalyzer", "CareNotifier")

foreach ($service in $businessServices) {
    if (Test-Path $service) {
        Write-Status "Iniciando $service..."
        Set-Location $service
        $logFile = "..\logs\$($service.ToLower()).log"
        Start-Process -FilePath "cmd" -ArgumentList "/c", "mvn spring-boot:run > $logFile 2>&1" -WindowStyle Hidden
        Set-Location ..
        Start-Sleep -Seconds 10
    }
}

# Iniciar sincronización
if (Test-Path "sincronizacion") {
    Write-Status "Iniciando servicio de sincronización..."
    Set-Location sincronizacion
    Start-Process -FilePath "cmd" -ArgumentList "/c", "mvn spring-boot:run > ..\logs\sincronizacion.log 2>&1" -WindowStyle Hidden
    Set-Location ..
}

# Mostrar URLs
Write-Success "Sistema iniciado correctamente!"
Write-Host ""
Write-Host "🌐 URLs de Acceso:" -ForegroundColor Cyan
Write-Host "=================================="
Write-Host "📊 Eureka Dashboard:      http://localhost:8761"
Write-Host "🚪 API Gateway:           http://localhost:8080"
Write-Host "🏥 PatientDataCollector:  http://localhost:8081/swagger-ui.html"
Write-Host "🔬 HealthAnalyzer:        http://localhost:8082/swagger-ui.html"
Write-Host "📱 CareNotifier:          http://localhost:8083/swagger-ui.html"
Write-Host "🔄 Sincronización:        http://localhost:8084"
Write-Host ""
Write-Host "🐘 CockroachDB UI:        http://localhost:8090"
Write-Host "🐰 RabbitMQ Management:   http://localhost:15672 (usuario: medical_admin)"
Write-Host "📈 Prometheus:            http://localhost:9090"
Write-Host "📊 Grafana:               http://localhost:3000 (admin/medical_grafana_admin)"
Write-Host ""
Write-Host "📝 Logs disponibles en el directorio 'logs/'" -ForegroundColor Yellow
Write-Host ""
Write-Success "¡Sistema de Monitoreo Médico Distribuido listo para usar!"

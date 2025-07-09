#!/bin/bash

# Script de inicio para el Sistema de Monitoreo Médico Distribuido
# Autor: Limones & Munarco

echo "🏥 Iniciando Sistema de Monitoreo Médico Distribuido..."
echo "=================================================="

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para imprimir mensajes coloridos
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar si Docker está instalado
if ! command -v docker &> /dev/null; then
    print_error "Docker no está instalado. Por favor instale Docker primero."
    exit 1
fi

# Verificar si Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose no está instalado. Por favor instale Docker Compose primero."
    exit 1
fi

# Verificar si Maven está instalado
if ! command -v mvn &> /dev/null; then
    print_error "Maven no está instalado. Por favor instale Maven primero."
    exit 1
fi

# Verificar si Java está instalado
if ! command -v java &> /dev/null; then
    print_error "Java no está instalado. Por favor instale Java 21+ primero."
    exit 1
fi

print_status "Verificando versión de Java..."
java_version=$(java -version 2>&1 | head -n1 | awk -F '"' '{print $2}')
print_success "Java versión: $java_version"

# Paso 1: Iniciar servicios de infraestructura
print_status "Paso 1: Iniciando servicios de infraestructura (CockroachDB, RabbitMQ, Redis, Prometheus, Grafana)..."
docker-compose up -d

if [ $? -eq 0 ]; then
    print_success "Servicios de infraestructura iniciados correctamente"
else
    print_error "Error al iniciar servicios de infraestructura"
    exit 1
fi

# Esperar a que los servicios estén listos
print_status "Esperando a que los servicios estén listos..."
sleep 30

# Paso 2: Compilar todos los microservicios
print_status "Paso 2: Compilando microservicios..."

microservices=("ms-eureka-server" "ms-gateway-server" "PatientDataCollector" "HealthAnalyzer" "CareNotifier" "sincronizacion")

for service in "${microservices[@]}"; do
    if [ -d "$service" ]; then
        print_status "Compilando $service..."
        cd "$service"
        mvn clean compile -q
        if [ $? -eq 0 ]; then
            print_success "$service compilado correctamente"
        else
            print_error "Error compilando $service"
            cd ..
            exit 1
        fi
        cd ..
    else
        print_warning "Directorio $service no encontrado, saltando..."
    fi
done

# Paso 3: Iniciar microservicios en orden
print_status "Paso 3: Iniciando microservicios en orden..."

# Iniciar Eureka Server primero
print_status "Iniciando Eureka Server..."
cd ms-eureka-server
nohup mvn spring-boot:run > ../logs/eureka.log 2>&1 &
cd ..
sleep 20

# Iniciar API Gateway
print_status "Iniciando API Gateway..."
cd ms-gateway-server
nohup mvn spring-boot:run > ../logs/gateway.log 2>&1 &
cd ..
sleep 15

# Iniciar microservicios de negocio
business_services=("PatientDataCollector" "HealthAnalyzer" "CareNotifier")

for service in "${business_services[@]}"; do
    if [ -d "$service" ]; then
        print_status "Iniciando $service..."
        cd "$service"
        nohup mvn spring-boot:run > "../logs/${service,,}.log" 2>&1 &
        cd ..
        sleep 10
    fi
done

# Iniciar servicio de sincronización
if [ -d "sincronizacion" ]; then
    print_status "Iniciando servicio de sincronización..."
    cd sincronizacion
    nohup mvn spring-boot:run > ../logs/sincronizacion.log 2>&1 &
    cd ..
fi

# Mostrar URLs de acceso
print_success "Sistema iniciado correctamente!"
echo ""
echo "🌐 URLs de Acceso:"
echo "=================================="
echo "📊 Eureka Dashboard:      http://localhost:8761"
echo "🚪 API Gateway:           http://localhost:8080"
echo "🏥 PatientDataCollector:  http://localhost:8081/swagger-ui.html"
echo "🔬 HealthAnalyzer:        http://localhost:8082/swagger-ui.html"
echo "📱 CareNotifier:          http://localhost:8083/swagger-ui.html"
echo "🔄 Sincronización:        http://localhost:8084"
echo ""
echo "🐘 CockroachDB UI:        http://localhost:8090"
echo "🐰 RabbitMQ Management:   http://localhost:15672 (usuario: medical_admin)"
echo "📈 Prometheus:            http://localhost:9090"
echo "📊 Grafana:               http://localhost:3000 (admin/medical_grafana_admin)"
echo ""
echo "📝 Logs disponibles en el directorio 'logs/'"
echo ""
print_success "¡Sistema de Monitoreo Médico Distribuido listo para usar!"

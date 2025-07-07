# Sistema de Monitoreo Médico Distribuido - Limones & Munarco

## 📋 Descripción del Proyecto

Sistema distribuido de monitoreo médico en tiempo real basado en microservicios, desarrollado con Spring Boot, que permite la recolección, análisis y notificación de signos vitales de pacientes.

## 🏗️ Arquitectura del Sistema

### Microservicios Principales

1. **PatientDataCollector** - Recolector de Datos de Pacientes
   - Recolecta signos vitales en tiempo real
   - Valida y procesa datos de dispositivos médicos
   - Puerto: 8081

2. **HealthAnalyzer** - Analizador de Salud
   - Analiza patrones y tendencias en signos vitales
   - Detecta anomalías y evalúa riesgos
   - Genera reportes de análisis
   - Puerto: 8082

3. **CareNotifier** - Notificador de Cuidados
   - Envía notificaciones y alertas médicas
   - Gestiona contactos de emergencia
   - Múltiples canales de notificación (Email, SMS)
   - Puerto: 8083

### Servicios de Infraestructura

4. **ms-eureka-server** - Servidor de Registro de Servicios
   - Registro y descubrimiento de microservicios
   - Puerto: 8761

5. **ms-gateway-server** - API Gateway
   - Punto de entrada único al sistema
   - Enrutamiento y balanceo de carga
   - Puerto: 8080

6. **sincronizacion** - Servicio de Sincronización
   - Sincronización de datos entre microservicios
   - Gestión de estado distribuido

## 🛠️ Tecnologías Utilizadas

- **Framework**: Spring Boot 3.3.6
- **Lenguaje**: Java 21
- **Base de Datos**: CockroachDB (PostgreSQL compatible)
- **Mensajería**: RabbitMQ
- **Registro de Servicios**: Netflix Eureka
- **Gateway**: Spring Cloud Gateway
- **Documentación API**: Swagger/OpenAPI 3
- **Monitoreo**: Micrometer + Prometheus
- **Resiliencia**: Resilience4j (Circuit Breaker, Retry, Bulkhead)
- **Contenedores**: Docker & Docker Compose

## 🚀 Características Principales

### Funcionalidades Médicas
- ✅ Recolección en tiempo real de signos vitales
- ✅ Análisis automático de riesgo médico
- ✅ Detección de anomalías en pacientes
- ✅ Notificaciones automáticas de emergencia
- ✅ Gestión de perfiles de pacientes
- ✅ Reportes y estadísticas médicas

### Características Técnicas
- ✅ Arquitectura orientada a eventos
- ✅ Comunicación asíncrona con RabbitMQ
- ✅ Patrones de resiliencia implementados
- ✅ Monitoreo y métricas en tiempo real
- ✅ Documentación automática de APIs
- ✅ Validación robusta de datos
- ✅ Manejo centralizado de excepciones

## 🏃‍♂️ Cómo Ejecutar el Proyecto

### Prerrequisitos
- Java 21+
- Maven 3.8+
- Docker & Docker Compose
- Git

### 1. Clonar el Repositorio
```bash
git clone https://github.com/jhonsantos94/Limones_Munarco_2p_distribuidas.git
cd Limones_Munarco_2p_distribuidas
```

### 2. Iniciar Servicios de Infraestructura
```bash
# Iniciar CockroachDB y RabbitMQ
docker-compose up -d
```

### 3. Compilar Todos los Microservicios
```bash
# Compilar PatientDataCollector
cd PatientDataCollector
mvn clean install
cd ..

# Compilar HealthAnalyzer
cd HealthAnalyzer
mvn clean install
cd ..

# Compilar CareNotifier
cd CareNotifier
mvn clean install
cd ..

# Compilar Eureka Server
cd ms-eureka-server
mvn clean install
cd ..

# Compilar API Gateway
cd ms-gateway-server
mvn clean install
cd ..

# Compilar Sincronización
cd sincronizacion
mvn clean install
cd ..
```

### 4. Ejecutar en Orden
```bash
# 1. Iniciar Eureka Server
cd ms-eureka-server
mvn spring-boot:run &

# 2. Iniciar API Gateway
cd ../ms-gateway-server
mvn spring-boot:run &

# 3. Iniciar Microservicios de Negocio
cd ../PatientDataCollector
mvn spring-boot:run &

cd ../HealthAnalyzer
mvn spring-boot:run &

cd ../CareNotifier
mvn spring-boot:run &

cd ../sincronizacion
mvn spring-boot:run &
```

## 🌐 URLs de Acceso

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **PatientDataCollector API**: http://localhost:8081/swagger-ui.html
- **HealthAnalyzer API**: http://localhost:8082/swagger-ui.html
- **CareNotifier API**: http://localhost:8083/swagger-ui.html

## 📚 Documentación de APIs

Cada microservicio incluye documentación Swagger/OpenAPI accesible en:
- `http://localhost:{port}/swagger-ui.html`
- `http://localhost:{port}/v3/api-docs`

## 🧪 Ejecutar Pruebas

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar pruebas de un microservicio específico
cd PatientDataCollector
mvn test
```

## 📊 Monitoreo y Métricas

El sistema incluye:
- Métricas de Prometheus en `/actuator/prometheus`
- Health checks en `/actuator/health`
- Información del servicio en `/actuator/info`

## 🔧 Configuración

### Variables de Entorno Principales
```bash
# Base de Datos
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:26257/medical_system
SPRING_DATASOURCE_USERNAME=medical_user
SPRING_DATASOURCE_PASSWORD=medical_pass

# RabbitMQ
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest

# Eureka
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://localhost:8761/eureka/
```

## 🏆 Patrones y Buenas Prácticas Implementadas

- **Circuit Breaker**: Prevención de cascada de fallos
- **Bulkhead**: Aislamiento de recursos críticos
- **Retry**: Reintentos automáticos con backoff exponencial
- **Event-Driven Architecture**: Comunicación asíncrona
- **CQRS**: Separación de comandos y consultas
- **Health Check Pattern**: Monitoreo de salud de servicios
- **API Gateway Pattern**: Punto de entrada unificado
- **Service Registry Pattern**: Descubrimiento automático de servicios

## 👥 Autores

- **Jhon Santos**
- **Limones & Munarco**

## 📝 Licencia

Este proyecto es parte de un examen académico de Sistemas Distribuidos.

## 🤝 Contribuciones

Este es un proyecto académico. Para sugerencias o mejoras, crear un issue en el repositorio.

---

**Desarrollado para el curso de Sistemas Distribuidos - 2do Parcial**

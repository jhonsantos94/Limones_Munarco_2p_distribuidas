# PatientDataCollector Microservice

## Descripción
Microservicio para recolectar y almacenar datos de signos vitales de dispositivos médicos IoT. Forma parte del Sistema de Monitoreo Distribuido para Atención Médica en Tiempo Real.

## Funcionalidades Principales

### 📊 Recolección de Datos
- **POST** `/conjunta/2p/vital-signs`: Recibe datos de dispositivos médicos
- **GET** `/conjunta/2p/vital-signs/{deviceId}`: Historial de lecturas por dispositivo
- **GET** `/conjunta/2p/vital-signs/{deviceId}/range`: Lecturas en rango de fechas

### 🔄 Eventos Publicados
- `NewVitalSignEvent`: Se emite cada vez que se recibe una nueva lectura

### 🏥 Tipos de Signos Vitales Soportados
- `heart-rate`: Frecuencia cardíaca (30-200 bpm)
- `blood-pressure-systolic`: Presión arterial sistólica (70-250 mmHg)
- `blood-pressure-diastolic`: Presión arterial diastólica (40-150 mmHg)
- `oxygen-saturation`: Saturación de oxígeno (50-100%)
- `temperature`: Temperatura corporal (30-45°C)
- `respiratory-rate`: Frecuencia respiratoria (8-40 respiraciones/min)

### ⚡ Características de Resiliencia
- **Almacenamiento local** cuando RabbitMQ está caído
- **Reintento automático** con backoff exponencial (máximo 3 intentos)
- **Recuperación automática** de eventos pendientes cuando el servicio se restaura
- **Validación de rangos médicos** para rechazar lecturas inválidas

## Configuración

### Base de Datos
- **CockroachDB** en producción (puerto 26257)
- **H2** para desarrollo
- Base de datos: `medical_system_db`

### RabbitMQ
- **Exchange**: `medical.events.exchange` (Topic)
- **Queue**: `new.vital.sign.queue`
- **Routing Key**: `vital.sign.new`

### Eureka
- **Puerto**: 8081
- **Nombre del servicio**: `patient-data-collector`
- **Zona por defecto**: `http://localhost:8761/eureka/`

## Formato de Datos

### Entrada (POST /vital-signs)
```json
{
  "deviceId": "D001",
  "type": "heart-rate",
  "value": 135,
  "timestamp": "2024-04-05T12:00:00Z",
  "patientId": "P001",
  "unit": "bpm"
}
```

### Respuesta
```json
{
  "id": 1,
  "deviceId": "D001",
  "type": "heart-rate",
  "value": 135,
  "timestamp": "2024-04-05T12:00:00Z",
  "patientId": "P001",
  "unit": "bpm",
  "status": "WARNING"
}
```

### Evento Publicado
```json
{
  "eventId": "EVT-12345-abcd",
  "deviceId": "D001",
  "type": "heart-rate",
  "value": 135,
  "timestamp": "2024-04-05T12:00:00Z",
  "patientId": "P001",
  "unit": "bpm",
  "status": "WARNING"
}
```

## Estados de Signos Vitales
- **NORMAL**: Valores dentro del rango normal
- **WARNING**: Valores fuera del rango normal pero no críticos
- **CRITICAL**: Valores que requieren atención médica inmediata

## Ejecutar el Servicio

### Prerrequisitos
- Java 21
- Maven 3.6+
- CockroachDB ejecutándose en puerto 26257
- RabbitMQ ejecutándose en puerto 5672
- Eureka Server ejecutándose en puerto 8761

### Comandos
```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar aplicación
mvn spring-boot:run

# Construir JAR
mvn clean package
```

## Endpoints de Monitoreo
- **Health Check**: `/conjunta/2p/vital-signs/health`
- **Actuator**: `/actuator/health`
- **OpenAPI**: `/swagger-ui.html`

## Logs
Los logs incluyen información detallada sobre:
- Recepción de datos de dispositivos
- Publicación de eventos
- Errores de validación
- Estado de RabbitMQ
- Recuperación de eventos

## Integración con otros Microservicios
Este microservicio se integra con:
- **HealthAnalyzer**: Recibe eventos `NewVitalSignEvent`
- **CareNotifier**: Puede recibir alertas críticas
- **Eureka Server**: Para descubrimiento de servicios
- **API Gateway**: Para enrutamiento de requests

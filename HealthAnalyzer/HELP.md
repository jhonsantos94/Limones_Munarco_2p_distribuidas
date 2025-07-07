# Microservicio de Catálogo

Este microservicio gestiona el catálogo de publicaciones en el sistema.

## Funcionalidades principales

* Gestión de items del catálogo (crear, actualizar, eliminar)
* Búsqueda de items por diferentes criterios (tipo, autor, título, etc.)
* Comunicación con otros microservicios a través de RabbitMQ
* Actualización de categorías para items del catálogo

## Getting Started

### Requisitos

* Java 21
* Maven
* RabbitMQ
* Base de datos (H2 para desarrollo, PostgreSQL para producción)

### Configuración

Para entornos de desarrollo, el microservicio utiliza una base de datos H2 en memoria.
Para entornos de producción, configurar las variables de entorno:

```
DB_PASSWORD=tuContraseñaSegura
RABBITMQ_PASSWORD=tuContraseñaRabbitMQ
```

### Referencias

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
* [Spring for RabbitMQ](https://spring.io/guides/gs/messaging-rabbitmq/)
* [Messaging with RabbitMQ](https://spring.io/guides/gs/messaging-rabbitmq/)

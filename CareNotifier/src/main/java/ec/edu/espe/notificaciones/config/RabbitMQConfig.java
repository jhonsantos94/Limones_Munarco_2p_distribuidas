package ec.edu.espe.notificaciones.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue notificacionesCola() {
        return QueueBuilder.durable("notificaciones.cola").build();
    }
    
    @Bean
    public Queue relojCola() {
        return QueueBuilder.durable("reloj.solicitud").build();
    }
    
    @Bean
    public Queue categoriasCola() {
        return QueueBuilder.durable("categorias.cola").build();
    }
}

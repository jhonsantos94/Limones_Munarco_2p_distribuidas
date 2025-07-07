package ec.edu.espe.patientdatacollector.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    // Exchange para eventos médicos
    public static final String MEDICAL_EVENTS_EXCHANGE = "medical.events.exchange";
    
    // Colas para eventos específicos
    public static final String NEW_VITAL_SIGN_QUEUE = "new.vital.sign.queue";
    public static final String CRITICAL_HEART_RATE_QUEUE = "critical.heart.rate.queue";
    public static final String OXYGEN_LEVEL_CRITICAL_QUEUE = "oxygen.level.critical.queue";
    public static final String DEVICE_OFFLINE_QUEUE = "device.offline.queue";
    public static final String SYSTEM_OFFLINE_QUEUE = "system.offline.queue";
    
    // Routing keys
    public static final String NEW_VITAL_SIGN_KEY = "vital.sign.new";
    public static final String CRITICAL_HEART_RATE_KEY = "alert.heart.rate.critical";
    public static final String OXYGEN_CRITICAL_KEY = "alert.oxygen.critical";
    public static final String DEVICE_OFFLINE_KEY = "device.offline";
    public static final String SYSTEM_OFFLINE_KEY = "system.offline";
    
    @Bean
    public TopicExchange medicalEventsExchange() {
        return new TopicExchange(MEDICAL_EVENTS_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue newVitalSignQueue() {
        return QueueBuilder.durable(NEW_VITAL_SIGN_QUEUE).build();
    }
    
    @Bean
    public Queue criticalHeartRateQueue() {
        return QueueBuilder.durable(CRITICAL_HEART_RATE_QUEUE).build();
    }
    
    @Bean
    public Queue oxygenLevelCriticalQueue() {
        return QueueBuilder.durable(OXYGEN_LEVEL_CRITICAL_QUEUE).build();
    }
    
    @Bean
    public Queue deviceOfflineQueue() {
        return QueueBuilder.durable(DEVICE_OFFLINE_QUEUE).build();
    }
    
    @Bean
    public Queue systemOfflineQueue() {
        return QueueBuilder.durable(SYSTEM_OFFLINE_QUEUE).build();
    }
    
    // Bindings
    @Bean
    public Binding newVitalSignBinding() {
        return BindingBuilder.bind(newVitalSignQueue())
                .to(medicalEventsExchange())
                .with(NEW_VITAL_SIGN_KEY);
    }
    
    @Bean
    public Binding criticalHeartRateBinding() {
        return BindingBuilder.bind(criticalHeartRateQueue())
                .to(medicalEventsExchange())
                .with(CRITICAL_HEART_RATE_KEY);
    }
    
    @Bean
    public Binding oxygenCriticalBinding() {
        return BindingBuilder.bind(oxygenLevelCriticalQueue())
                .to(medicalEventsExchange())
                .with(OXYGEN_CRITICAL_KEY);
    }
    
    @Bean
    public Binding deviceOfflineBinding() {
        return BindingBuilder.bind(deviceOfflineQueue())
                .to(medicalEventsExchange())
                .with(DEVICE_OFFLINE_KEY);
    }
    
    @Bean
    public Binding systemOfflineBinding() {
        return BindingBuilder.bind(systemOfflineQueue())
                .to(medicalEventsExchange())
                .with(SYSTEM_OFFLINE_KEY);
    }
    
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}

package ec.edu.espe.healthanalyzer.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    // Exchanges
    public static final String MEDICAL_EVENTS_EXCHANGE = "medical.events.exchange";
    public static final String HEALTH_ALERTS_EXCHANGE = "health.alerts.exchange";
    
    // Queues
    public static final String VITAL_SIGNS_ANALYSIS_QUEUE = "vital.signs.analysis.queue";
    public static final String HEALTH_ALERTS_QUEUE = "health.alerts.queue";
    
    // Routing Keys
    public static final String VITAL_SIGNS_ROUTING_KEY = "vital.signs.new";
    public static final String HEALTH_ALERT_ROUTING_KEY = "health.alert.new";
    
    @Bean
    public TopicExchange medicalEventsExchange() {
        return new TopicExchange(MEDICAL_EVENTS_EXCHANGE, true, false);
    }
    
    @Bean
    public TopicExchange healthAlertsExchange() {
        return new TopicExchange(HEALTH_ALERTS_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue vitalSignsAnalysisQueue() {
        return QueueBuilder.durable(VITAL_SIGNS_ANALYSIS_QUEUE)
                .withArgument("x-dead-letter-exchange", MEDICAL_EVENTS_EXCHANGE + ".dlx")
                .build();
    }
    
    @Bean
    public Queue healthAlertsQueue() {
        return QueueBuilder.durable(HEALTH_ALERTS_QUEUE)
                .withArgument("x-dead-letter-exchange", HEALTH_ALERTS_EXCHANGE + ".dlx")
                .build();
    }
    
    @Bean
    public Binding vitalSignsAnalysisBinding() {
        return BindingBuilder
                .bind(vitalSignsAnalysisQueue())
                .to(medicalEventsExchange())
                .with(VITAL_SIGNS_ROUTING_KEY);
    }
    
    @Bean
    public Binding healthAlertsBinding() {
        return BindingBuilder
                .bind(healthAlertsQueue())
                .to(healthAlertsExchange())
                .with(HEALTH_ALERT_ROUTING_KEY);
    }
    
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }
}

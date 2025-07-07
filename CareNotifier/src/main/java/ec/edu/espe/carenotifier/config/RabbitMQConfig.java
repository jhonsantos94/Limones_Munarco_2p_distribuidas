package ec.edu.espe.carenotifier.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    // Exchange for health alerts
    public static final String HEALTH_ALERTS_EXCHANGE = "health.alerts.exchange";
    public static final String HEALTH_ALERTS_QUEUE = "health.alerts.queue";
    public static final String HEALTH_ALERTS_ROUTING_KEY = "health.alert";

    // Exchange for notification status updates
    public static final String NOTIFICATION_STATUS_EXCHANGE = "notification.status.exchange";
    public static final String NOTIFICATION_STATUS_QUEUE = "notification.status.queue";
    public static final String NOTIFICATION_STATUS_ROUTING_KEY = "notification.status";

    // Dead letter exchange for failed messages
    public static final String DLX_EXCHANGE = "dlx.exchange";
    public static final String DLX_QUEUE = "dlx.queue";

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    // Health Alerts Exchange and Queue
    @Bean
    public TopicExchange healthAlertsExchange() {
        return new TopicExchange(HEALTH_ALERTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue healthAlertsQueue() {
        return QueueBuilder.durable(HEALTH_ALERTS_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dlx")
                .build();
    }

    @Bean
    public Binding healthAlertsBinding() {
        return BindingBuilder
                .bind(healthAlertsQueue())
                .to(healthAlertsExchange())
                .with(HEALTH_ALERTS_ROUTING_KEY);
    }

    // Notification Status Exchange and Queue
    @Bean
    public TopicExchange notificationStatusExchange() {
        return new TopicExchange(NOTIFICATION_STATUS_EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationStatusQueue() {
        return QueueBuilder.durable(NOTIFICATION_STATUS_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dlx")
                .build();
    }

    @Bean
    public Binding notificationStatusBinding() {
        return BindingBuilder
                .bind(notificationStatusQueue())
                .to(notificationStatusExchange())
                .with(NOTIFICATION_STATUS_ROUTING_KEY);
    }

    // Dead Letter Exchange and Queue
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLX_QUEUE).build();
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("dlx");
    }
}

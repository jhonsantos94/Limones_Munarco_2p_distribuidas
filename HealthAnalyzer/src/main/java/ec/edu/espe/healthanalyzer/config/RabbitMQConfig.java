package ec.edu.espe.healthanalyzer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ALERTS_EXCHANGE_NAME        = "health.alerts.exchange";
    public static final String ALERTS_QUEUE_NAME           = "health.alerts.queue";
    public static final String ALERTS_ROUTING_KEY          = "health.alerts.routing.key";
    public static final String DLX_EXCHANGE_NAME           = "health.alerts.exchange.dlx";
    public static final String DLQ_NAME                    = "health.alerts.queue.dlq";
    public static final String DLQ_ROUTING_KEY             = "health.alerts.routing.key.dlq";
    public static final String HEALTH_ALERTS_EXCHANGE_NAME = "health.alerts";
    public static final String HEALTH_ALERTS_ROUTING_KEY   = "health.alert";
    public static final String HEALTH_ALERTS_QUEUE_NAME    = "health.alerts.custom.queue";

    // Alias constants to match service usage
    public static final String HEALTH_ALERTS_EXCHANGE      = HEALTH_ALERTS_EXCHANGE_NAME;
    public static final String HEALTH_ALERT_ROUTING_KEY    = HEALTH_ALERTS_ROUTING_KEY;

    @Bean
    public TopicExchange alertsExchange() {
        return new TopicExchange(ALERTS_EXCHANGE_NAME);
    }

    @Bean
    public Queue alertsQueue() {
        return QueueBuilder.durable(ALERTS_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding alertsBinding() {
        return BindingBuilder.bind(alertsQueue())
                .to(alertsExchange())
                .with(ALERTS_ROUTING_KEY);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(DLX_EXCHANGE_NAME);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_NAME).build();
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public TopicExchange healthAlertsExchange() {
        return new TopicExchange(HEALTH_ALERTS_EXCHANGE_NAME);
    }

    @Bean
    public Queue healthAlertsQueue() {
        return QueueBuilder.durable(HEALTH_ALERTS_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding healthAlertsBinding() {
        return BindingBuilder.bind(healthAlertsQueue())
                .to(healthAlertsExchange())
                .with(HEALTH_ALERTS_ROUTING_KEY);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

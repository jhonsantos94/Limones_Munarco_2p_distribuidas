package ec.edu.espe.healthanalyzer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String VITAL_SIGNS_QUEUE = "vital-signs.queue";

    @Bean
    public Queue vitalSignsQueue() {
        return new Queue(VITAL_SIGNS_QUEUE, true);
    }
}

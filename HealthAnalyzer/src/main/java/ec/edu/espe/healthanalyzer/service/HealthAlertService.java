package ec.edu.espe.healthanalyzer.service;

import ec.edu.espe.healthanalyzer.config.RabbitMQConfig;
import ec.edu.espe.healthanalyzer.dto.HealthAlertEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthAlertService {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void publishHealthAlert(HealthAlertEventDto alert) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.HEALTH_ALERTS_EXCHANGE,
                    RabbitMQConfig.HEALTH_ALERT_ROUTING_KEY,
                    alert
            );
            
            log.info("Health alert published for patient: {} with severity: {}", 
                    alert.getPatientId(), alert.getSeverity());
                    
        } catch (Exception e) {
            log.error("Failed to publish health alert for patient: {}: {}", 
                    alert.getPatientId(), e.getMessage(), e);
        }
    }
    
    // Alias method for consistency with service calls
    public void sendAlert(HealthAlertEventDto alert) {
        publishHealthAlert(alert);
    }
    
    // Process health alert method required by HealthAnalysisService
    public void processHealthAlert(HealthAlertEventDto alert) {
        log.info("Processing health alert for patient: {} with severity: {}", 
                alert.getPatientId(), alert.getSeverity());
        
        // Process the alert (validation, enrichment, etc.)
        publishHealthAlert(alert);
    }
}

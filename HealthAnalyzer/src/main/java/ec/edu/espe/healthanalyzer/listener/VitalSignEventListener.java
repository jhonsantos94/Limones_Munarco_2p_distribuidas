package ec.edu.espe.healthanalyzer.listener;

import ec.edu.espe.healthanalyzer.dto.VitalSignEventDto;
import ec.edu.espe.healthanalyzer.service.HealthAnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VitalSignEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(VitalSignEventListener.class);
    
    @Autowired
    private HealthAnalysisService healthAnalysisService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @RabbitListener(queues = "vital-signs.queue")
    public void handleVitalSignEvent(String message) {
        try {
            logger.info("Received vital sign event: {}", message);
            
            VitalSignEventDto vitalSignEvent = objectMapper.readValue(message, VitalSignEventDto.class);
            
            // Validate the event
            if (vitalSignEvent.getPatientId() == null || vitalSignEvent.getPatientId().trim().isEmpty()) {
                logger.warn("Received vital sign event with null or empty patient ID, skipping processing");
                return;
            }
            
            // Process the vital sign event asynchronously
            healthAnalysisService.processVitalSignEvent(vitalSignEvent);
            
            logger.info("Successfully processed vital sign event for patient: {}", vitalSignEvent.getPatientId());
            
        } catch (Exception e) {
            logger.error("Error processing vital sign event: {}", e.getMessage(), e);
            // In a production environment, you might want to send this to a dead letter queue
            // or implement retry logic
        }
    }
    
    @RabbitListener(queues = "health-analysis.dlq")
    public void handleFailedMessages(String message) {
        logger.error("Processing failed message from dead letter queue: {}", message);
        
        // Here you could implement:
        // - Store failed messages for manual review
        // - Send alerts to administrators
        // - Attempt manual processing
        // - Log to a separate monitoring system
        
        try {
            // For now, just log the failed message for review
            logger.info("Failed message logged for manual review: {}", message);
        } catch (Exception e) {
            logger.error("Error handling failed message: {}", e.getMessage(), e);
        }
    }
}

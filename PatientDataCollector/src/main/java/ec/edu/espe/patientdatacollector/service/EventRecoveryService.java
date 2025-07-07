package ec.edu.espe.patientdatacollector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.espe.patientdatacollector.config.RabbitMQConfig;
import ec.edu.espe.patientdatacollector.dto.NewVitalSignEventDto;
import ec.edu.espe.patientdatacollector.model.EventStorage;
import ec.edu.espe.patientdatacollector.repository.EventStorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventRecoveryService {
    
    private final EventStorageRepository eventStorageRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final ConnectionFactory connectionFactory;
    
    private static final int MAX_RETRY_ATTEMPTS = 3;
    
    @Scheduled(fixedDelay = 30000) // Cada 30 segundos
    public void processStoredEvents() {
        if (!isRabbitMQAvailable()) {
            log.debug("RabbitMQ still unavailable. Skipping event recovery.");
            return;
        }
        
        List<EventStorage> unprocessedEvents = eventStorageRepository.findByProcessedFalseOrderByCreatedAt();
        
        if (!unprocessedEvents.isEmpty()) {
            log.info("RabbitMQ is back online. Processing {} stored events", unprocessedEvents.size());
            
            for (EventStorage event : unprocessedEvents) {
                processStoredEvent(event);
            }
        }
    }
    
    private void processStoredEvent(EventStorage eventStorage) {
        try {
            if (eventStorage.getRetryCount() >= MAX_RETRY_ATTEMPTS) {
                log.warn("Max retry attempts reached for event: {}. Marking as failed.", eventStorage.getEventId());
                // Aquí podrías mover a una cola de eventos fallidos o notificar
                return;
            }
            
            Object eventData = deserializeEventData(eventStorage.getEventType(), eventStorage.getEventData());
            
            switch (eventStorage.getEventType()) {
                case "NewVitalSignEvent":
                    NewVitalSignEventDto vitalSignEvent = (NewVitalSignEventDto) eventData;
                    rabbitTemplate.convertAndSend(
                        RabbitMQConfig.MEDICAL_EVENTS_EXCHANGE,
                        RabbitMQConfig.NEW_VITAL_SIGN_KEY,
                        vitalSignEvent
                    );
                    break;
                // Agregar más tipos de eventos según sea necesario
                default:
                    log.warn("Unknown event type: {}", eventStorage.getEventType());
                    return;
            }
            
            // Marcar como procesado
            eventStorage.setProcessed(true);
            eventStorage.setLastRetryAt(LocalDateTime.now());
            eventStorageRepository.save(eventStorage);
            
            log.info("Successfully processed stored event: {}", eventStorage.getEventId());
            
        } catch (Exception e) {
            log.error("Failed to process stored event: {}. Incrementing retry count.", eventStorage.getEventId(), e);
            
            eventStorage.setRetryCount(eventStorage.getRetryCount() + 1);
            eventStorage.setLastRetryAt(LocalDateTime.now());
            eventStorageRepository.save(eventStorage);
        }
    }
    
    private Object deserializeEventData(String eventType, String eventDataJson) throws Exception {
        switch (eventType) {
            case "NewVitalSignEvent":
                return objectMapper.readValue(eventDataJson, NewVitalSignEventDto.class);
            default:
                throw new IllegalArgumentException("Unknown event type: " + eventType);
        }
    }
    
    private boolean isRabbitMQAvailable() {
        try {
            connectionFactory.createConnection().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Scheduled(cron = "0 0 2 * * ?") // Todos los días a las 2 AM
    public void cleanupOldProcessedEvents() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        List<EventStorage> oldEvents = eventStorageRepository.findByProcessedTrueAndRetryCountLessThan(MAX_RETRY_ATTEMPTS);
        
        oldEvents.stream()
                .filter(event -> event.getLastRetryAt() != null && event.getLastRetryAt().isBefore(cutoff))
                .forEach(event -> {
                    eventStorageRepository.delete(event);
                    log.debug("Cleaned up old processed event: {}", event.getEventId());
                });
        
        log.info("Cleanup completed. Removed old processed events.");
    }
}

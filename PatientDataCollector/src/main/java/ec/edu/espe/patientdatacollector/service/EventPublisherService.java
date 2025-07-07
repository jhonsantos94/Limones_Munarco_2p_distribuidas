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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherService {
    
    private final RabbitTemplate rabbitTemplate;
    private final EventStorageRepository eventStorageRepository;
    private final ObjectMapper objectMapper;
    private final ConnectionFactory connectionFactory;
    
    @Async
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void publishNewVitalSignEvent(NewVitalSignEventDto eventDto) {
        try {
            // Verificar conexión a RabbitMQ
            if (isRabbitMQAvailable()) {
                // Publicar directamente
                rabbitTemplate.convertAndSend(
                    RabbitMQConfig.MEDICAL_EVENTS_EXCHANGE,
                    RabbitMQConfig.NEW_VITAL_SIGN_KEY,
                    eventDto
                );
                
                log.info("Published NewVitalSignEvent: {}", eventDto.getEventId());
                
                // Marcar como procesado si estaba en storage local
                markEventAsProcessed(eventDto.getEventId());
                
            } else {
                // Almacenar localmente si RabbitMQ no está disponible
                storeEventLocally("NewVitalSignEvent", eventDto);
                log.warn("RabbitMQ unavailable. Event stored locally: {}", eventDto.getEventId());
            }
            
        } catch (Exception e) {
            log.error("Failed to publish event: {}. Storing locally.", eventDto.getEventId(), e);
            storeEventLocally("NewVitalSignEvent", eventDto);
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
    
    private void storeEventLocally(String eventType, Object eventData) {
        try {
            String eventDataJson = objectMapper.writeValueAsString(eventData);
            
            EventStorage eventStorage = new EventStorage();
            if (eventData instanceof NewVitalSignEventDto) {
                eventStorage.setEventId(((NewVitalSignEventDto) eventData).getEventId());
            }
            eventStorage.setEventType(eventType);
            eventStorage.setEventData(eventDataJson);
            eventStorage.setCreatedAt(LocalDateTime.now());
            eventStorage.setProcessed(false);
            eventStorage.setRetryCount(0);
            
            eventStorageRepository.save(eventStorage);
            
        } catch (Exception e) {
            log.error("Failed to store event locally", e);
        }
    }
    
    private void markEventAsProcessed(String eventId) {
        try {
            eventStorageRepository.findByEventId(eventId)
                .ifPresent(event -> {
                    event.setProcessed(true);
                    event.setLastRetryAt(LocalDateTime.now());
                    eventStorageRepository.save(event);
                });
        } catch (Exception e) {
            log.error("Failed to mark event as processed: {}", eventId, e);
        }
    }
}

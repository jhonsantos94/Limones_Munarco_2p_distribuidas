package ec.edu.espe.carenotifier.listener;

import ec.edu.espe.carenotifier.dto.HealthAlertEventDto;
import ec.edu.espe.carenotifier.service.CareNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HealthAlertEventListener {

    private final CareNotificationService careNotificationService;

    @RabbitListener(queues = "${app.rabbitmq.queue.health-alerts:health.alerts.queue}")
    public void handleHealthAlert(HealthAlertEventDto healthAlertEvent) {
        try {
            log.info("Received health alert for patient: {}", healthAlertEvent.getPatientId());
            careNotificationService.processHealthAlert(healthAlertEvent);
            log.info("Successfully processed health alert for patient: {}", healthAlertEvent.getPatientId());
        } catch (Exception e) {
            log.error("Error processing health alert for patient: {}", healthAlertEvent.getPatientId(), e);
            // The message will be sent to DLX for manual handling
            throw e;
        }
    }
}

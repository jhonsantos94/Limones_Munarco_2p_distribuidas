package ec.edu.espe.carenotifier.service;

import ec.edu.espe.carenotifier.model.CareNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsNotificationService {

    @Value("${app.notification.sms.enabled:false}")
    private boolean smsEnabled;

    public boolean sendSms(CareNotification notification) {
        try {
            if (!smsEnabled) {
                log.info("SMS service is disabled. Simulating SMS for notification: {}", notification.getId());
                return true; // Simulate successful SMS
            }

            // In a real implementation, you would integrate with SMS providers like Twilio, AWS SNS, etc.
            String message = buildSmsMessage(notification);
            
            // Simulate SMS sending
            log.info("SMS sent to {} for notification: {}", 
                    notification.getRecipientPhone(), notification.getId());
            log.debug("SMS content: {}", message);
            
            return true;

        } catch (Exception e) {
            log.error("Failed to send SMS for notification: {}", notification.getId(), e);
            return false;
        }
    }

    private String buildSmsMessage(CareNotification notification) {
        StringBuilder message = new StringBuilder();
        message.append("HEALTH ALERT: ");
        message.append(notification.getTitle());
        message.append(" - ");
        message.append(notification.getMessage());
        
        if (message.length() > 160) {
            message.setLength(157);
            message.append("...");
        }
        
        return message.toString();
    }
}

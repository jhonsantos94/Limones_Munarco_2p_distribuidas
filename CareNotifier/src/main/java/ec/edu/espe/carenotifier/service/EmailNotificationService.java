package ec.edu.espe.carenotifier.service;

import ec.edu.espe.carenotifier.model.CareNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${app.notification.email.from:noreply@carenotifier.com}")
    private String fromEmail;

    public boolean sendEmail(CareNotification notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(notification.getRecipientEmail());
            message.setSubject(notification.getTitle());
            message.setText(buildEmailBody(notification));

            mailSender.send(message);
            log.info("Email sent successfully for notification: {}", notification.getId());
            return true;

        } catch (Exception e) {
            log.error("Failed to send email for notification: {}", notification.getId(), e);
            return false;
        }
    }

    private String buildEmailBody(CareNotification notification) {
        StringBuilder body = new StringBuilder();
        body.append("Dear Patient/Family Member,\n\n");
        body.append(notification.getMessage());
        body.append("\n\n");
        body.append("Priority: ").append(notification.getPriority().name()).append("\n");
        body.append("Type: ").append(notification.getType().name()).append("\n");
        body.append("Time: ").append(notification.getCreatedAt()).append("\n\n");
        body.append("This is an automated notification from your healthcare team.\n");
        body.append("If this is an emergency, please contact emergency services immediately.\n\n");
        body.append("Best regards,\n");
        body.append("Your Healthcare Team");
        
        return body.toString();
    }
}

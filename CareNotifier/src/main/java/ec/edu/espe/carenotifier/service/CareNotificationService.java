package ec.edu.espe.carenotifier.service;

import ec.edu.espe.carenotifier.dto.HealthAlertEventDto;
import ec.edu.espe.carenotifier.dto.NotificationRequestDto;
import ec.edu.espe.carenotifier.dto.NotificationResponseDto;
import ec.edu.espe.carenotifier.exception.NotificationException;
import ec.edu.espe.carenotifier.model.*;
import ec.edu.espe.carenotifier.repository.CareNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class CareNotificationService {

    private final CareNotificationRepository notificationRepository;
    private final EmailNotificationService emailNotificationService;
    private final SmsNotificationService smsNotificationService;
    private final EmergencyContactService emergencyContactService;

    @Transactional
    public NotificationResponseDto createNotification(NotificationRequestDto request) {
        try {
            CareNotification notification = mapToEntity(request);
            notification = notificationRepository.save(notification);
            
            // Send notification asynchronously
            sendNotificationAsync(notification);
            
            return mapToResponse(notification);
        } catch (Exception e) {
            log.error("Error creating notification for patient: {}", request.getPatientId(), e);
            throw new NotificationException("Failed to create notification", e);
        }
    }

    @Async
    public CompletableFuture<Void> sendNotificationAsync(CareNotification notification) {
        try {
            sendNotification(notification);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error sending notification: {}", notification.getId(), e);
            handleNotificationFailure(notification, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Transactional
    public void processHealthAlert(HealthAlertEventDto alertEvent) {
        try {
            log.info("Processing health alert for patient: {}", alertEvent.getPatientId());
            
            // Create notification from health alert
            CareNotification notification = createNotificationFromAlert(alertEvent);
            notification = notificationRepository.save(notification);
            
            // Get emergency contacts if priority is critical or high
            if (alertEvent.getPriority() == PriorityLevel.CRITICAL || 
                alertEvent.getPriority() == PriorityLevel.HIGH) {
                
                List<EmergencyContact> contacts = emergencyContactService
                    .getActiveContactsByPatientId(alertEvent.getPatientId());
                
                // Send to emergency contacts
                for (EmergencyContact contact : contacts) {
                    createEmergencyNotification(notification, contact);
                }
            }
            
            // Send primary notification
            sendNotification(notification);
            
        } catch (Exception e) {
            log.error("Error processing health alert for patient: {}", alertEvent.getPatientId(), e);
            throw new NotificationException("Failed to process health alert", e);
        }
    }

    private void sendNotification(CareNotification notification) {
        try {
            boolean emailSent = false;
            boolean smsSent = false;
            
            // Send email if email address is provided
            if (notification.getRecipientEmail() != null && !notification.getRecipientEmail().isEmpty()) {
                emailSent = emailNotificationService.sendEmail(notification);
            }
            
            // Send SMS if phone number is provided
            if (notification.getRecipientPhone() != null && !notification.getRecipientPhone().isEmpty()) {
                smsSent = smsNotificationService.sendSms(notification);
            }
            
            // Update notification status
            if (emailSent || smsSent) {
                updateNotificationStatus(notification.getId(), NotificationStatus.SENT, LocalDateTime.now());
            } else {
                updateNotificationStatus(notification.getId(), NotificationStatus.FAILED, null);
            }
            
        } catch (Exception e) {
            log.error("Error sending notification: {}", notification.getId(), e);
            handleNotificationFailure(notification, e.getMessage());
        }
    }

    @Transactional
    public void updateNotificationStatus(Long notificationId, NotificationStatus status, LocalDateTime timestamp) {
        Optional<CareNotification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            CareNotification notification = notificationOpt.get();
            notification.setStatus(status);
            
            switch (status) {
                case SENT -> notification.setSentAt(timestamp);
                case DELIVERED -> notification.setDeliveredAt(timestamp);
                case READ -> notification.setReadAt(timestamp);
            }
            
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void handleNotificationFailure(CareNotification notification, String errorMessage) {
        notification.setStatus(NotificationStatus.FAILED);
        notification.setErrorMessage(errorMessage);
        notification.setRetryCount(notification.getRetryCount() + 1);
        notificationRepository.save(notification);
        
        // Schedule retry if retry count is less than max attempts
        if (notification.getRetryCount() < 3) {
            // This would be handled by a scheduled task
            log.info("Scheduling retry for notification: {}", notification.getId());
        }
    }

    public List<CareNotification> getNotificationsByPatient(String patientId) {
        return notificationRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    public List<CareNotification> getPendingNotifications() {
        return notificationRepository.findByStatusOrderByCreatedAtAsc(NotificationStatus.PENDING);
    }

    private CareNotification mapToEntity(NotificationRequestDto request) {
        CareNotification notification = new CareNotification();
        notification.setPatientId(request.getPatientId());
        notification.setType(request.getType());
        notification.setPriority(request.getPriority());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setRecipientEmail(request.getRecipientEmail());
        notification.setRecipientPhone(request.getRecipientPhone());
        notification.setHealthcareProviderId(request.getHealthcareProviderId());
        notification.setMetadata(request.getMetadata());
        notification.setStatus(NotificationStatus.PENDING);
        return notification;
    }

    private NotificationResponseDto mapToResponse(CareNotification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .patientId(notification.getPatientId())
                .type(notification.getType())
                .priority(notification.getPriority())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .build();
    }

    private CareNotification createNotificationFromAlert(HealthAlertEventDto alertEvent) {
        CareNotification notification = new CareNotification();
        notification.setPatientId(alertEvent.getPatientId());
        notification.setType(alertEvent.getType());
        notification.setPriority(alertEvent.getPriority());
        notification.setTitle(alertEvent.getTitle());
        notification.setMessage(alertEvent.getMessage());
        notification.setRecipientEmail(alertEvent.getRecipientEmail());
        notification.setRecipientPhone(alertEvent.getRecipientPhone());
        notification.setHealthcareProviderId(alertEvent.getHealthcareProviderId());
        notification.setMetadata(alertEvent.getMetadata());
        notification.setStatus(NotificationStatus.PENDING);
        return notification;
    }

    private void createEmergencyNotification(CareNotification originalNotification, EmergencyContact contact) {
        CareNotification emergencyNotification = new CareNotification();
        emergencyNotification.setPatientId(originalNotification.getPatientId());
        emergencyNotification.setType(NotificationType.EMERGENCY_NOTIFICATION);
        emergencyNotification.setPriority(PriorityLevel.CRITICAL);
        emergencyNotification.setTitle("EMERGENCY: " + originalNotification.getTitle());
        emergencyNotification.setMessage("Emergency notification for " + contact.getContactName() + 
                " regarding patient. Original message: " + originalNotification.getMessage());
        emergencyNotification.setRecipientEmail(contact.getEmail());
        emergencyNotification.setRecipientPhone(contact.getPhoneNumber());
        emergencyNotification.setEmergencyContactId(contact.getId().toString());
        emergencyNotification.setStatus(NotificationStatus.PENDING);
        
        CareNotification saved = notificationRepository.save(emergencyNotification);
        sendNotificationAsync(saved);
    }
}

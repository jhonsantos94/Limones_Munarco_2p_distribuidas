package ec.edu.espe.carenotifier.dto;

import ec.edu.espe.carenotifier.model.NotificationType;
import ec.edu.espe.carenotifier.model.PriorityLevel;
import ec.edu.espe.carenotifier.model.NotificationStatus;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {

    private Long id;
    private String patientId;
    private NotificationType type;
    private PriorityLevel priority;
    private String title;
    private String message;
    private String recipientEmail;
    private String recipientPhone;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private Integer retryCount;
    private String errorMessage;
}

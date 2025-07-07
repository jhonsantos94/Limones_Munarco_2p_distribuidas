package ec.edu.espe.carenotifier.dto;

import ec.edu.espe.carenotifier.model.NotificationType;
import ec.edu.espe.carenotifier.model.PriorityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthAlertEventDto {

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @NotNull(message = "Priority level is required")
    private PriorityLevel priority;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @Email(message = "Invalid email format")
    private String recipientEmail;

    private String recipientPhone;

    private String healthcareProviderId;

    private String emergencyContactId;

    private String riskLevel;

    private String anomalies;

    private String recommendations;

    private String metadata;

    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
}

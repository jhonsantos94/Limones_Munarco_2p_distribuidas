package ec.edu.espe.healthanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthAlertEventDto {
    
    private String eventId;
    private String patientId;
    private String alertType; // CRITICAL_CONDITION, TREND_ALERT, MEDICATION_REMINDER
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private String message;
    private String recommendedAction;
    private String analysisId;
    private LocalDateTime timestamp;
    private String riskLevel;
    private String anomalies;
    private String recommendations;
}

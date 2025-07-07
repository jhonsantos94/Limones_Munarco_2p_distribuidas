package ec.edu.espe.healthanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VitalSignEventDto {
    
    private String eventId;
    
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
    
    // Vital sign measurements
    @DecimalMin(value = "0.0", message = "Heart rate must be positive")
    @DecimalMax(value = "300.0", message = "Heart rate is too high")
    private Double heartRate;
    
    @DecimalMin(value = "0.0", message = "Systolic blood pressure must be positive")
    @DecimalMax(value = "300.0", message = "Systolic blood pressure is too high")
    private Double bloodPressureSystolic;
    
    @DecimalMin(value = "0.0", message = "Diastolic blood pressure must be positive")
    @DecimalMax(value = "200.0", message = "Diastolic blood pressure is too high")
    private Double bloodPressureDiastolic;
    
    @DecimalMin(value = "30.0", message = "Temperature is too low")
    @DecimalMax(value = "45.0", message = "Temperature is too high")
    private Double temperature;
    
    @DecimalMin(value = "0.0", message = "Oxygen saturation must be positive")
    @DecimalMax(value = "100.0", message = "Oxygen saturation cannot exceed 100%")
    private Double oxygenSaturation;
    
    @DecimalMin(value = "0.0", message = "Respiratory rate must be positive")
    @DecimalMax(value = "100.0", message = "Respiratory rate is too high")
    private Double respiratoryRate;
    
    // Device information
    private String deviceId;
    private String deviceType;
    
    // Additional metadata
    private String notes;
    private String location;
}

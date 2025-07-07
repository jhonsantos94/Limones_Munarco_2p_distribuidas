package ec.edu.espe.patientdatacollector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VitalSignRequestDto {
    
    @NotBlank(message = "Device ID is required")
    private String deviceId;
    
    @NotBlank(message = "Type is required")
    @Pattern(regexp = "heart-rate|blood-pressure-systolic|blood-pressure-diastolic|oxygen-saturation|temperature|respiratory-rate", 
             message = "Invalid vital sign type")
    private String type;
    
    @NotNull(message = "Value is required")
    @DecimalMin(value = "0.0", message = "Value must be positive")
    @DecimalMax(value = "300.0", message = "Value too high")
    private Double value;
    
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
    
    private String patientId;
    private String unit;
}

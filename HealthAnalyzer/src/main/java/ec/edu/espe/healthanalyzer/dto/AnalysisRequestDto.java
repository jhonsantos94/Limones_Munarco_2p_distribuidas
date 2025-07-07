package ec.edu.espe.healthanalyzer.dto;

import ec.edu.espe.healthanalyzer.model.PatientHealthProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequestDto {
    
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    @NotBlank(message = "Analysis type is required")
    @Pattern(regexp = "TREND_ANALYSIS|ANOMALY_DETECTION|RISK_ASSESSMENT|VITAL_SIGNS", 
             message = "Invalid analysis type")
    private String analysisType;
    
    @NotBlank(message = "Data source is required")
    @Pattern(regexp = "VITAL_SIGNS|SYMPTOMS|MEDICATION|PATIENT_DATA_COLLECTOR", 
             message = "Invalid data source")
    private String dataSource;
    
    @Min(value = 1, message = "Time range must be at least 1 hour")
    @Max(value = 720, message = "Time range cannot exceed 720 hours (30 days)")
    private Integer timeRangeHours = 24; // Default to 24 hours if not specified
    
    @NotNull(message = "Vital sign event data is required")
    private VitalSignEventDto vitalSignEvent;
    
    private PatientHealthProfile patientProfile; // Optional, will be created if not provided
}

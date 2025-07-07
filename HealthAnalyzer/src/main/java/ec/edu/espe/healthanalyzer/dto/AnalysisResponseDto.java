package ec.edu.espe.healthanalyzer.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResponseDto {
    
    private Long id;
    
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    @NotBlank(message = "Analysis type is required")
    private String analysisType;
    
    private String dataSource;
    private String analysisResult;
    private String riskLevel;
    
    @DecimalMin(value = "0.0", message = "Confidence score must be positive")
    @DecimalMax(value = "1.0", message = "Confidence score cannot exceed 1.0")
    private Double confidenceScore;
    
    private List<String> recommendations;
    private List<String> anomalies;
    
    @NotNull(message = "Analysis timestamp is required")
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private LocalDateTime analysisTimestamp;
    private String status;
}

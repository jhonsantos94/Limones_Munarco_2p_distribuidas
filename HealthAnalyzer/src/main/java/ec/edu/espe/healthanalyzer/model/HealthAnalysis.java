package ec.edu.espe.healthanalyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "health_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthAnalysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "patient_id", nullable = false)
    private String patientId;
    
    @Column(name = "analysis_type", nullable = false)
    private String analysisType; // TREND_ANALYSIS, ANOMALY_DETECTION, RISK_ASSESSMENT
    
    @Column(name = "data_source", nullable = false)
    private String dataSource; // VITAL_SIGNS, SYMPTOMS, MEDICATION
    
    @Column(name = "analysis_result", columnDefinition = "TEXT")
    private String analysisResult; // JSON con los resultados
    
    @Column(name = "risk_level")
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
    
    @Column(name = "confidence_score")
    private Double confidenceScore; // 0.0 to 1.0
    
    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations; // JSON con recomendaciones
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "status")
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Column(name = "anomalies", columnDefinition = "TEXT")
    private String anomalies; // JSON with anomalies detected
}

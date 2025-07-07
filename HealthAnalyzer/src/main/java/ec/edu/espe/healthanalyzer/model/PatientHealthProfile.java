package ec.edu.espe.healthanalyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "patient_health_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientHealthProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "patient_id", nullable = false, unique = true)
    private String patientId;
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "gender")
    private String gender; // MALE, FEMALE, OTHER
    
    @Column(name = "medical_conditions", columnDefinition = "TEXT")
    private String medicalConditions; // JSON array de condiciones
    
    @Column(name = "medications", columnDefinition = "TEXT")
    private String medications; // JSON array de medicamentos
    
    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies; // JSON array de alergias
    
    @Column(name = "baseline_vitals", columnDefinition = "TEXT")
    private String baselineVitals; // JSON con valores basales normales
    
    @Column(name = "risk_factors", columnDefinition = "TEXT")
    private String riskFactors; // JSON array de factores de riesgo
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "average_heart_rate")
    private Double averageHeartRate;
    
    @Column(name = "average_blood_pressure")
    private String averageBloodPressure;
    
    @Column(name = "average_temperature")
    private Double averageTemperature;
    
    @Column(name = "last_vital_signs_update")
    private LocalDateTime lastVitalSignsUpdate;
    
    // Additional statistics fields
    @Column(name = "reading_count")
    private Integer readingCount = 0;
    
    @Column(name = "heart_rate_avg")
    private Double heartRateAvg;
    
    @Column(name = "blood_pressure_systolic_avg")
    private Double bloodPressureSystolicAvg;
    
    @Column(name = "blood_pressure_diastolic_avg")
    private Double bloodPressureDiastolicAvg;
    
    @Column(name = "temperature_avg")
    private Double temperatureAvg;
    
    @Column(name = "respiratory_rate_avg")
    private Double respiratoryRateAvg;
    
    @Column(name = "oxygen_saturation_avg")
    private Double oxygenSaturationAvg;
    
    @Column(name = "trends", columnDefinition = "TEXT")
    private String trends; // Analysis trends
    
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
}

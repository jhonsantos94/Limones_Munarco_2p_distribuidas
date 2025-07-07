package ec.edu.espe.patientdatacollector.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "vital_signs")
public class VitalSigns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @PositiveOrZero(message = "Heart rate must be positive or zero")
    @Column(name = "heart_rate")
    private Integer heartRate; // beats per minute

    @PositiveOrZero(message = "Systolic pressure must be positive or zero")
    @Column(name = "systolic_pressure")
    private Integer systolicPressure; // mmHg

    @PositiveOrZero(message = "Diastolic pressure must be positive or zero")
    @Column(name = "diastolic_pressure")
    private Integer diastolicPressure; // mmHg

    @PositiveOrZero(message = "Body temperature must be positive or zero")
    @Column(name = "body_temperature", precision = 4, scale = 1)
    private BigDecimal bodyTemperature; // Celsius

    @PositiveOrZero(message = "Respiratory rate must be positive or zero")
    @Column(name = "respiratory_rate")
    private Integer respiratoryRate; // breaths per minute

    @PositiveOrZero(message = "Oxygen saturation must be positive or zero")
    @Column(name = "oxygen_saturation", precision = 5, scale = 2)
    private BigDecimal oxygenSaturation; // percentage

    @PositiveOrZero(message = "Blood glucose must be positive or zero")
    @Column(name = "blood_glucose", precision = 6, scale = 2)
    private BigDecimal bloodGlucose; // mg/dL

    @PositiveOrZero(message = "Weight must be positive or zero")
    @Column(name = "weight", precision = 5, scale = 2)
    private BigDecimal weight; // kg

    @PositiveOrZero(message = "Height must be positive or zero")
    @Column(name = "height", precision = 5, scale = 2)
    private BigDecimal height; // cm

    @Column(name = "pain_level")
    private Integer painLevel; // 0-10 scale

    @Column(name = "notes", length = 1000)
    private String notes;

    @NotNull(message = "Measurement timestamp is required")
    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @NotNull(message = "Measurement type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_type", nullable = false)
    private MeasurementType measurementType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    @Column(name = "version")
    private Long version;

    // Constructors
    public VitalSigns() {}

    public VitalSigns(Patient patient, LocalDateTime measuredAt, MeasurementType measurementType) {
        this.patient = patient;
        this.measuredAt = measuredAt;
        this.measurementType = measurementType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Integer getSystolicPressure() {
        return systolicPressure;
    }

    public void setSystolicPressure(Integer systolicPressure) {
        this.systolicPressure = systolicPressure;
    }

    public Integer getDiastolicPressure() {
        return diastolicPressure;
    }

    public void setDiastolicPressure(Integer diastolicPressure) {
        this.diastolicPressure = diastolicPressure;
    }

    public BigDecimal getBodyTemperature() {
        return bodyTemperature;
    }

    public void setBodyTemperature(BigDecimal bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }

    public Integer getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(Integer respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public BigDecimal getOxygenSaturation() {
        return oxygenSaturation;
    }

    public void setOxygenSaturation(BigDecimal oxygenSaturation) {
        this.oxygenSaturation = oxygenSaturation;
    }

    public BigDecimal getBloodGlucose() {
        return bloodGlucose;
    }

    public void setBloodGlucose(BigDecimal bloodGlucose) {
        this.bloodGlucose = bloodGlucose;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public Integer getPainLevel() {
        return painLevel;
    }

    public void setPainLevel(Integer painLevel) {
        this.painLevel = painLevel;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }

    public void setMeasuredAt(LocalDateTime measuredAt) {
        this.measuredAt = measuredAt;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public MeasurementType getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(MeasurementType measurementType) {
        this.measurementType = measurementType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // Business methods
    public BigDecimal calculateBMI() {
        if (weight != null && height != null && height.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightInMeters = height.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            return weight.divide(heightInMeters.multiply(heightInMeters), 2, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }

    public String getBloodPressureString() {
        if (systolicPressure != null && diastolicPressure != null) {
            return systolicPressure + "/" + diastolicPressure;
        }
        return null;
    }

    public boolean hasAbnormalVitals() {
        return (heartRate != null && (heartRate < 60 || heartRate > 100)) ||
               (systolicPressure != null && (systolicPressure < 90 || systolicPressure > 140)) ||
               (diastolicPressure != null && (diastolicPressure < 60 || diastolicPressure > 90)) ||
               (bodyTemperature != null && (bodyTemperature.compareTo(BigDecimal.valueOf(36.1)) < 0 || 
                                           bodyTemperature.compareTo(BigDecimal.valueOf(37.2)) > 0)) ||
               (oxygenSaturation != null && oxygenSaturation.compareTo(BigDecimal.valueOf(95)) < 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VitalSigns that = (VitalSigns) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(patient, that.patient) &&
               Objects.equals(measuredAt, that.measuredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, patient, measuredAt);
    }

    @Override
    public String toString() {
        return "VitalSigns{" +
                "id=" + id +
                ", patient=" + (patient != null ? patient.getId() : null) +
                ", heartRate=" + heartRate +
                ", bloodPressure=" + getBloodPressureString() +
                ", bodyTemperature=" + bodyTemperature +
                ", oxygenSaturation=" + oxygenSaturation +
                ", measuredAt=" + measuredAt +
                ", measurementType=" + measurementType +
                '}';
    }
}

package ec.edu.espe.patientdatacollector.dto;

import ec.edu.espe.patientdatacollector.model.MeasurementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VitalSignsResponseDTO {

    private Long id;
    private Long patientId;
    private String patientName;
    private Integer heartRate;
    private Integer systolicPressure;
    private Integer diastolicPressure;
    private BigDecimal bodyTemperature;
    private Integer respiratoryRate;
    private BigDecimal oxygenSaturation;
    private BigDecimal bloodGlucose;
    private BigDecimal weight;
    private BigDecimal height;
    private Integer painLevel;
    private String notes;
    private LocalDateTime measuredAt;
    private String deviceId;
    private MeasurementType measurementType;
    private LocalDateTime createdAt;
    private Long version;

    // Additional calculated fields
    private BigDecimal bmi;
    private String bloodPressure;
    private boolean hasAbnormalVitals;

    // Constructors
    public VitalSignsResponseDTO() {
        // Default constructor for JSON serialization
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
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

    public BigDecimal getBmi() {
        return bmi;
    }

    public void setBmi(BigDecimal bmi) {
        this.bmi = bmi;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public boolean isHasAbnormalVitals() {
        return hasAbnormalVitals;
    }

    public void setHasAbnormalVitals(boolean hasAbnormalVitals) {
        this.hasAbnormalVitals = hasAbnormalVitals;
    }

    @Override
    public String toString() {
        return "VitalSignsResponseDTO{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", patientName='" + patientName + '\'' +
                ", heartRate=" + heartRate +
                ", bloodPressure='" + bloodPressure + '\'' +
                ", bodyTemperature=" + bodyTemperature +
                ", oxygenSaturation=" + oxygenSaturation +
                ", measuredAt=" + measuredAt +
                ", measurementType=" + measurementType +
                ", hasAbnormalVitals=" + hasAbnormalVitals +
                '}';
    }
}

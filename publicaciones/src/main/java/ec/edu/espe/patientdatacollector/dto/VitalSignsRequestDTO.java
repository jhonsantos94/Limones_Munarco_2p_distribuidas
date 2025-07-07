package ec.edu.espe.patientdatacollector.dto;

import ec.edu.espe.patientdatacollector.model.MeasurementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VitalSignsRequestDTO {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @PositiveOrZero(message = "Heart rate must be positive or zero")
    private Integer heartRate;

    @PositiveOrZero(message = "Systolic pressure must be positive or zero")
    private Integer systolicPressure;

    @PositiveOrZero(message = "Diastolic pressure must be positive or zero")
    private Integer diastolicPressure;

    @PositiveOrZero(message = "Body temperature must be positive or zero")
    private BigDecimal bodyTemperature;

    @PositiveOrZero(message = "Respiratory rate must be positive or zero")
    private Integer respiratoryRate;

    @PositiveOrZero(message = "Oxygen saturation must be positive or zero")
    private BigDecimal oxygenSaturation;

    @PositiveOrZero(message = "Blood glucose must be positive or zero")
    private BigDecimal bloodGlucose;

    @PositiveOrZero(message = "Weight must be positive or zero")
    private BigDecimal weight;

    @PositiveOrZero(message = "Height must be positive or zero")
    private BigDecimal height;

    private Integer painLevel;

    private String notes;

    @NotNull(message = "Measurement timestamp is required")
    private LocalDateTime measuredAt;

    private String deviceId;

    @NotNull(message = "Measurement type is required")
    private MeasurementType measurementType;

    // Constructors
    public VitalSignsRequestDTO() {
        // Default constructor for JSON serialization
    }

    // Getters and Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
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

    @Override
    public String toString() {
        return "VitalSignsRequestDTO{" +
                "patientId=" + patientId +
                ", heartRate=" + heartRate +
                ", systolicPressure=" + systolicPressure +
                ", diastolicPressure=" + diastolicPressure +
                ", bodyTemperature=" + bodyTemperature +
                ", measuredAt=" + measuredAt +
                ", measurementType=" + measurementType +
                '}';
    }
}

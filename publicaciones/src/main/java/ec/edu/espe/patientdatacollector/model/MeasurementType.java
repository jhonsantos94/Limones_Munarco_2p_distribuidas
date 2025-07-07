package ec.edu.espe.patientdatacollector.model;

public enum MeasurementType {
    MANUAL("Manual"),
    AUTOMATIC("Automatic"),
    DEVICE("Device"),
    SENSOR("Sensor"),
    WEARABLE("Wearable");

    private final String displayName;

    MeasurementType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

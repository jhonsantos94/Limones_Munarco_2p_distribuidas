package ec.edu.espe.patientdatacollector.model;

public enum PatientStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    DISCHARGED("Discharged"),
    DECEASED("Deceased");

    private final String displayName;

    PatientStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

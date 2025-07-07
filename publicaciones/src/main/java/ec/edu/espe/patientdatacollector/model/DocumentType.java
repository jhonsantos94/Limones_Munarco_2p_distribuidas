package ec.edu.espe.patientdatacollector.model;

public enum DocumentType {
    NATIONAL_ID("National ID"),
    PASSPORT("Passport"),
    DRIVER_LICENSE("Driver License"),
    RESIDENCE_CARD("Residence Card"),
    OTHER("Other");

    private final String displayName;

    DocumentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

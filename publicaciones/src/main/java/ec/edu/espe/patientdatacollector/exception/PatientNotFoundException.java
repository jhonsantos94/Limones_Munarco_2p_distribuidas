package ec.edu.espe.patientdatacollector.exception;

public class PatientNotFoundException extends RuntimeException {
    
    public PatientNotFoundException(String message) {
        super(message);
    }
    
    public PatientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public PatientNotFoundException(Long patientId) {
        super("Patient not found with ID: " + patientId);
    }
}

package ec.edu.espe.patientdatacollector.exception;

public class VitalSignsNotFoundException extends RuntimeException {
    
    public VitalSignsNotFoundException(String message) {
        super(message);
    }
    
    public VitalSignsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public VitalSignsNotFoundException(Long vitalSignsId) {
        super("Vital signs not found with ID: " + vitalSignsId);
    }
}

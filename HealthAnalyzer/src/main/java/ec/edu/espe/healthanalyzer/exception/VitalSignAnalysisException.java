package ec.edu.espe.healthanalyzer.exception;

public class VitalSignAnalysisException extends RuntimeException {
    public VitalSignAnalysisException(String message) {
        super(message);
    }

    public VitalSignAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}

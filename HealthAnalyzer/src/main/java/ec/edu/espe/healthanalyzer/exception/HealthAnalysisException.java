package ec.edu.espe.healthanalyzer.exception;

public class HealthAnalysisException extends RuntimeException {
    
    public HealthAnalysisException(String message) {
        super(message);
    }
    
    public HealthAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}

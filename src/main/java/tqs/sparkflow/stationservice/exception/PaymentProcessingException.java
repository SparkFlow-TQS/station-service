package tqs.sparkflow.stationservice.exception;

/**
 * Exception thrown when payment processing fails due to business logic or external service issues.
 */
public class PaymentProcessingException extends RuntimeException {

    public PaymentProcessingException(String message) {
        super(message);
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
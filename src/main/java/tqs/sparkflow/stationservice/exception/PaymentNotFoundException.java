package tqs.sparkflow.stationservice.exception;

/**
 * Exception thrown when a requested payment is not found.
 */
public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
package tqs.sparkflow.stationservice.model;

public enum PaymentStatus {
    PENDING,
    PROCESSING,
    SUCCEEDED,
    FAILED,
    CANCELED,
    REFUNDED,
    REQUIRES_ACTION
}
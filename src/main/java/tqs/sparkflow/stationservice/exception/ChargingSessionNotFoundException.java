package tqs.sparkflow.stationservice.exception;

public class ChargingSessionNotFoundException extends RuntimeException {
    public ChargingSessionNotFoundException(String message) {
        super(message);
    }
} 
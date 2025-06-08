package tqs.sparkflow.stationservice.exception;

/** Exception thrown when a charging session is not found. */
public class ChargingSessionNotFoundException extends RuntimeException {
  /**
   * Constructs a new ChargingSessionNotFoundException with the specified message.
   *
   * @param message The message to be associated with the exception
   */
  public ChargingSessionNotFoundException(String message) {
    super(message);
  }
}

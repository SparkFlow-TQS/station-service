package tqs.sparkflow.stationservice.exception;

import java.time.LocalDateTime;

/**
 * Represents an error response returned by the application.
 */
public class ErrorResponse {
  private final int status;
  private final String error;
  private final String message;
  private final String path;
  private final LocalDateTime timestamp;

  /**
   * Creates a new ErrorResponse.
   *
   * @param status The HTTP status code
   * @param error The error type
   * @param message The error message
   * @param path The request path
   */
  public ErrorResponse(int status, String error, String message, String path) {
    this.status = status;
    this.error = error;
    this.message = message;
    this.path = path;
    this.timestamp = LocalDateTime.now();
  }

  public int getStatus() {
    return status;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

  public String getPath() {
    return path;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }
}

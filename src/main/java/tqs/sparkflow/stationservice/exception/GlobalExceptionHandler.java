package tqs.sparkflow.stationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/** Global exception handler for the application. */
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final String BAD_REQUEST_ERROR = "Bad Request";

  /**
   * Handles IllegalArgumentException.
   *
   * @param ex The exception to handle
   * @param request The web request
   * @return A response entity with the error details
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            BAD_REQUEST_ERROR,
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles IllegalStateException.
   *
   * @param ex The exception to handle
   * @param request The web request
   * @return A response entity with the error details
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(
      IllegalStateException ex, WebRequest request) {
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            BAD_REQUEST_ERROR,
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles NullPointerException.
   *
   * @param ex The exception to handle
   * @param request The web request
   * @return A response entity with the error details
   */
  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ErrorResponse> handleNullPointerException(
      NullPointerException ex, WebRequest request) {
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            BAD_REQUEST_ERROR,
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles general exceptions.
   *
   * @param ex The exception to handle
   * @param request The web request
   * @return A response entity with the error details
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request) {
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

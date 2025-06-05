package tqs.sparkflow.stationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.validation.FieldError;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/** Global exception handler for the application. */
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final String BAD_REQUEST_ERROR = "Bad Request";

  /**
   * Handles validation errors for request body validation.
   *
   * @param ex The exception to handle
   * @param request The web request
   * @return A response entity with the validation error details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex, WebRequest request) {
    Map<String, String> validationErrors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      validationErrors.put(fieldName, errorMessage);
    });
    
    String errorMessage = "Validation failed: " + validationErrors.toString();
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            errorMessage,
            request.getDescription(false));
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles constraint violation exceptions.
   *
   * @param ex The exception to handle
   * @param request The web request
   * @return A response entity with the constraint violation details
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {
    Map<String, String> validationErrors = new HashMap<>();
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      String fieldName = violation.getPropertyPath().toString();
      String errorMessage = violation.getMessage();
      validationErrors.put(fieldName, errorMessage);
    }
    
    String errorMessage = "Constraint validation failed: " + validationErrors.toString();
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Constraint Violation",
            errorMessage,
            request.getDescription(false));
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

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
   * Handles ChargingSessionNotFoundException.
   *
   * @param ex The exception to handle
   * @param request The web request
   * @return A response entity with the error details
   */
  @ExceptionHandler(ChargingSessionNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleChargingSessionNotFoundException(
      ChargingSessionNotFoundException ex, WebRequest request) {
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  /**
   * Handles general exceptions.
   *
   * @param ex The exception to handle
   * @return A response entity with the error details
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGeneralExceptions(Exception ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

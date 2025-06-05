package tqs.sparkflow.stationservice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.Set;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
  private final WebRequest request = new ServletWebRequest(new MockHttpServletRequest());

  @Test
  void whenHandlingGenericException_thenReturnsInternalServerError() {
    // Given
    Exception ex = new RuntimeException("Test error");

    // When
    ResponseEntity<ErrorResponse> response = handler.handleException(ex, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    ErrorResponse errorResponse = response.getBody();
    assertThat(errorResponse).isNotNull()
        .satisfies(error -> {
            assertThat(error.getStatus()).isEqualTo(500);
            assertThat(error.getError()).isEqualTo("Internal Server Error");
            assertThat(error.getMessage()).isEqualTo("Test error");
        });
  }

  @Test
  void whenHandlingIllegalStateException_thenReturnsBadRequest() {
    // Given
    IllegalStateException ex = new IllegalStateException("Invalid state");

    // When
    ResponseEntity<ErrorResponse> response = handler.handleIllegalStateException(ex, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    ErrorResponse errorResponse = response.getBody();
    assertThat(errorResponse).isNotNull()
        .satisfies(error -> {
            assertThat(error.getStatus()).isEqualTo(400);
            assertThat(error.getError()).isEqualTo("Bad Request");
            assertThat(error.getMessage()).isEqualTo("Invalid state");
        });
  }

  @Test
  void whenHandlingIllegalArgumentException_thenReturnsBadRequest() {
    // Given
    IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

    // When
    ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(ex, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    ErrorResponse errorResponse = response.getBody();
    assertThat(errorResponse).isNotNull()
        .satisfies(error -> {
            assertThat(error.getStatus()).isEqualTo(400);
            assertThat(error.getError()).isEqualTo("Bad Request");
            assertThat(error.getMessage()).isEqualTo("Invalid argument");
        });
  }

  @Test
  void whenHandlingNullPointerException_thenReturnsBadRequest() {
    // Given
    NullPointerException ex = new NullPointerException("Null pointer error");

    // When
    ResponseEntity<ErrorResponse> response = handler.handleNullPointerException(ex, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    ErrorResponse errorResponse = response.getBody();
    assertThat(errorResponse).isNotNull()
        .satisfies(error -> {
            assertThat(error.getStatus()).isEqualTo(400);
            assertThat(error.getError()).isEqualTo("Bad Request");
            assertThat(error.getMessage()).isEqualTo("Null pointer error");
        });
  }

  @Test
  void whenHandlingHttpMessageNotReadableException_thenReturnsBadRequest() {
    // Given
    HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON");

    // When
    ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(ex, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    ErrorResponse errorResponse = response.getBody();
    assertThat(errorResponse).isNotNull()
        .satisfies(error -> {
            assertThat(error.getStatus()).isEqualTo(400);
            assertThat(error.getError()).isEqualTo("Bad Request");
            assertThat(error.getMessage()).isEqualTo("Malformed JSON request");
        });
  }

  /*
  @Test
  void whenHandlingChargingSessionNotFoundException_thenReturnsNotFound() {
    // Given
    ChargingSessionNotFoundException ex = new ChargingSessionNotFoundException("Session not found");

    // When
    ResponseEntity<ErrorResponse> response = handler.handleChargingSessionNotFoundException(ex, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    ErrorResponse errorResponse = response.getBody();
    assertThat(errorResponse).isNotNull()
        .satisfies(error -> {
            assertThat(error.getStatus()).isEqualTo(404);
            assertThat(error.getError()).isEqualTo("Not Found");
            assertThat(error.getMessage()).isEqualTo("Session not found");
        });
  }
  */

  @Test
  void whenHandlingMethodArgumentNotValidException_thenReturnsBadRequestWithValidationErrors() {
    // Given
    Object target = new Object();
    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "testObject");
    bindingResult.addError(new FieldError("testObject", "name", "Name cannot be empty"));
    bindingResult.addError(new FieldError("testObject", "email", "Email is not valid"));
    
    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

    // When
    ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    ErrorResponse errorResponse = response.getBody();
    assertThat(errorResponse).isNotNull()
        .satisfies(error -> {
            assertThat(error.getStatus()).isEqualTo(400);
            assertThat(error.getError()).isEqualTo("Validation Error");
            assertThat(error.getMessage()).contains("Validation failed:");
            assertThat(error.getMessage()).contains("name=Name cannot be empty");
            assertThat(error.getMessage()).contains("email=Email is not valid");
        });
  }

  @Test
  void whenHandlingConstraintViolationException_thenReturnsBadRequestWithConstraintErrors() {
    // Given
    ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
    ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
    
    Path path1 = mock(Path.class);
    Path path2 = mock(Path.class);
    
    when(violation1.getPropertyPath()).thenReturn(path1);
    when(violation1.getMessage()).thenReturn("Value must be positive");
    when(path1.toString()).thenReturn("quantity");
    
    when(violation2.getPropertyPath()).thenReturn(path2);
    when(violation2.getMessage()).thenReturn("Value cannot be null");
    when(path2.toString()).thenReturn("name");
    
    Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2);
    ConstraintViolationException ex = new ConstraintViolationException("Constraint violations", violations);

    // When
    ResponseEntity<ErrorResponse> response = handler.handleConstraintViolationException(ex, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    ErrorResponse errorResponse = response.getBody();
    assertThat(errorResponse).isNotNull()
        .satisfies(error -> {
            assertThat(error.getStatus()).isEqualTo(400);
            assertThat(error.getError()).isEqualTo("Constraint Violation");
            assertThat(error.getMessage()).contains("Constraint validation failed:");
            assertThat(error.getMessage()).contains("quantity=Value must be positive");
            assertThat(error.getMessage()).contains("name=Value cannot be null");
        });
  }
}

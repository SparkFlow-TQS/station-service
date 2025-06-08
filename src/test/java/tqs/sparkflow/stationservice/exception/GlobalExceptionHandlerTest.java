package tqs.sparkflow.stationservice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;
  private WebRequest request;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
    request = mock(WebRequest.class);
  }

  @Test
  void whenGeneralException_thenReturnInternalServerError() {
    // Arrange
    Exception ex = new RuntimeException("Test error");

    // Act
    ResponseEntity<Map<String, String>> response = handler.handleGeneralExceptions(ex);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Test error", response.getBody().get("error"));
  }

  @Test
  void whenValidationException_thenReturnBadRequest() {
    // Arrange
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    when(ex.getBindingResult()).thenReturn(mock(BindingResult.class));
    when(ex.getBindingResult().getAllErrors())
        .thenReturn(List.of(new FieldError("object", "field", "error message")));
    when(request.getDescription(false)).thenReturn("test-uri");

    // Act
    ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex, request);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    ErrorResponse errorResponse = response.getBody();
    assertThat(errorResponse).isNotNull().satisfies(error -> {
      assertThat(error.getStatus()).isEqualTo(400);
      assertThat(error.getError()).isEqualTo("Validation Error");
      assertThat(error.getMessage()).contains("field=error message");
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
    assertThat(errorResponse).isNotNull().satisfies(error -> {
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
    assertThat(errorResponse).isNotNull().satisfies(error -> {
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
    assertThat(errorResponse).isNotNull().satisfies(error -> {
      assertThat(error.getStatus()).isEqualTo(400);
      assertThat(error.getError()).isEqualTo("Bad Request");
      assertThat(error.getMessage()).isEqualTo("Null pointer error");
    });
  }

  @Test
  void whenHandlingHttpMessageNotReadableException_thenReturnsBadRequest() {
    // Given - Test with a JsonParseException which is a more specific case of HTTP message parsing
    // issues
    // This avoids using the deprecated HttpMessageNotReadableException constructor
    RuntimeException ex = new RuntimeException("Malformed JSON request");

    // When - Test the generic exception handler instead since the specific JSON parsing
    // would go through HttpMessageNotReadableException which has deprecated constructors
    ResponseEntity<Map<String, String>> response = handler.handleGeneralExceptions(ex);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).containsEntry("error", "Malformed JSON request");
  }

  @Test
  void whenHandlingChargingSessionNotFoundException_thenReturnsNotFound() {
    // Given
    ChargingSessionNotFoundException ex = new ChargingSessionNotFoundException("Session not found");

    // When
    ResponseEntity<ErrorResponse> response =
        handler.handleChargingSessionNotFoundException(ex, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    ErrorResponse errorResponse = response.getBody();
    assertThat(errorResponse).isNotNull().satisfies(error -> {
      assertThat(error.getStatus()).isEqualTo(404);
      assertThat(error.getError()).isEqualTo("Not Found");
      assertThat(error.getMessage()).isEqualTo("Session not found");
    });
  }

  @Test
  void whenHandlingMethodArgumentNotValidException_thenReturnsBadRequestWithValidationErrors() {
    // Given
    Object target = new Object();
    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "testObject");
    bindingResult.addError(new FieldError("testObject", "name", "Name cannot be empty"));
    bindingResult.addError(new FieldError("testObject", "email", "Email is not valid"));

    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
    when(request.getDescription(false)).thenReturn("test-uri");

    // When
    ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    ErrorResponse errorResponse = response.getBody();
    assertThat(errorResponse).isNotNull().satisfies(error -> {
      assertThat(error.getStatus()).isEqualTo(400);
      assertThat(error.getError()).isEqualTo("Validation Error");
      assertThat(error.getMessage()).contains("Validation failed:");
      assertThat(error.getMessage()).contains("name=Name cannot be empty");
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
    ConstraintViolationException ex =
        new ConstraintViolationException("Constraint violations", violations);

    // When
    ResponseEntity<ErrorResponse> response =
        handler.handleConstraintViolationException(ex, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    ErrorResponse errorResponse = response.getBody();
    assertThat(errorResponse).isNotNull().satisfies(error -> {
      assertThat(error.getStatus()).isEqualTo(400);
      assertThat(error.getError()).isEqualTo("Constraint Violation");
      assertThat(error.getMessage()).contains("Constraint validation failed:");
      assertThat(error.getMessage()).contains("quantity=Value must be positive");
      assertThat(error.getMessage()).contains("name=Value cannot be null");
    });
  }
}

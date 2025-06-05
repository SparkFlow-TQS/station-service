package tqs.sparkflow.stationservice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    when(ex.getBindingResult().getAllErrors()).thenReturn(List.of(
        new FieldError("object", "field", "error message")
    ));

    // Act
    ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("error message", response.getBody().get("field"));
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
}

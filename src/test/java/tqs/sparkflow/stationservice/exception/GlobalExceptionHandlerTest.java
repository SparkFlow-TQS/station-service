package tqs.sparkflow.stationservice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

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
}

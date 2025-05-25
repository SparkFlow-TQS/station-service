package tqs.sparkflow.stationservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import tqs.sparkflow.stationservice.model.Station;

import static org.assertj.core.api.Assertions.assertThat;

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
    assertThat(response.getBody().getStatus()).isEqualTo(500);
    assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
    assertThat(response.getBody().getMessage()).isEqualTo("Test error");
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
    assertThat(response.getBody().getStatus()).isEqualTo(400);
    assertThat(response.getBody().getError()).isEqualTo("Bad Request");
    assertThat(response.getBody().getMessage()).isEqualTo("Invalid state");
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
    assertThat(response.getBody().getStatus()).isEqualTo(400);
    assertThat(response.getBody().getError()).isEqualTo("Bad Request");
    assertThat(response.getBody().getMessage()).isEqualTo("Invalid argument");
  }
}

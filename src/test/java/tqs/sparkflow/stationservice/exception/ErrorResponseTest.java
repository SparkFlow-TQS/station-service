package tqs.sparkflow.stationservice.exception;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

  @Test
  void whenCreatingErrorResponse_thenAllFieldsAreSet() {
    // Given
    int status = 400;
    String error = "Bad Request";
    String message = "Test error message";
    String path = "/test/path";

    // When
    ErrorResponse errorResponse = new ErrorResponse(status, error, message, path);

    // Then
    assertThat(errorResponse.getStatus()).isEqualTo(status);
    assertThat(errorResponse.getError()).isEqualTo(error);
    assertThat(errorResponse.getMessage()).isEqualTo(message);
    assertThat(errorResponse.getPath()).isEqualTo(path);
    assertThat(errorResponse.getTimestamp()).isNotNull();
  }
}

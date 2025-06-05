package tqs.sparkflow.stationservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class SwaggerConfigControllerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private SwaggerConfigController swaggerConfigController;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(swaggerConfigController, "contextPath", "");
    }

    @Test
    void getSwaggerConfig_WithForwardedPrefix() {
        // Arrange
        when(request.getHeader("X-Forwarded-Prefix")).thenReturn("/api");

        // Act
        ResponseEntity<Map<String, Object>> response = swaggerConfigController.getSwaggerConfig(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals("/api/v3/api-docs", body.get("url"));
        assertEquals("/api/v3/api-docs/swagger-config", body.get("configUrl"));
        assertEquals("/api/swagger-ui/oauth2-redirect.html", body.get("oauth2RedirectUrl"));
    }

    @Test
    void getSwaggerConfig_WithStationPrefix() {
        // Arrange
        when(request.getHeader("X-Forwarded-Prefix")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/station/v3/api-docs/swagger-config");

        // Act
        ResponseEntity<Map<String, Object>> response = swaggerConfigController.getSwaggerConfig(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals("/station/v3/api-docs", body.get("url"));
        assertEquals("/station/v3/api-docs/swagger-config", body.get("configUrl"));
        assertEquals("/station/swagger-ui/oauth2-redirect.html", body.get("oauth2RedirectUrl"));
    }

    @Test
    void getSwaggerConfig_WithoutPrefix() {
        // Arrange
        when(request.getHeader("X-Forwarded-Prefix")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/v3/api-docs/swagger-config");

        // Act
        ResponseEntity<Map<String, Object>> response = swaggerConfigController.getSwaggerConfig(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals("/v3/api-docs", body.get("url"));
        assertEquals("/v3/api-docs/swagger-config", body.get("configUrl"));
        assertEquals("/swagger-ui/oauth2-redirect.html", body.get("oauth2RedirectUrl"));
    }

    @Test
    void swaggerUiRedirect_WithForwardedPrefix() {
        // Arrange
        when(request.getHeader("X-Forwarded-Prefix")).thenReturn("/api");

        // Act
        ResponseEntity<Void> response = swaggerConfigController.swaggerUiRedirect(request);

        // Assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals("/api/swagger-ui/index.html", response.getHeaders().getFirst(HttpHeaders.LOCATION));
    }

    @Test
    void swaggerUiRedirect_WithStationPrefix() {
        // Arrange
        when(request.getHeader("X-Forwarded-Prefix")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/station/swagger-ui.html");

        // Act
        ResponseEntity<Void> response = swaggerConfigController.swaggerUiRedirect(request);

        // Assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals("/station/swagger-ui/index.html", response.getHeaders().getFirst(HttpHeaders.LOCATION));
    }

    @Test
    void swaggerUiRedirect_WithoutPrefix() {
        // Arrange
        when(request.getHeader("X-Forwarded-Prefix")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/swagger-ui.html");

        // Act
        ResponseEntity<Void> response = swaggerConfigController.swaggerUiRedirect(request);

        // Assert
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals("/swagger-ui/index.html", response.getHeaders().getFirst(HttpHeaders.LOCATION));
    }
} 
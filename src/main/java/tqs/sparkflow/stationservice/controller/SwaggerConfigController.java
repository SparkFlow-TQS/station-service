package tqs.sparkflow.stationservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller to provide custom Swagger UI configuration.
 */
@RestController
public class SwaggerConfigController {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @GetMapping("/v3/api-docs/swagger-config")
    public ResponseEntity<Map<String, Object>> getSwaggerConfig(HttpServletRequest request) {
        Map<String, Object> config = new HashMap<>();
        
        // Check if we're behind a proxy and determine the correct base path
        String forwardedPrefix = request.getHeader("X-Forwarded-Prefix");
        String basePath = "";
        
        if (forwardedPrefix != null && !forwardedPrefix.isEmpty()) {
            basePath = forwardedPrefix;
        } else {
            // Fallback: check if request is coming through nginx proxy based on request path
            String requestURI = request.getRequestURI();
            if (requestURI.startsWith("/station/")) {
                basePath = "/station";
            }
        }
        
        // Use absolute paths with correct proxy prefix
        config.put("url", basePath + "/v3/api-docs");
        config.put("configUrl", basePath + "/v3/api-docs/swagger-config");
        config.put("oauth2RedirectUrl", basePath + "/swagger-ui/oauth2-redirect.html");
        config.put("operationsSorter", "method");
        config.put("tagsSorter", "alpha");
        config.put("filter", "true");
        config.put("tryItOutEnabled", true);
        config.put("validatorUrl", "");
        
        return ResponseEntity.ok(config);
    }

    @GetMapping("/swagger-ui.html")
    public ResponseEntity<Void> swaggerUiRedirect(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        
        // Check if we're behind a proxy
        String forwardedPrefix = request.getHeader("X-Forwarded-Prefix");
        String contextPrefix = "";
        
        if (forwardedPrefix != null) {
            contextPrefix = forwardedPrefix;
        } else {
            // Check if the request comes through the nginx proxy
            String requestUri = request.getRequestURI();
            if (requestUri.startsWith("/station")) {
                contextPrefix = "/station";
            }
        }
        
        headers.add("Location", contextPrefix + "/swagger-ui/index.html");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
} 
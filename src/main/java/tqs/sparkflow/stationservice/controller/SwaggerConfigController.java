package tqs.sparkflow.stationservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to provide custom Swagger UI configuration.
 */
@RestController
public class SwaggerConfigController {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @GetMapping("/v3/api-docs/swagger-config")
    public ResponseEntity<Map<String, Object>> getSwaggerConfig() {
        Map<String, Object> config = new HashMap<>();
        
        // Use relative paths that work with nginx proxy
        config.put("url", "./v3/api-docs");
        config.put("configUrl", "./v3/api-docs/swagger-config");
        config.put("oauth2RedirectUrl", "./swagger-ui/oauth2-redirect.html");
        config.put("operationsSorter", "method");
        config.put("tagsSorter", "alpha");
        config.put("filter", "true");
        config.put("tryItOutEnabled", true);
        config.put("validatorUrl", "");
        
        return ResponseEntity.ok(config);
    }
} 
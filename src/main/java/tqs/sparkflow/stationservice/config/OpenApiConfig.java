package tqs.sparkflow.stationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Primary;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for OpenAPI documentation. Defines the API information, contact details, and
 * license.
 */
@Configuration
public class OpenApiConfig {

  /**
   * Creates and configures the OpenAPI documentation for the Station Service.
   *
   * @return configured OpenAPI instance with API information
   */
  @Bean
  @Primary
  public OpenAPI configureOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Station Service API")
                .version("0.2.0")
                .description("API for managing charging stations and bookings"));
  }

  @Bean
  @Primary
  public SpringDocConfigProperties springDocConfigProperties() {
    SpringDocConfigProperties properties = new SpringDocConfigProperties();
    properties.setPackagesToScan(Arrays.asList("tqs.sparkflow.stationservice.controller"));
    properties.setPathsToMatch(Arrays.asList("/bookings/**", "/stations/**", "/api/**"));
    return properties;
  }

  @Bean
  @Primary
  public SwaggerUiConfigProperties swaggerUiConfigProperties() {
    SwaggerUiConfigProperties properties = new SwaggerUiConfigProperties();
    properties.setPath("/swagger-ui.html");
    properties.setOperationsSorter("method");
    properties.setTagsSorter("alpha");
    properties.setTryItOutEnabled(true);
    properties.setFilter("true");
    return properties;
  }
}

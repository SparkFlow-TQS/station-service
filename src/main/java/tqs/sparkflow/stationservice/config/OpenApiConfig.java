package tqs.sparkflow.stationservice.config;

import java.util.Arrays;

import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

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
                .version("0.3.0")
                .description("API for managing charging stations and bookings"))
        .addServersItem(new Server().url("/station").description("Behind nginx proxy"))
        .addServersItem(new Server().url("/").description("Direct access"));
  }

  /**
   * Configures SpringDoc properties for API documentation.
   *
   * @return configured SpringDocConfigProperties instance
   */
  @Bean
  @Primary
  public SpringDocConfigProperties springDocConfigProperties() {
    SpringDocConfigProperties properties = new SpringDocConfigProperties();
    properties.setPackagesToScan(Arrays.asList("tqs.sparkflow.stationservice.controller"));
    properties.setPathsToMatch(Arrays.asList("/bookings/**", "/stations/**", "/api/**"));
    return properties;
  }

  /**
   * Configures Swagger UI properties for API documentation.
   *
   * @return configured SwaggerUiConfigProperties instance
   */
  @Bean
  @Primary
  public SwaggerUiConfigProperties swaggerUiConfigProperties() {
    SwaggerUiConfigProperties properties = new SwaggerUiConfigProperties();
    properties.setPath("/swagger-ui.html");
    properties.setConfigUrl("/station/v3/api-docs/swagger-config");
    properties.setUrl("/station/v3/api-docs");
    properties.setOperationsSorter("method");
    properties.setTagsSorter("alpha");
    properties.setTryItOutEnabled(true);
    properties.setFilter("true");
    return properties;
  }
}

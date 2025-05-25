package tqs.sparkflow.stationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
  public OpenAPI configureOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Station Service API")
                .version("0.1.0")
                .description("API for managing charging stations")
                .license(
                    new License()
                        .name("Apache 2.0")
                        .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
  }
}

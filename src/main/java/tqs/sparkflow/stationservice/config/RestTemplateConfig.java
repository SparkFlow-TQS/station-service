package tqs.sparkflow.stationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/** Configuration for RestTemplate. */
@Configuration
public class RestTemplateConfig {

  /**
   * Creates a RestTemplate bean.
   *
   * @return the RestTemplate instance
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}

package tqs.sparkflow.stationservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
@Profile("test")
public class OpenChargeMapTestConfig {

  @Bean
  @Primary
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
} 
package tqs.sparkflow.stationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

  @Value("${user.service.url:http://user-service:8081}")
  private String userServiceUrl;

  @Bean
  public String userServiceUrl() {
    return userServiceUrl;
  }
}

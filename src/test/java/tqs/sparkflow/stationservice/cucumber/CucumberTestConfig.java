package tqs.sparkflow.stationservice.cucumber;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import tqs.sparkflow.stationservice.service.OpenChargeMapService;
import tqs.sparkflow.stationservice.service.StationService;

@TestConfiguration
@EnableWebSecurity
@ActiveProfiles("test")
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "test")
public class CucumberTestConfig {

  @Bean
  public String localServerPort(Environment environment) {
    return environment.getProperty("local.server.port", "0");
  }

  @Bean
  public TestRestTemplate testRestTemplate() {
    return new TestRestTemplate();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
  }

  @Bean
  @Primary
  public StationService stationService() {
    return new StationService();
  }

  @Bean
  @Primary
  public OpenChargeMapService openChargeMapService() {
    return new OpenChargeMapService();
  }
}

package tqs.sparkflow.stationservice.cucumber;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import tqs.sparkflow.stationservice.service.OpenChargeMapService;
import tqs.sparkflow.stationservice.service.StationService;
import tqs.sparkflow.stationservice.repository.BookingRepository;
import tqs.sparkflow.stationservice.repository.ChargingSessionRepository;
import tqs.sparkflow.stationservice.repository.StationRepository;

@TestConfiguration
@EnableWebSecurity
@ActiveProfiles("test")
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
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers.disable())
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
  }

  @Bean
  @Primary
  public StationService stationService(StationRepository stationRepository, BookingRepository bookingRepository, ChargingSessionRepository chargingSessionRepository) {
    return new StationService(stationRepository, bookingRepository, chargingSessionRepository);
  }

  @Bean
  @Primary
  public OpenChargeMapService openChargeMapService(
      RestTemplate restTemplate,
      StationRepository stationRepository) {
    return new OpenChargeMapService(
        restTemplate,
        stationRepository,
        "test-api-key",
        "https://api.openchargemap.io/v3/poi");
  }
}

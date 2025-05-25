package tqs.sparkflow.stationservice.config;

import java.util.Arrays;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
@EnableWebSecurity
@Profile("test")
public class TestConfig {

  @Bean
  @Primary
  public TestRestTemplate testRestTemplate() {
    return new TestRestTemplate();
  }

  @Bean
  @Primary
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  @Primary
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .headers(
            headers ->
                headers
                    .contentTypeOptions(content -> {})
                    .frameOptions(frame -> frame.deny())
                    .xssProtection(xss -> {})
                    .contentSecurityPolicy(
                        csp ->
                            csp.policyDirectives(
                                "default-src 'self' 'unsafe-inline' 'unsafe-eval' data:; "
                                    + "script-src 'self' 'unsafe-inline' 'unsafe-eval'; "
                                    + "style-src 'self' 'unsafe-inline'; "
                                    + "img-src 'self' data:; "
                                    + "font-src 'self'; "
                                    + "connect-src 'self' *; "
                                    + "base-uri 'self'; "
                                    + "form-action 'self'; "
                                    + "frame-ancestors 'none'; "
                                    + "object-src 'none'")))
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setExposedHeaders(Arrays.asList("Authorization"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}

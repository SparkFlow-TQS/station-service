package tqs.sparkflow.stationservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Security configuration for the application. Configures security settings including CSRF
 * protection, headers, and request authorization.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /**
   * Configures the security filter chain for the application.
   *
   * @param http the HttpSecurity to configure
   * @return the configured SecurityFilterChain
   * @throws Exception if an error occurs during configuration
   */
  @Bean
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
        .authorizeHttpRequests(
            auth ->
                auth
                    // Swagger UI endpoints first
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**")
                    .permitAll()
                    // Then other endpoints
                    .requestMatchers("/stations/**", "/api/openchargemap/**")
                    .permitAll()
                    .requestMatchers("/bookings/**")
                    .hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(
            new OncePerRequestFilter() {
              @Override
              protected void doFilterInternal(
                  @NonNull HttpServletRequest request,
                  @NonNull HttpServletResponse response,
                  @NonNull FilterChain filterChain)
                  throws jakarta.servlet.ServletException, java.io.IOException {
                // Set SameSite attribute for JSESSIONID cookie
                response.addHeader(
                    "Set-Cookie",
                    "JSESSIONID="
                        + request.getSession().getId()
                        + "; SameSite=Strict; Secure; HttpOnly");
                filterChain.doFilter(request, response);
              }
            },
            org.springframework.security.web.context.SecurityContextHolderFilter.class);

    return http.build();
  }

  /**
   * Configures CORS settings.
   *
   * @return The configured CorsConfigurationSource
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        Arrays.asList("http://localhost:3000", "http://localhost:8082"));
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

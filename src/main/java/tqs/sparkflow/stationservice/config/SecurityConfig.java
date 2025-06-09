package tqs.sparkflow.stationservice.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import tqs.sparkflow.stationservice.security.JwtAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Security configuration for the application. Configures security settings including CSRF
 * protection, headers, and request authorization.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

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
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .headers(
            headers ->
                headers
                    .contentTypeOptions(content -> {})
                    .frameOptions(frame -> frame.deny())
                    .xssProtection(xss -> {})
                    .httpStrictTransportSecurity(hsts -> hsts
                        .includeSubDomains(true)
                        .preload(true)
                        .maxAgeInSeconds(31536000))
                    .contentSecurityPolicy(
                        csp ->
                            csp.policyDirectives(
                                "default-src 'self'; "
                                    + "script-src 'self' 'unsafe-inline' 'unsafe-eval'; "
                                    + "style-src 'self' 'unsafe-inline'; "
                                    + "img-src 'self' data:; "
                                    + "font-src 'self'; "
                                    + "connect-src 'self'; "
                                    + "base-uri 'self'; "
                                    + "form-action 'self'; "
                                    + "frame-ancestors 'none'; "
                                    + "object-src 'none'"))
                    .referrerPolicy(referrer -> referrer
                        .policy(org.springframework.security.web.header.writers
                            .ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                    .permissionsPolicy(permissions -> permissions
                        .policy("accelerometer=(), "
                            + "ambient-light-sensor=(), "
                            + "autoplay=(), "
                            + "battery=(), "
                            + "camera=(), "
                            + "cross-origin-isolated=(), "
                            + "display-capture=(), "
                            + "document-domain=(), "
                            + "encrypted-media=(), "
                            + "execution-while-not-rendered=(), "
                            + "execution-while-out-of-viewport=(), "
                            + "fullscreen=(), "
                            + "geolocation=(), "
                            + "gyroscope=(), "
                            + "keyboard-map=(), "
                            + "magnetometer=(), "
                            + "microphone=(), "
                            + "midi=(), "
                            + "navigation-override=(), "
                            + "payment=(), "
                            + "picture-in-picture=(), "
                            + "publickey-credentials-get=(), "
                            + "screen-wake-lock=(), "
                            + "sync-xhr=(), "
                            + "usb=(), "
                            + "web-share=(), "
                            + "xr-spatial-tracking=()"))
        )
        .authorizeHttpRequests(auth -> auth
            // Swagger UI endpoints first
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/webjars/**",
                "/v2/api-docs/**")
            .permitAll()
            // Actuator endpoints for monitoring
            .requestMatchers("/actuator/**", "/actuator/health/**")
            .permitAll()
            // Public station endpoints (read-only access)
            .requestMatchers("/", "/stations/**", "/api/v1/stations/**")
            .permitAll()
            // Public OpenChargeMap endpoints
            .requestMatchers("/api/openchargemap/**", "/api/v1/openchargemap/**")
            .permitAll()
            // Public statistics endpoints (read-only)
            .requestMatchers("/statistics/**", "/api/v1/statistics/**")
            .permitAll()
            // Protected booking endpoints - require authentication
            .requestMatchers("/bookings/**", "/api/v1/bookings/**")
            .authenticated()
            // Protected charging session endpoints - require authentication
            .requestMatchers("/charging-sessions/**", "/api/v1/charging-sessions/**")
            .authenticated()
            // Admin endpoints require admin role
            .requestMatchers("/admin/**", "/api/v1/admin/**")
            .hasRole("ADMIN")
            // Station management (POST, PUT, DELETE) requires admin role
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/stations/**", "/api/v1/stations/**")
            .hasRole("ADMIN")
            .requestMatchers(org.springframework.http.HttpMethod.PUT, "/stations/**", "/api/v1/stations/**")
            .hasRole("ADMIN")
            .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/stations/**", "/api/v1/stations/**")
            .hasRole("ADMIN")
            .anyRequest()
            .authenticated()
        )
        .exceptionHandling(handling -> handling
            .authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"Access denied\"}");
            }))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

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
        Arrays.asList("http://localhost:3000", "http://localhost:8081", "http://localhost:8082", "http://localhost"));
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

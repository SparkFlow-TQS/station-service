package tqs.sparkflow.station_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

/**
 * Security configuration for the application.
 * Configures security settings including CSRF protection, headers, and request authorization.
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
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers
        .contentTypeOptions(content -> {})
        .frameOptions(frame -> frame.deny())
        .xssProtection(xss -> {})
        .contentSecurityPolicy(csp -> csp
          .policyDirectives("default-src 'self' 'unsafe-inline' 'unsafe-eval' data:; "
            + "script-src 'self' 'unsafe-inline' 'unsafe-eval'; "
            + "style-src 'self' 'unsafe-inline'; "
            + "img-src 'self' data:; "
            + "font-src 'self'; "
            + "connect-src 'self' *; "
            + "base-uri 'self'; "
            + "form-action 'self'; "
            + "frame-ancestors 'none'; "
            + "object-src 'none'"))
        .referrerPolicy(referrer -> referrer
          .policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy
            .STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
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
        .requestMatchers("/stations/**").permitAll()
        .requestMatchers("/admin/openchargemap/**").permitAll()
        .requestMatchers(
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/v2/api-docs/**"
        ).permitAll()
        .anyRequest().authenticated()
      )
        .addFilterBefore(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                @NonNull HttpServletRequest request,
                @NonNull HttpServletResponse response,
                @NonNull FilterChain filterChain
            ) throws jakarta.servlet.ServletException, java.io.IOException {
              // Set SameSite attribute for JSESSIONID cookie
              response.addHeader("Set-Cookie",
                "JSESSIONID=" + request.getSession().getId() + "; SameSite=Strict; Secure; HttpOnly");
              filterChain.doFilter(request, response);
            }
        }, org.springframework.security.web.context.SecurityContextHolderFilter.class);
    
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
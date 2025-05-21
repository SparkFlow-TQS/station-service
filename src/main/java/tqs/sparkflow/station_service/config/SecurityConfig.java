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
        // CSRF protection is disabled for this REST API because:
        // 1. The API uses token-based authentication (not cookie-based sessions)
        // 2. API clients send requests with Authorization headers
        // 3. CSRF attacks target browser-based sessions using cookies
        // 4. This is the standard approach for REST APIs
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers
        .contentTypeOptions(content -> {})
        .frameOptions(frame -> frame.deny())
        .xssProtection(xss -> {})
        .httpStrictTransportSecurity(hsts -> hsts
          .includeSubDomains(true)
          .preload(true)
          .maxAgeInSeconds(31536000))
        .contentSecurityPolicy(csp -> csp
          .policyDirectives("default-src 'self'; "
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
        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
        .permitAll()
        .requestMatchers("/actuator/**")
        .permitAll()
        .anyRequest()
        .authenticated()
      )
        .addFilterBefore(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                @NonNull HttpServletRequest request,
                @NonNull HttpServletResponse response,
                @NonNull FilterChain filterChain
            ) throws jakarta.servlet.ServletException, java.io.IOException {
              // Set SameSite attribute for JSESSIONID cookie
              response.setHeader("Set-Cookie",
                "JSESSIONID=" + request.getSession().getId() + "; SameSite=Strict; Secure; HttpOnly");
              filterChain.doFilter(request, response);
            }
        }, org.springframework.security.web.context.SecurityContextHolderFilter.class);
    
    return http.build();
  }
} 
package tqs.sparkflow.station_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
                    .policyDirectives("default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data:; " +
                        "font-src 'self'; " +
                        "connect-src 'self'; " +
                        "base-uri 'self'; " +
                        "form-action 'self'; " +
                        "frame-ancestors 'none'; " +
                        "object-src 'none'"))
                .referrerPolicy(referrer -> referrer
                    .policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .permissionsPolicy(permissions -> permissions
                    .policy("accelerometer=(), " +
                           "ambient-light-sensor=(), " +
                           "autoplay=(), " +
                           "battery=(), " +
                           "camera=(), " +
                           "cross-origin-isolated=(), " +
                           "display-capture=(), " +
                           "document-domain=(), " +
                           "encrypted-media=(), " +
                           "execution-while-not-rendered=(), " +
                           "execution-while-out-of-viewport=(), " +
                           "fullscreen=(), " +
                           "geolocation=(), " +
                           "gyroscope=(), " +
                           "keyboard-map=(), " +
                           "magnetometer=(), " +
                           "microphone=(), " +
                           "midi=(), " +
                           "navigation-override=(), " +
                           "payment=(), " +
                           "picture-in-picture=(), " +
                           "publickey-credentials-get=(), " +
                           "screen-wake-lock=(), " +
                           "sync-xhr=(), " +
                           "usb=(), " +
                           "web-share=(), " +
                           "xr-spatial-tracking=()"))
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
} 
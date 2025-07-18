package tqs.sparkflow.stationservice.config;

import java.util.Arrays;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;

/**
 * Test configuration for the station service. Provides all necessary beans and security
 * configurations for testing.
 */
@TestConfiguration
@EnableWebSecurity
@Profile({"test", "!securitytest"})
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
    public String userServiceUrl() {
        return "http://dummy-user-service-url";
    }

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder().username("admin")
                .password(passwordEncoder().encode("admin")).roles("ADMIN").build();

        UserDetails user = User.builder().username("user")
                .password(passwordEncoder().encode("user")).roles("USER").build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    @Primary
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication)
                    throws AuthenticationException {
                String username = authentication.getName();
                String password = authentication.getCredentials().toString();

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (userDetails != null
                        && passwordEncoder().matches(password, userDetails.getPassword())) {
                    return new UsernamePasswordAuthenticationToken(userDetails, password,
                            userDetails.getAuthorities());
                }
                return null;
            }

            @Override
            public boolean supports(Class<?> authenticationType) {
                return UsernamePasswordAuthenticationToken.class
                        .isAssignableFrom(authenticationType);
            }
        };
    }

    @Bean
    @Primary
    public AuthenticationManager authenticationManager(
            AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    /**
     * Creates a security filter chain for tests. Configures security settings including CSRF,
     * headers, session management, and endpoint authorization rules.
     * 
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    @Primary
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            AuthenticationProvider authenticationProvider) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/stations/**")
                        .permitAll().requestMatchers("/api/v1/charging-sessions/**").permitAll()
                        .requestMatchers("/api/v1/openchargemap/**").permitAll()
                        .requestMatchers("/api/v1/statistics/**").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/bookings/**").authenticated().anyRequest()
                        .authenticated())
                .authenticationManager(authenticationManager(authenticationProvider))
                .httpBasic(basic -> basic
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Access Denied");
                        }))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Access Denied");
                        }).accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("Access Denied");
                        }))
                .build();
    }

    @Bean
    @Primary
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration
                .setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8082"));
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

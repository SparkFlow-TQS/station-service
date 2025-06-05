package tqs.sparkflow.stationservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;
import tqs.sparkflow.stationservice.service.OpenChargeMapService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import static org.assertj.core.api.Assertions.assertThat;
import jakarta.servlet.http.HttpServletRequest;
import org.mockito.Mockito;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import tqs.sparkflow.stationservice.StationServiceApplication;
import tqs.sparkflow.stationservice.model.Station;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {
    StationServiceApplication.class,
    TestConfig.class,
    SecurityConfig.class,
    WebConfig.class
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
    "openchargemap.api.url=http://dummy-url-for-tests",
    "user.service.url=http://dummy-user-service-url",
    "spring.main.allow-bean-definition-overriding=true",
    "spring.security.user.name=test",
    "spring.security.user.password=test",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration,org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true",
    "spring.jpa.properties.hibernate.format_sql=true",
    "spring.jpa.properties.hibernate.jdbc.time_zone=UTC",
    "spring.jpa.properties.hibernate.query.fail_on_pagination_over_collection_fetch=false",
    "spring.jpa.properties.hibernate.id.new_generator_mappings=false",
    "spring.flyway.enabled=false",
    "spring.flyway.baseline-on-migrate=false"
})
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private OpenChargeMapService openChargeMapService;

    @Mock
    private RestTemplate restTemplate;

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    void contextLoads() {
        // Just checks that the security filter chain bean is created
        assert securityFilterChain != null;
    }

    @Test
    void publicEndpoint_isAccessible() throws Exception {
        mockMvc.perform(get("/api/v1/stations"))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpoint_requiresAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/admin")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "user")))
                .andExpect(status().isForbidden());
    }

    @Test
    void unknownEndpoint_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/some-protected")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("anonymous", "anonymous")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCorsConfiguration() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/api/v1/stations");
        mockRequest.setMethod("GET");
        mockRequest.setServerName("localhost");
        mockRequest.setServerPort(8080);
        mockRequest.setScheme("http");
        
        CorsConfiguration corsConfig = corsSource.getCorsConfiguration(mockRequest);

        assertThat(corsConfig.getAllowedOrigins())
            .containsExactlyInAnyOrder("http://localhost:3000", "http://localhost:8082");
        
        assertThat(corsConfig.getAllowedMethods())
            .containsExactlyInAnyOrder("GET", "POST", "PUT", "DELETE", "OPTIONS");
        
        assertThat(corsConfig.getAllowedHeaders())
            .containsExactly("*");
        
        assertThat(corsConfig.getExposedHeaders())
            .containsExactly("Authorization");
        
        assertThat(corsConfig.getAllowCredentials())
            .isTrue();
        
        assertThat(corsConfig.getMaxAge())
            .isEqualTo(3600L);
    }

    @Test
    void testSecurityFilterChain() throws Exception {
        // Get the security filter chain from the actual configuration
        SecurityFilterChain filterChain = securityFilterChain;

        assertThat(filterChain).isNotNull();
        
        // Verify that the filter chain contains the expected filters
        assertThat(filterChain.getFilters())
            .isNotEmpty()
            .anyMatch(filter -> filter instanceof org.springframework.security.web.context.SecurityContextHolderFilter)
            .anyMatch(filter -> filter instanceof org.springframework.security.web.header.HeaderWriterFilter)
            .anyMatch(filter -> filter instanceof org.springframework.web.filter.CorsFilter)
            .anyMatch(filter -> filter instanceof org.springframework.security.web.authentication.AnonymousAuthenticationFilter)
            .anyMatch(filter -> filter instanceof org.springframework.security.web.access.ExceptionTranslationFilter)
            .anyMatch(filter -> filter instanceof org.springframework.security.web.access.intercept.AuthorizationFilter);
    }

    @RestController
    static class TestStationsController {
        @GetMapping("/api/v1/stations")
        public String stations() {
            return "ok";
        }
    }

    private Station createTestStation(String name) {
        Station station = new Station();
        station.setName(name);
        station.setAddress("Test Address");
        station.setCity("Test City");
        station.setCountry("Test Country");
        station.setLatitude(38.7223);
        station.setLongitude(-9.1393);
        station.setQuantityOfChargers(2);
        station.setStatus("Available");
        return station;
    }
} 
package tqs.sparkflow.stationservice.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import tqs.sparkflow.stationservice.controller.ChargingSessionController;
import tqs.sparkflow.stationservice.controller.StationController;
import tqs.sparkflow.stationservice.service.ChargingSessionService;
import tqs.sparkflow.stationservice.service.StationService;
import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.util.JwtUtil;

@WebMvcTest(controllers = {StationController.class, ChargingSessionController.class})
@Import({SecurityConfig.class, WebConfig.class})
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",
        "spring.security.user.name=test", "spring.security.user.password=test",
        "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.properties.hibernate.jdbc.time_zone=UTC",
        "spring.jpa.properties.hibernate.query.fail_on_pagination_over_collection_fetch=false",
        "spring.jpa.properties.hibernate.id.new_generator_mappings=false",
        "spring.flyway.enabled=false", "spring.flyway.baseline-on-migrate=false",
        "jwt.secret=test-secret-key-for-tests-that-should-be-at-least-32-characters-long",
        "spring.profiles.active=securitytest"})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StationService stationService;

    @MockBean
    private ChargingSessionService chargingSessionService;

    @SuppressWarnings("java:S6813") // Suppress SonarQube deprecation warning for MockBean
    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void whenAccessingPublicEndpoint_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/stations")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void whenAccessingUserEndpoint_thenSuccess() throws Exception {
        // Mock the service behavior
        ChargingSession session = new ChargingSession("1", "1");
        when(chargingSessionService.getSession("1")).thenReturn(session);

        mockMvc.perform(get("/api/v1/charging-sessions/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenAccessingAdminEndpoint_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/stations/1")).andExpect(status().isOk());
    }

    @Test
    void whenAccessingProtectedEndpointWithoutAuth_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/charging-sessions/1")).andExpect(status().isUnauthorized());
    }
}

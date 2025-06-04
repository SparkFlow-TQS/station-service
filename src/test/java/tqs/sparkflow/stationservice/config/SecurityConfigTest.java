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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
    "openchargemap.api.url=http://dummy-url-for-tests",
    "user.service.url=http://dummy-user-service-url",
    "spring.main.allow-bean-definition-overriding=true"
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

    @RestController
    static class TestStationsController {
        @GetMapping("/api/v1/stations")
        public String stations() {
            return "ok";
        }
    }
} 
package tqs.sparkflow.stationservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.mock.mockito.MockBean;
import tqs.sparkflow.stationservice.service.OpenChargeMapService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "openchargemap.api.url=http://dummy-url-for-tests")
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenChargeMapService openChargeMapService;

    @Test
    void contextLoads() {
        // Just checks that the security filter chain bean is created
        assert securityFilterChain != null;
    }
} 
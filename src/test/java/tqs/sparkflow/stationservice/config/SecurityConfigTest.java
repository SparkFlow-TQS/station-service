package tqs.sparkflow.stationservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SecurityConfig.class)
class SecurityConfigTest {
    @Autowired
    private SecurityConfig securityConfig;

    @Test
    void contextLoads() throws Exception {
        assertThat(securityConfig).isNotNull();
        SecurityFilterChain chain = securityConfig.securityFilterChain(null);
        assertThat(chain).isNotNull();
    }
} 
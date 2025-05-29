package tqs.sparkflow.stationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tqs.sparkflow.stationservice.config.TestConfig;

@SpringBootTest(classes = {StationServiceApplication.class, TestConfig.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "openchargemap.api.key=test-key",
    "user.service.url=http://dummy-user-service-url",
    "spring.main.allow-bean-definition-overriding=true"
})
class StationServiceApplicationTest {
    @Test
    void contextLoads() {
        // Context load test
    }
} 
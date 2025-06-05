package tqs.sparkflow.stationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tqs.sparkflow.stationservice.config.TestConfig;

@SpringBootTest(classes = {StationServiceApplication.class, TestConfig.class})
@ActiveProfiles("test")
class StationServiceApplicationTest {

    @Test
    void contextLoads() {
        // Test that the context loads successfully
        System.out.println("Context loaded successfully");
    }
} 
package tqs.sparkflow.station_service.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tqs.sparkflow.station_service.StationServiceApplication;

@CucumberContextConfiguration
@SpringBootTest(
    classes = {StationServiceApplication.class, CucumberTestConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.profiles.active=test",
        "spring.main.allow-bean-definition-overriding=true"
    }
)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
} 
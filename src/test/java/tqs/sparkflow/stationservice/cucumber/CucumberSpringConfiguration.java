package tqs.sparkflow.stationservice.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tqs.sparkflow.stationservice.StationServiceApplication;
import tqs.sparkflow.stationservice.TestcontainersConfiguration;
import tqs.sparkflow.stationservice.config.CucumberTestConfig;
import tqs.sparkflow.stationservice.config.TestConfig;

@CucumberContextConfiguration
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {
        StationServiceApplication.class,
        TestConfig.class,
        TestcontainersConfiguration.class,
        CucumberTestConfig.class
    },
    properties = {"spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
}

package tqs.sparkflow.stationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class StationServiceApplicationTests {

  @Test
  void contextLoads() {
    // This test verifies that the Spring application context loads successfully.
    // It's intentionally empty because the test passes if no exceptions are thrown
    // during context initialization. If there are any configuration issues,
    // the context will fail to load and the test will fail.
  }
}

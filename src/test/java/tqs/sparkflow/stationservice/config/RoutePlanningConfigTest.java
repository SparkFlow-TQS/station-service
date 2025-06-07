package tqs.sparkflow.stationservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = RoutePlanningConfig.class)
@TestPropertySource(properties = {"route.planning.min-battery-percentage=0.2",
    "route.planning.max-battery-percentage=0.8", "route.planning.max-detour-distance=20.0",
    "route.planning.requests-per-second=10.0"})
public class RoutePlanningConfigTest {

  @Autowired
  private RoutePlanningConfig config;

  @Test
  void whenPropertiesSet_thenConfigValuesAreCorrect() {
    assertEquals(0.2, config.getMinBatteryPercentage());
    assertEquals(0.8, config.getMaxBatteryPercentage());
    assertEquals(20.0, config.getMaxDetourDistance());
    assertEquals(10.0, config.getRequestsPerSecond());
  }
}

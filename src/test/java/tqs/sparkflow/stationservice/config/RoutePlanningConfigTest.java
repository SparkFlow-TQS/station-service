package tqs.sparkflow.stationservice.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"route.planning.min-battery-percentage=0.2",
    "route.planning.max-battery-percentage=0.8", "route.planning.max-detour-distance=20.0",
    "route.planning.requests-per-second=10.0"})
class RoutePlanningConfigTest {

  @Autowired
  private RoutePlanningConfig config;

  @Test
  void whenPropertiesSet_thenConfigValuesAreCorrect() {
    assertThat(config.getMinBatteryPercentage()).isEqualTo(0.2);
    assertThat(config.getMaxBatteryPercentage()).isEqualTo(0.8);
    assertThat(config.getMaxDetourDistance()).isEqualTo(20.0);
    assertThat(config.getRequestsPerSecond()).isEqualTo(10.0);
  }
}

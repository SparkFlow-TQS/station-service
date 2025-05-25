package tqs.sparkflow.stationservice.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import tqs.sparkflow.stationservice.StationServiceApplication;
import tqs.sparkflow.stationservice.TestcontainersConfiguration;
import tqs.sparkflow.stationservice.config.TestConfig;
import tqs.sparkflow.stationservice.repository.StationRepository;

@SpringBootTest(
    classes = {StationServiceApplication.class, TestConfig.class, TestcontainersConfiguration.class},
    properties = {"spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles("test")
@TestPropertySource(properties = {"openchargemap.api.key=invalid-test-key"})
class OpenChargeMapServiceInvalidKeyIT {

  @Autowired private OpenChargeMapService openChargeMapService;

  @Autowired private StationRepository stationRepository;

  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  void setUp() {
    stationRepository.deleteAll();
  }

  @Test
  void whenPopulatingStationsWithInvalidApiKey_thenThrowsException() {
    // Given
    double latitude = 38.7223;
    double longitude = -9.1393;
    int radius = 10;

    // When/Then
    assertThatThrownBy(() -> openChargeMapService.populateStations(latitude, longitude, radius))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("No stations found");
  }
}

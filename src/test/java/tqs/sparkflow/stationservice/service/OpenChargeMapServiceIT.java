package tqs.sparkflow.stationservice.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import tqs.sparkflow.stationservice.StationServiceApplication;
import tqs.sparkflow.stationservice.TestcontainersConfiguration;
import tqs.sparkflow.stationservice.config.TestConfig;
import tqs.sparkflow.stationservice.config.OpenChargeMapTestConfig;
import tqs.sparkflow.stationservice.repository.StationRepository;

@SpringBootTest(
    classes = {
        StationServiceApplication.class,
        TestConfig.class,
        TestcontainersConfiguration.class,
        OpenChargeMapTestConfig.class
    },
    properties = {"spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"openchargemap.api.key=test-key"})
class OpenChargeMapServiceIT {

  @Autowired
  private StationRepository stationRepository;

  @Mock
  private RestTemplate restTemplate;

  private OpenChargeMapService openChargeMapService;

  @BeforeEach
  void setUp() {
    stationRepository.deleteAll();
    openChargeMapService = new OpenChargeMapService(restTemplate, stationRepository, "test-key", "http://test-url");
  }

  @Test
  void whenPopulatingStationsWithInvalidCoordinates_thenThrowsException() {
    // Given
    double invalidLatitude = 91.0;
    double longitude = -9.1393;
    int radius = 10;

    // When/Then
    assertThatThrownBy(() -> openChargeMapService.populateStations(invalidLatitude, longitude, radius))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Latitude must be between -90 and 90 degrees");
  }

  @Test
  void whenPopulatingStationsWithInvalidRadius_thenThrowsException() {
    // Given
    double latitude = 38.7223;
    double longitude = -9.1393;
    int invalidRadius = -1;

    // When/Then
    assertThatThrownBy(() -> openChargeMapService.populateStations(latitude, longitude, invalidRadius))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Radius must be positive");
  }

  @Test
  void whenApiKeyInvalid_thenThrowsException() {
    // Given
    Mockito.when(restTemplate.exchange(
        anyString(),
        eq(HttpMethod.GET),
        eq(null),
        ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()))
            .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

    // When/Then
    assertThatThrownBy(() -> openChargeMapService.populateStations(38.7223, -9.1393, 10))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Invalid Open Charge Map API key");
  }

  @Test
  void whenPopulatingStationsWithInvalidApiKey_thenThrowsException() {
    // Given
    double latitude = 38.7223;
    double longitude = -9.1393;
    int radius = 10;

    // Mock RestTemplate to throw 401 Unauthorized
    Mockito.lenient().when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            eq(null),
            ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()))
        .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

    // When/Then
    assertThatThrownBy(() -> openChargeMapService.populateStations(latitude, longitude, radius))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Invalid Open Charge Map API key");
  }
}

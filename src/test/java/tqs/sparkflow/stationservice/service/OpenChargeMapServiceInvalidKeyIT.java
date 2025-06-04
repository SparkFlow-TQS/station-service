package tqs.sparkflow.stationservice.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;

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
import tqs.sparkflow.stationservice.repository.StationRepository;
import org.mockito.ArgumentMatchers;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;

@SpringBootTest(
    classes = {StationServiceApplication.class, TestConfig.class, TestcontainersConfiguration.class},
    properties = {"spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"openchargemap.api.key=invalid-test-key"})
class OpenChargeMapServiceInvalidKeyIT {

  @Autowired private OpenChargeMapService openChargeMapService;

  @Autowired private StationRepository stationRepository;

  @Mock
  private RestTemplate restTemplate;

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
        .hasMessage("Access denied to Open Charge Map API");
  }
}

package tqs.sparkflow.stationservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import tqs.sparkflow.stationservice.StationServiceApplication;
import tqs.sparkflow.stationservice.TestcontainersConfiguration;
import tqs.sparkflow.stationservice.config.TestConfig;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;

@SpringBootTest(
    classes = {StationServiceApplication.class, TestConfig.class, TestcontainersConfiguration.class},
    properties = {"spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
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
  void whenPopulatingStationsWithValidCoordinates_thenStationsAreCreated() {
    // Given
    List<Map<String, Object>> mockResponse = createMockResponse();
    ResponseEntity<List<Map<String, Object>>> responseEntity =
        new ResponseEntity<>(mockResponse, HttpStatus.OK);
    Mockito.when(restTemplate.exchange(
        anyString(),
        eq(HttpMethod.GET),
        eq(null),
        ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()))
            .thenReturn(responseEntity);

    // When
    List<Station> result = openChargeMapService.populateStations(38.7223, -9.1393, 10);

    // Then
    assertThat(result).isNotEmpty();
    assertThat(result.get(0).getName()).isEqualTo("Test Station");
    assertThat(stationRepository.findAll()).isNotEmpty();
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

  private List<Map<String, Object>> createMockResponse() {
    List<Map<String, Object>> response = new ArrayList<>();
    Map<String, Object> station = new HashMap<>();
    
    Map<String, Object> addressInfo = new HashMap<>();
    addressInfo.put("Title", "Test Station");
    addressInfo.put("AddressLine1", "Test Address");
    addressInfo.put("Town", "Test Town");
    addressInfo.put("StateOrProvince", "Test State");
    addressInfo.put("Postcode", "12345");
    addressInfo.put("Country", Map.of("Title", "Test Country"));
    addressInfo.put("Latitude", 38.7223);
    addressInfo.put("Longitude", -9.1393);

    Map<String, Object> connection = new HashMap<>();
    connection.put("ConnectionType", Map.of("Title", "Type 2"));
    connection.put("PowerKW", 22.0);
    connection.put("StatusType", Map.of("IsOperational", true));

    station.put("ID", 1);
    station.put("AddressInfo", addressInfo);
    station.put("Connections", List.of(connection));
    
    response.add(station);
    return response;
  }
}

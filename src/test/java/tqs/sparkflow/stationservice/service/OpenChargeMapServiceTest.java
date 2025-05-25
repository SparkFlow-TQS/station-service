package tqs.sparkflow.stationservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tqs.sparkflow.stationservice.repository.StationRepository;
import tqs.sparkflow.stationservice.model.Station;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenChargeMapServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private StationRepository stationRepository;

  private OpenChargeMapService service;

  private final String baseUrl = "https://api.openchargemap.io/v3/poi";

  @BeforeEach
  void setUp() {
    service = new OpenChargeMapService(restTemplate, stationRepository, "test-api-key", baseUrl);
    ReflectionTestUtils.setField(service, "baseUrl", baseUrl);
  }

  @Test
  void whenPopulatingStations_thenStationsAreSaved() {
    // Given
    List<Map<String, Object>> mockResponse = createMockResponse();
    ResponseEntity<List<Map<String, Object>>> responseEntity =
        new ResponseEntity<>(mockResponse, HttpStatus.OK);
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null),
        any(ParameterizedTypeReference.class))).thenReturn(responseEntity);
    when(stationRepository.saveAll(any())).thenReturn(convertToStations(mockResponse));

    // When
    List<Station> result = service.populateStations(38.7223, -9.1393, 50);

    // Then
    assertThat(result).hasSize(1);
    verify(stationRepository).saveAll(any());
  }

  @Test
  void whenNoStationsFound_thenThrowsException() {
    // Given
    ResponseEntity<List<Map<String, Object>>> responseEntity =
        new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null),
        any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

    // When/Then
    assertThatThrownBy(() -> service.populateStations(38.7223, -9.1393, 50))
        .isInstanceOf(IllegalStateException.class).hasMessageContaining("No stations found");
  }

  @Test
  void whenApiKeyInvalid_thenThrowsException() {
    // Given
    when(restTemplate.exchange(
        anyString(),
        eq(HttpMethod.GET),
        eq(null),
        any(ParameterizedTypeReference.class)
    )).thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

    // When/Then
    assertThatThrownBy(() -> service.populateStations(38.7223, -9.1393, 50))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Invalid Open Charge Map API key");
  }

  @Test
  void whenApiAccessDenied_thenThrowsException() {
    // Given
    when(restTemplate.exchange(
        anyString(),
        eq(HttpMethod.GET),
        eq(null),
        any(ParameterizedTypeReference.class)
    )).thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));

    // When/Then
    assertThatThrownBy(() -> service.populateStations(38.7223, -9.1393, 50))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Access denied");
  }

  private List<Map<String, Object>> createMockResponse() {
    List<Map<String, Object>> response = new ArrayList<>();
    Map<String, Object> station = new HashMap<>();

    Map<String, Object> addressInfo = new HashMap<>();
    addressInfo.put("Title", "Test Station");
    addressInfo.put("AddressLine1", "Test Address");
    addressInfo.put("Latitude", 38.7223);
    addressInfo.put("Longitude", -9.1393);

    List<Map<String, Object>> connections = new ArrayList<>();
    Map<String, Object> connection = new HashMap<>();
    connection.put("ConnectionTypeID", "1");
    connections.add(connection);

    station.put("ID", 1);
    station.put("AddressInfo", addressInfo);
    station.put("Connections", connections);

    response.add(station);
    return response;
  }

  private List<Station> convertToStations(List<Map<String, Object>> stationsData) {
    return stationsData.stream().map(data -> {
      try {
        Map<String, Object> addressInfo = (Map<String, Object>) data.get("AddressInfo");
        List<Map<String, Object>> connections = (List<Map<String, Object>>) data.get("Connections");

        Station station = new Station();

        // Handle ID which could be Integer or String
        Object id = data.get("ID");
        if (id != null) {
          if (id instanceof Number) {
            station.setId(((Number) id).longValue());
          } else {
            station.setId(Long.parseLong(id.toString()));
          }
        }

        // Handle name which could be null
        Object name = addressInfo.get("Title");
        station.setName(name != null ? name.toString() : "Unknown");

        // Handle address which could be null
        Object address = addressInfo != null ? addressInfo.get("AddressLine1") : null;
        station.setAddress(address != null ? address.toString() : "Unknown");

        // Handle coordinates which could be Double
        Object lat = addressInfo != null ? addressInfo.get("Latitude") : null;
        Object lon = addressInfo != null ? addressInfo.get("Longitude") : null;
        station.setLatitude(lat != null ? ((Number) lat).doubleValue() : 0.0);
        station.setLongitude(lon != null ? ((Number) lon).doubleValue() : 0.0);

        station.setStatus("Available");

        // Handle connector type which could be null
        if (connections != null && !connections.isEmpty()) {
          Map<String, Object> firstConnection = connections.get(0);
          Object connectorType = firstConnection.get("ConnectionTypeID");
          station.setConnectorType(connectorType != null ? connectorType.toString() : "Unknown");
        } else {
          station.setConnectorType("Unknown");
        }

        return station;
      } catch (Exception e) {
        throw new IllegalStateException("Error converting station data: " + e.getMessage());
      }
    }).toList();
  }
}

package tqs.sparkflow.station_service.service;

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
import tqs.sparkflow.station_service.repository.StationRepository;

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
        service = new OpenChargeMapService("test-api-key", stationRepository);
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(service, "baseUrl", baseUrl);
    }

    @Test
    void whenPopulatingStations_thenStationsAreSaved() {
        // Given
        List<Map<String, Object>> mockResponse = createMockResponse();
        ResponseEntity<List<Map<String, Object>>> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);
        when(stationRepository.saveAll(any())).thenReturn(new ArrayList<>());

        // When
        String result = service.populateStations(38.7223, -9.1393, 50);

        // Then
        assertThat(result).isEqualTo("Successfully populated 1 stations");
        verify(stationRepository).saveAll(any());
    }

    @Test
    void whenNoStationsFound_thenThrowsException() {
        // Given
        ResponseEntity<List<Map<String, Object>>> responseEntity = new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // When/Then
        assertThatThrownBy(() -> service.populateStations(38.7223, -9.1393, 50))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No stations found");
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
} 
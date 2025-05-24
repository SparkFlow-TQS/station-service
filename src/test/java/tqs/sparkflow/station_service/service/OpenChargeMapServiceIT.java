package tqs.sparkflow.station_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import tqs.sparkflow.station_service.StationServiceApplication;
import tqs.sparkflow.station_service.config.TestConfig;
import tqs.sparkflow.station_service.repository.StationRepository;

@SpringBootTest(
    classes = {StationServiceApplication.class, TestConfig.class}
)
@ActiveProfiles("test")
class OpenChargeMapServiceIT {

    @Autowired
    private OpenChargeMapService openChargeMapService;

    @Autowired
    private StationRepository stationRepository;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        stationRepository.deleteAll();
    }

    @Test
    void whenPopulatingStationsWithValidCoordinates_thenStationsAreCreated() {
        // Given
        List<Map<String, Object>> mockResponse = createMockResponse();
        ResponseEntity<List<Map<String, Object>>> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            eq(null),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        // When
        String result = openChargeMapService.populateStations(38.7223, -9.1393, 10);

        // Then
        assertThat(result).contains("Successfully populated");
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
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void whenPopulatingStationsWithInvalidRadius_thenThrowsException() {
        // Given
        double latitude = 38.7223;
        double longitude = -9.1393;
        int invalidRadius = 101;

        // When/Then
        assertThatThrownBy(() -> openChargeMapService.populateStations(latitude, longitude, invalidRadius))
            .isInstanceOf(IllegalStateException.class);
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
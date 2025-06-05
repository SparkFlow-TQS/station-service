package tqs.sparkflow.stationservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import tqs.sparkflow.stationservice.model.OpenChargeMapResponse;
import tqs.sparkflow.stationservice.model.OpenChargeMapStation;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OpenChargeMapServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private OpenChargeMapService service;

    private final String apiKey = "test-api-key";
    private final String baseUrl = "http://test-api.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(service, "apiKey", apiKey);
        ReflectionTestUtils.setField(service, "baseUrl", baseUrl);
    }

    @Test
    @XrayTest(key = "OCM-2")
    @Requirement("OCM-2")
    void getStationsByCity_returnsEmptyListOnNullResponse() {
        // Given
        lenient().when(restTemplate.getForObject(anyString(), eq(OpenChargeMapResponse.class)))
            .thenReturn(null);
        
        // When
        List<Station> stations = service.getStationsByCity("City");
        
        // Then
        assertThat(stations).isEmpty();
    }

    @Test
    @XrayTest(key = "OCM-4")
    @Requirement("OCM-4")
    void populateStations_invalidLatitude_throwsException() {
        assertThatThrownBy(() -> service.populateStations(-100, 0, 10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Latitude must be between -90 and 90 degrees");
    }

    @Test
    void populateStations_invalidLongitude_throwsException() {
        assertThatThrownBy(() -> service.populateStations(0, -200, 10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Longitude must be between -180 and 180 degrees");
    }

    @Test
    void populateStations_invalidRadius_throwsException() {
        assertThatThrownBy(() -> service.populateStations(0, 0, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Radius must be positive");
    }

    @Test
    @XrayTest(key = "OCM-1")
    @Requirement("OCM-1")
    void getStationsByCity_returnsStations() {
        // Given
        OpenChargeMapStation ocmStation = new OpenChargeMapStation();
        ocmStation.setId("1");
        ocmStation.setName("Test Station");
        ocmStation.setAddress("Test Address");
        ocmStation.setCity("Test City");
        ocmStation.setCountry("Test Country");
        ocmStation.setLatitude(1.0);
        ocmStation.setLongitude(2.0);
        ocmStation.setQuantityOfChargers(1);
        ocmStation.setStatus("Available");

        OpenChargeMapResponse response = new OpenChargeMapResponse();
        response.setStations(List.of(ocmStation));
        
        // Use specific URL match to ensure mock is called
        String expectedUrl = String.format("%s?key=%s&city=%s", baseUrl, apiKey, "Test City");
        when(restTemplate.getForObject(eq(expectedUrl), eq(OpenChargeMapResponse.class)))
            .thenReturn(response);

        // When
        List<Station> stations = service.getStationsByCity("Test City");

        // Then
        assertThat(stations).hasSize(1);
        assertThat(stations.get(0).getName()).isEqualTo("Test Station");
        assertThat(stations.get(0).getAddress()).isEqualTo("Test Address");
        assertThat(stations.get(0).getCity()).isEqualTo("Test City");
    }

    @Test
    void getStationsByCity_withNullStation_skipsNullStation() {
        // Given
        OpenChargeMapStation ocmStation1 = new OpenChargeMapStation();
        ocmStation1.setId("1");
        ocmStation1.setName("Valid Station");
        ocmStation1.setAddress("Valid Address");
        ocmStation1.setCity("Test City");
        ocmStation1.setCountry("Test Country");
        ocmStation1.setLatitude(1.0);
        ocmStation1.setLongitude(2.0);
        ocmStation1.setQuantityOfChargers(1);
        ocmStation1.setStatus("Available");

        OpenChargeMapResponse response = new OpenChargeMapResponse();
        List<OpenChargeMapStation> stations = new ArrayList<>();
        stations.add(ocmStation1);
        stations.add(null);  // Add null station
        response.setStations(stations);

        // Use specific URL match to ensure mock is called
        String expectedUrl = String.format("%s?key=%s&city=%s", baseUrl, apiKey, "Test City");
        when(restTemplate.getForObject(eq(expectedUrl), eq(OpenChargeMapResponse.class)))
            .thenReturn(response);

        // When
        List<Station> result = service.getStationsByCity("Test City");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Valid Station");
    }
}

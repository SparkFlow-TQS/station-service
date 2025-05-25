package tqs.sparkflow.stationservice.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tqs.sparkflow.stationservice.model.OpenChargeMapResponse;
import tqs.sparkflow.stationservice.model.OpenChargeMapStation;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;

class OpenChargeMapServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private OpenChargeMapService service;

    private final String apiKey = "dummy-key";
    private final String baseUrl = "http://dummy-url";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new OpenChargeMapService(restTemplate, stationRepository, apiKey, baseUrl);
    }

    @Test
    void getStationsByCity_returnsStations() {
        OpenChargeMapStation ocmStation = new OpenChargeMapStation();
        ocmStation.setId("1");
        ocmStation.setName("Test");
        ocmStation.setAddress("Addr");
        ocmStation.setCity("City");
        ocmStation.setCountry("Country");
        ocmStation.setLatitude(1.0);
        ocmStation.setLongitude(2.0);
        ocmStation.setConnectorType("Type2");

        OpenChargeMapResponse response = new OpenChargeMapResponse();
        response.setStations(List.of(ocmStation));

        when(restTemplate.getForObject(anyString(), eq(OpenChargeMapResponse.class))).thenReturn(response);

        List<Station> stations = service.getStationsByCity("City");
        assertThat(stations).hasSize(1);
        assertThat(stations.get(0).getName()).isEqualTo("Test");
    }

    @Test
    void getStationsByCity_returnsEmptyListOnNullResponse() {
        when(restTemplate.getForObject(anyString(), eq(OpenChargeMapResponse.class))).thenReturn(null);
        List<Station> stations = service.getStationsByCity("City");
        assertThat(stations).isEmpty();
    }

    @Test
    void populateStations_happyPath() {
        double lat = 1.0, lon = 2.0;
        int radius = 10;
        Map<String, Object> addressInfo = Map.of(
            "Title", "Test", "AddressLine1", "Addr", "Latitude", lat, "Longitude", lon
        );
        Map<String, Object> connection = Map.of("ConnectionTypeID", "Type2");
        Map<String, Object> stationData = new HashMap<>();
        stationData.put("ID", 1);
        stationData.put("AddressInfo", addressInfo);
        stationData.put("Connections", List.of(connection));
        List<Map<String, Object>> responseBody = List.of(stationData);

        ResponseEntity<List<Map<String, Object>>> responseEntity =
            new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()))
            .thenReturn(responseEntity);
        when(stationRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Station> stations = service.populateStations(lat, lon, radius);
        assertThat(stations).hasSize(1);
        assertThat(stations.get(0).getName()).isEqualTo("Test");
    }

    @Test
    void populateStations_invalidLatitude_throwsException() {
        assertThatThrownBy(() -> service.populateStations(-100, 0, 10))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void populateStations_invalidLongitude_throwsException() {
        assertThatThrownBy(() -> service.populateStations(0, -200, 10))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void populateStations_invalidRadius_throwsException() {
        assertThatThrownBy(() -> service.populateStations(0, 0, 0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void populateStations_noStationsFound_throwsException() {
        ResponseEntity<List<Map<String, Object>>> responseEntity =
            new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()))
            .thenReturn(responseEntity);

        assertThatThrownBy(() -> service.populateStations(1, 2, 10))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No stations found");
    }

    @Test
    void populateStations_unauthorized_throwsException() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()))
            .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        assertThatThrownBy(() -> service.populateStations(1, 2, 10))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Invalid Open Charge Map API key");
    }

    @Test
    void populateStations_forbidden_throwsException() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()))
            .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));

        assertThatThrownBy(() -> service.populateStations(1, 2, 10))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Access denied");
    }

    @Test
    void populateStations_otherHttpError_throwsException() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad request"));

        assertThatThrownBy(() -> service.populateStations(1, 2, 10))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Error accessing Open Charge Map API");
    }

    @Test
    void populateStations_otherException_throwsException() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()))
            .thenThrow(new RuntimeException("Some error"));

        assertThatThrownBy(() -> service.populateStations(1, 2, 10))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Error fetching stations");
    }
}

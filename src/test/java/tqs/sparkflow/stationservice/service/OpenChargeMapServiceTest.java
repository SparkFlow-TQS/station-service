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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
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
        ReflectionTestUtils.setField(service, "apiKey", apiKey);
        ReflectionTestUtils.setField(service, "baseUrl", baseUrl);
    }

    @Test
    @XrayTest(key = "OCM-1")
    @Requirement("OCM-1")
    void getStationsByCity_returnsStations() {
        OpenChargeMapStation ocmStation = new OpenChargeMapStation();
        ocmStation.setId("1");
        ocmStation.setName("Test");
        ocmStation.setAddress("Addr");
        ocmStation.setCity("City");
        ocmStation.setCountry("Country");
        ocmStation.setLatitude(1.0);
        ocmStation.setLongitude(2.0);
        ocmStation.setQuantityOfChargers(1);

        OpenChargeMapResponse response = new OpenChargeMapResponse();
        response.setStations(List.of(ocmStation));

        lenient().when(restTemplate.getForObject(anyString(), eq(OpenChargeMapResponse.class))).thenReturn(response);

        List<Station> stations = service.getStationsByCity("City");
        assertThat(stations).hasSize(1);
        assertThat(stations.get(0).getName()).isEqualTo("Test");
    }

    @Test
    @XrayTest(key = "OCM-2")
    @Requirement("OCM-2")
    void getStationsByCity_returnsEmptyListOnNullResponse() {
        lenient().when(restTemplate.getForObject(anyString(), eq(OpenChargeMapResponse.class))).thenReturn(null);
        List<Station> stations = service.getStationsByCity("City");
        assertThat(stations).isEmpty();
    }

    @Test
    @XrayTest(key = "OCM-3")
    @Requirement("OCM-3")
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

        lenient().when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()
        )).thenReturn(responseEntity);

        lenient().when(stationRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Station> stations = service.populateStations(lat, lon, radius);
        assertThat(stations).hasSize(1);
        assertThat(stations.get(0).getName()).isEqualTo("Test");
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
    void populateStations_noStationsFound_throwsException() {
        ResponseEntity<List<Map<String, Object>>> responseEntity =
            new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);

        lenient().when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()
        )).thenReturn(responseEntity);

        assertThatThrownBy(() -> service.populateStations(1, 2, 10))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No stations found");
    }

    @Test
    void populateStations_unauthorized_throwsException() {
        lenient().when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()
        )).thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        assertThatThrownBy(() -> service.populateStations(1, 2, 10))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Invalid Open Charge Map API key");
    }

    @Test
    void populateStations_forbidden_throwsException() {
        lenient().when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()
        )).thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));

        assertThatThrownBy(() -> service.populateStations(1, 2, 10))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Access denied");
    }

    @Test
    void populateStations_otherHttpError_throwsException() {
        lenient().when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad request"));

        assertThatThrownBy(() -> service.populateStations(1, 2, 10))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Error accessing Open Charge Map API");
    }

    @Test
    void populateStations_otherException_throwsException() {
        lenient().when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()
        )).thenThrow(new RuntimeException("Some error"));

        assertThatThrownBy(() -> service.populateStations(1, 2, 10))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Error fetching stations");
    }

    @Test
    void getStationsByCity_httpError_throwsException() {
        lenient().when(restTemplate.getForObject(anyString(), eq(OpenChargeMapResponse.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> service.getStationsByCity("City"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Error accessing Open Charge Map API: 400 BAD_REQUEST");
    }

    @Test
    void getStationsByCity_otherException_throwsException() {
        lenient().when(restTemplate.getForObject(anyString(), eq(OpenChargeMapResponse.class)))
            .thenThrow(new RuntimeException("Network error"));

        assertThatThrownBy(() -> service.getStationsByCity("City"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Error fetching stations");
    }

    @Test
    void getStationsByCity_withNullStation_skipsNullStation() {
        OpenChargeMapStation ocmStation1 = new OpenChargeMapStation();
        ocmStation1.setId("1");
        ocmStation1.setName("Test1");
        ocmStation1.setAddress("Addr1");
        ocmStation1.setCity("City");
        ocmStation1.setCountry("Country");
        ocmStation1.setLatitude(1.0);
        ocmStation1.setLongitude(2.0);
        ocmStation1.setQuantityOfChargers(1);

        OpenChargeMapResponse response = new OpenChargeMapResponse();
        List<OpenChargeMapStation> stations = new ArrayList<>();
        stations.add(ocmStation1);
        stations.add(null);
        response.setStations(stations);

        lenient().when(restTemplate.getForObject(anyString(), eq(OpenChargeMapResponse.class))).thenReturn(response);

        List<Station> result = service.getStationsByCity("City");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test1");
    }

    @Test
    void populateStations_withMultipleConnections_calculatesTotalChargers() {
        double lat = 1.0, lon = 2.0;
        int radius = 10;
        Map<String, Object> addressInfo = Map.of(
            "Title", "Test", "AddressLine1", "Addr", "Latitude", lat, "Longitude", lon
        );
        Map<String, Object> connection1 = Map.of("ConnectionTypeID", "Type2", "Quantity", 2);
        Map<String, Object> connection2 = Map.of("ConnectionTypeID", "Type2", "Quantity", 3);
        Map<String, Object> stationData = new HashMap<>();
        stationData.put("ID", 1);
        stationData.put("AddressInfo", addressInfo);
        stationData.put("Connections", List.of(connection1, connection2));
        List<Map<String, Object>> responseBody = List.of(stationData);

        ResponseEntity<List<Map<String, Object>>> responseEntity =
            new ResponseEntity<>(responseBody, HttpStatus.OK);

        lenient().when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()
        )).thenReturn(responseEntity);

        lenient().when(stationRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Station> stations = service.populateStations(lat, lon, radius);
        assertThat(stations).hasSize(1);
        assertThat(stations.get(0).getQuantityOfChargers()).isEqualTo(5);
    }

    @Test
    void populateStations_withInvalidConnectionQuantity_defaultsToOne() {
        double lat = 1.0, lon = 2.0;
        int radius = 10;
        Map<String, Object> addressInfo = Map.of(
            "Title", "Test", "AddressLine1", "Addr", "Latitude", lat, "Longitude", lon
        );
        Map<String, Object> connection = Map.of("ConnectionTypeID", "Type2", "Quantity", "invalid");
        Map<String, Object> stationData = new HashMap<>();
        stationData.put("ID", 1);
        stationData.put("AddressInfo", addressInfo);
        stationData.put("Connections", List.of(connection));
        List<Map<String, Object>> responseBody = List.of(stationData);

        ResponseEntity<List<Map<String, Object>>> responseEntity =
            new ResponseEntity<>(responseBody, HttpStatus.OK);

        lenient().when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()
        )).thenReturn(responseEntity);

        lenient().when(stationRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Station> stations = service.populateStations(lat, lon, radius);
        assertThat(stations).hasSize(1);
        assertThat(stations.get(0).getQuantityOfChargers()).isEqualTo(1);
    }

    @Test
    void populateStations_withMissingAddressInfo_usesDefaultValues() {
        double lat = 1.0, lon = 2.0;
        int radius = 10;
        Map<String, Object> stationData = new HashMap<>();
        stationData.put("ID", 1);
        stationData.put("AddressInfo", new HashMap<>());
        stationData.put("Connections", List.of(Map.of("ConnectionTypeID", "Type2")));
        List<Map<String, Object>> responseBody = List.of(stationData);

        ResponseEntity<List<Map<String, Object>>> responseEntity =
            new ResponseEntity<>(responseBody, HttpStatus.OK);

        lenient().when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()
        )).thenReturn(responseEntity);

        lenient().when(stationRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Station> stations = service.populateStations(lat, lon, radius);
        assertThat(stations).hasSize(1);
        assertThat(stations.get(0).getName()).isEqualTo("Unknown");
        assertThat(stations.get(0).getAddress()).isEqualTo("Unknown");
    }

    @Test
    void populateStations_withInvalidStationData_throwsException() {
        double lat = 1.0, lon = 2.0;
        int radius = 10;
        Map<String, Object> stationData = new HashMap<>();
        stationData.put("ID", "invalid");
        stationData.put("AddressInfo", "not-a-map");
        stationData.put("Connections", "not-a-list");
        List<Map<String, Object>> responseBody = List.of(stationData);

        ResponseEntity<List<Map<String, Object>>> responseEntity =
            new ResponseEntity<>(responseBody, HttpStatus.OK);

        lenient().when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()
        )).thenReturn(responseEntity);

        assertThatThrownBy(() -> service.populateStations(lat, lon, radius))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Error converting station data");
    }
}

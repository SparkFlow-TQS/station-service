package tqs.sparkflow.stationservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import tqs.sparkflow.stationservice.model.OpenChargeMapResponse;
import tqs.sparkflow.stationservice.model.OpenChargeMapStation;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
class OpenChargeMapServiceTest {

        @Mock
        private RestTemplate restTemplate;

        @Mock
        private StationRepository stationRepository;

        private OpenChargeMapService service;

        private final String apiKey = "test-api-key";
        private final String baseUrl = "http://test-api.com";

        @BeforeEach
        void setUp() {
                // Create service manually with mocked dependencies
                service = new OpenChargeMapService(restTemplate, stationRepository, apiKey,
                                baseUrl);
        }

        @Test
        @XrayTest(key = "OCM-2")
        @Requirement("OCM-2")
        void getStationsByCity_returnsEmptyListOnNullResponse() {
                // Given
                lenient().when(restTemplate.getForObject(anyString(),
                                eq(OpenChargeMapResponse.class))).thenReturn(null);

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
                String cityParam = "Test City";
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

                // Use anyString() for URL to handle any URL formatting issues
                when(restTemplate.getForObject(anyString(), eq(OpenChargeMapResponse.class)))
                                .thenReturn(response);

                // When
                List<Station> stations = service.getStationsByCity(cityParam);

                // Then
                assertThat(stations).hasSize(1);
                assertThat(stations.get(0).getName()).isEqualTo("Test Station");
                assertThat(stations.get(0).getAddress()).isEqualTo("Test Address");
                assertThat(stations.get(0).getCity()).isEqualTo("Test City");
        }

        @Test
        void getStationsByCity_withNullStation_skipsNullStation() {
                // Given
                String cityParam = "Test City";
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
                stations.add(null); // Add null station
                response.setStations(stations);

                // Use anyString() for URL to handle any URL formatting issues
                when(restTemplate.getForObject(anyString(), eq(OpenChargeMapResponse.class)))
                                .thenReturn(response);

                // When
                List<Station> result = service.getStationsByCity(cityParam);

                // Then
                assertThat(result).hasSize(1);
                assertThat(result.get(0).getName()).isEqualTo("Valid Station");
        }

        @Test
        @XrayTest(key = "OCM-SVC-1")
        @Requirement("OCM-SVC-1")
        void whenPopulatingStations_thenReturnsSavedStations() {
                // Given
                double latitude = 40.123;
                double longitude = -8.456;
                int radius = 10;

                // Create mock station data
                Map<String, Object> addressInfo = new HashMap<>();
                addressInfo.put("Title", "Test Station");
                addressInfo.put("AddressLine1", "Test Address");
                addressInfo.put("Town", "Test City");
                addressInfo.put("Country", "Test Country");
                addressInfo.put("Latitude", latitude);
                addressInfo.put("Longitude", longitude);

                Map<String, Object> stationData = new HashMap<>();
                stationData.put("ID", 123);
                stationData.put("AddressInfo", addressInfo);
                stationData.put("Connections", new ArrayList<>());

                List<Map<String, Object>> stationsData = List.of(stationData);
                ResponseEntity<List<Map<String, Object>>> mockResponse =
                                new ResponseEntity<>(stationsData, HttpStatus.OK);

                List<Station> expectedStations = List.of(new Station());

                when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null),
                                any(ParameterizedTypeReference.class))).thenReturn(mockResponse);
                when(stationRepository.saveAll(any())).thenReturn(expectedStations);

                // When
                List<Station> result = service.populateStations(latitude, longitude, radius);

                // Then
                assertThat(result).isEqualTo(expectedStations);
                verify(stationRepository).saveAll(any());
        }

        @Test
        @XrayTest(key = "OCM-SVC-2")
        @Requirement("OCM-SVC-2")
        void whenPopulatingStationsWithInvalidLatitude_thenThrowsException() {
                // When/Then
                assertThatThrownBy(() -> service.populateStations(91.0, -8.456, 10))
                                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining(
                                                "Latitude must be between -90 and 90 degrees");
        }

        @Test
        @XrayTest(key = "OCM-SVC-3")
        @Requirement("OCM-SVC-3")
        void whenPopulatingStationsWithInvalidLongitude_thenThrowsException() {
                // When/Then
                assertThatThrownBy(() -> service.populateStations(40.123, 181.0, 10))
                                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining(
                                                "Longitude must be between -180 and 180 degrees");
        }

        @Test
        @XrayTest(key = "OCM-SVC-4")
        @Requirement("OCM-SVC-4")
        void whenPopulatingStationsWithInvalidRadius_thenThrowsException() {
                // When/Then
                assertThatThrownBy(() -> service.populateStations(40.123, -8.456, 0))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessageContaining("Radius must be positive");
        }

        @Test
        @XrayTest(key = "OCM-SVC-5")
        @Requirement("OCM-SVC-5")
        void whenPopulatingStationsWithUnauthorizedError_thenThrowsException() {
                // Given
                when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null),
                                any(ParameterizedTypeReference.class)))
                                                .thenThrow(new HttpClientErrorException(
                                                                HttpStatus.UNAUTHORIZED));

                // When/Then
                assertThatThrownBy(() -> service.populateStations(40.123, -8.456, 10))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("Invalid Open Charge Map API key");
        }

        @Test
        @XrayTest(key = "OCM-SVC-6")
        @Requirement("OCM-SVC-6")
        void whenPopulatingStationsWithForbiddenError_thenThrowsException() {
                // Given
                when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null),
                                any(ParameterizedTypeReference.class))).thenThrow(
                                                new HttpClientErrorException(HttpStatus.FORBIDDEN));

                // When/Then
                assertThatThrownBy(() -> service.populateStations(40.123, -8.456, 10))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("Access denied to Open Charge Map API");
        }

        @Test
        @XrayTest(key = "OCM-SVC-7")
        @Requirement("OCM-SVC-7")
        void whenPopulatingStationsWithEmptyResponse_thenThrowsException() {
                // Given
                ResponseEntity<List<Map<String, Object>>> mockResponse =
                                new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);

                when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null),
                                any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

                // When/Then
                assertThatThrownBy(() -> service.populateStations(40.123, -8.456, 10))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("No stations found");
        }

        @Test
        @XrayTest(key = "OCM-SVC-8")
        @Requirement("OCM-SVC-8")
        void whenConvertingStationData_thenReturnsCorrectStation() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                stationData.put("ID", 123);
                stationData.put("AddressInfo", new HashMap<>());
                stationData.put("Connections", new ArrayList<>());

                // When
                Station result = service.convertMapToStation(stationData);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getName()).isEqualTo("Unknown");
                assertThat(result.getAddress()).isEqualTo("Unknown");
                assertThat(result.getLatitude()).isEqualTo(0.0);
                assertThat(result.getLongitude()).isEqualTo(0.0);
                assertThat(result.getCity()).isEqualTo("Unknown");
                assertThat(result.getCountry()).isEqualTo("Unknown");
                assertThat(result.getQuantityOfChargers()).isEqualTo(1);
                assertThat(result.getStatus()).isNull();
        }

        @Test
        @XrayTest(key = "OCM-SVC-9")
        @Requirement("OCM-SVC-9")
        void whenConvertingStationDataWithMissingFields_thenUsesDefaultValues() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                Map<String, Object> addressInfo = new HashMap<>();
                List<Map<String, Object>> connections = new ArrayList<>();

                stationData.put("ID", 123);
                stationData.put("AddressInfo", addressInfo);
                stationData.put("Connections", connections);

                // When
                Station result = service.convertMapToStation(stationData);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getName()).isEqualTo("Unknown");
                assertThat(result.getAddress()).isEqualTo("Unknown");
                assertThat(result.getLatitude()).isEqualTo(0.0);
                assertThat(result.getLongitude()).isEqualTo(0.0);
                assertThat(result.getCity()).isEqualTo("Unknown");
                assertThat(result.getCountry()).isEqualTo("Unknown");
                assertThat(result.getQuantityOfChargers()).isEqualTo(1);
                assertThat(result.getStatus()).isNull();
        }

        @Test
        @XrayTest(key = "OCM-SVC-10")
        @Requirement("OCM-SVC-10")
        void whenConvertingStationDataWithInvalidAddressInfo_thenThrowsException() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                stationData.put("ID", 123);
                stationData.put("AddressInfo", "Invalid"); // Not a Map
                stationData.put("Connections", new ArrayList<>());

                // When/Then
                assertThatThrownBy(() -> service.convertMapToStation(stationData))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("AddressInfo is not a valid map");
        }

        @Test
        @XrayTest(key = "OCM-SVC-11")
        @Requirement("OCM-SVC-11")
        void whenConvertingStationDataWithInvalidConnections_thenThrowsException() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                Map<String, Object> addressInfo = new HashMap<>();

                stationData.put("ID", 123);
                stationData.put("AddressInfo", addressInfo);
                stationData.put("Connections", "Invalid"); // Not a List

                // When/Then
                assertThatThrownBy(() -> service.convertMapToStation(stationData))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("Connections is not a valid list");
        }

        @Test
        void whenConvertingStationDataWithStringId_thenIdIsParsed() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                stationData.put("ID", "123");
                stationData.put("AddressInfo", new HashMap<>());
                stationData.put("Connections", new ArrayList<>());

                // When
                Station result = service.convertMapToStation(stationData);

                // Then
                assertThat(result.getId()).isEqualTo(123L);
        }

        @Test
        void whenConvertingStationDataWithMultipleConnections_thenQuantitiesAreSummed() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                Map<String, Object> addressInfo = new HashMap<>();
                List<Map<String, Object>> connections = new ArrayList<>();

                Map<String, Object> connection1 = new HashMap<>();
                connection1.put("Quantity", 2);
                connections.add(connection1);

                Map<String, Object> connection2 = new HashMap<>();
                connection2.put("Quantity", 3);
                connections.add(connection2);

                stationData.put("ID", 1);
                stationData.put("AddressInfo", addressInfo);
                stationData.put("Connections", connections);

                // When
                Station result = service.convertMapToStation(stationData);

                // Then
                assertThat(result.getQuantityOfChargers()).isEqualTo(5);
        }

        @Test
        @XrayTest(key = "OCM-3")
        @Requirement("OCM-3")
        void getStationsByCity_whenHttpClientError_thenThrowsException() {
                // Given
                when(restTemplate.getForObject(anyString(), eq(OpenChargeMapResponse.class)))
                                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

                // When & Then
                assertThatThrownBy(() -> service.getStationsByCity("City"))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("Error accessing Open Charge Map API");
        }

        @Test
        @XrayTest(key = "OCM-5")
        @Requirement("OCM-5")
        void getStationsByCity_whenGenericError_thenThrowsException() {
                // Given
                when(restTemplate.getForObject(anyString(), eq(OpenChargeMapResponse.class)))
                                .thenThrow(new RuntimeException("Network error"));

                // When & Then
                assertThatThrownBy(() -> service.getStationsByCity("City"))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("Error fetching stations");
        }

        @Test
        void whenConvertingStationDataWithNullValues_thenUsesDefaultValues() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                Map<String, Object> addressInfo = new HashMap<>();
                List<Map<String, Object>> connections = new ArrayList<>();

                stationData.put("ID", null);
                stationData.put("AddressInfo", addressInfo);
                stationData.put("Connections", connections);

                // When
                Station result = service.convertMapToStation(stationData);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isNull();
                assertThat(result.getName()).isEqualTo("Unknown");
                assertThat(result.getAddress()).isEqualTo("Unknown");
                assertThat(result.getLatitude()).isEqualTo(0.0);
                assertThat(result.getLongitude()).isEqualTo(0.0);
                assertThat(result.getCity()).isEqualTo("Unknown");
                assertThat(result.getCountry()).isEqualTo("Unknown");
                assertThat(result.getQuantityOfChargers()).isEqualTo(1);
        }

        @Test
        void whenConvertingStationDataWithInvalidNumberFormat_thenThrowsException() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                Map<String, Object> addressInfo = new HashMap<>();
                addressInfo.put("Latitude", "invalid");
                addressInfo.put("Longitude", "invalid");

                stationData.put("ID", "invalid");
                stationData.put("AddressInfo", addressInfo);
                stationData.put("Connections", new ArrayList<>());

                // When & Then
                assertThatThrownBy(() -> service.convertMapToStation(stationData))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("Error converting station data");
        }

        @Test
        void whenConvertingStationDataWithInvalidConnectionQuantity_thenUsesDefaultValues() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                Map<String, Object> addressInfo = new HashMap<>();
                List<Map<String, Object>> connections = new ArrayList<>();

                Map<String, Object> connection = new HashMap<>();
                connection.put("Quantity", "invalid");
                connections.add(connection);

                stationData.put("ID", 1);
                stationData.put("AddressInfo", addressInfo);
                stationData.put("Connections", connections);

                // When
                Station result = service.convertMapToStation(stationData);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getQuantityOfChargers()).isEqualTo(1);
        }

        @Test
        void whenConvertingStationDataWithNullConnectionQuantity_thenUsesDefaultValues() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                Map<String, Object> addressInfo = new HashMap<>();
                List<Map<String, Object>> connections = new ArrayList<>();

                Map<String, Object> connection = new HashMap<>();
                connection.put("Quantity", null);
                connections.add(connection);

                stationData.put("ID", 1);
                stationData.put("AddressInfo", addressInfo);
                stationData.put("Connections", connections);

                // When
                Station result = service.convertMapToStation(stationData);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getQuantityOfChargers()).isEqualTo(1);
        }

        @Test
        void whenConvertingStationDataWithEmptyConnections_thenUsesDefaultValues() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                Map<String, Object> addressInfo = new HashMap<>();
                List<Map<String, Object>> connections = new ArrayList<>();

                stationData.put("ID", 1);
                stationData.put("AddressInfo", addressInfo);
                stationData.put("Connections", connections);

                // When
                Station result = service.convertMapToStation(stationData);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getQuantityOfChargers()).isEqualTo(1);
        }

        @Test
        void whenConvertingStationDataWithStatusType_thenSetsStatusAndOperational() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                Map<String, Object> statusType = new HashMap<>();
                statusType.put("IsOperational", true);
                statusType.put("Title", "Operational");
                stationData.put("StatusType", statusType);
                stationData.put("AddressInfo", new HashMap<>());
                stationData.put("Connections", new ArrayList<>());

                // When
                Station result = service.convertMapToStation(stationData);

                // Then
                assertThat(result.getStatus()).isEqualTo("Operational");
                assertThat(result.getIsOperational()).isTrue();
        }

        @Test
        void whenConvertingStationDataWithInvalidStatusType_thenStatusIsNull() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                stationData.put("StatusType", "Invalid"); // Not a Map
                stationData.put("AddressInfo", new HashMap<>());
                stationData.put("Connections", new ArrayList<>());

                // When
                Station result = service.convertMapToStation(stationData);

                // Then
                assertThat(result.getStatus()).isNull();
                assertThat(result.getIsOperational()).isNull();
        }

        @Test
        void whenConvertingStationDataWithPower_thenSetsHighestPower() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                List<Map<String, Object>> connections = new ArrayList<>();

                Map<String, Object> connection1 = new HashMap<>();
                connection1.put("PowerKW", 22.0);
                connections.add(connection1);

                Map<String, Object> connection2 = new HashMap<>();
                connection2.put("PowerKW", 50.0);
                connections.add(connection2);

                stationData.put("AddressInfo", new HashMap<>());
                stationData.put("Connections", connections);

                // When
                Station result = service.convertMapToStation(stationData);

                // Then
                assertThat(result.getPower()).isEqualTo(50);
        }

        @Test
        void whenConvertingStationDataWithInvalidPower_thenPowerIsNull() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                List<Map<String, Object>> connections = new ArrayList<>();

                Map<String, Object> connection = new HashMap<>();
                connection.put("PowerKW", "invalid");
                connections.add(connection);

                stationData.put("AddressInfo", new HashMap<>());
                stationData.put("Connections", connections);

                // When
                Station result = service.convertMapToStation(stationData);

                // Then
                assertThat(result.getPower()).isNull();
        }

        @Test
        void whenConvertingStationDataWithExternalId_thenSetsExternalId() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                stationData.put("ID", 123);
                stationData.put("AddressInfo", new HashMap<>());
                stationData.put("Connections", new ArrayList<>());

                // When
                Station result = service.convertMapToStation(stationData);

                // Then
                assertThat(result.getExternalId()).isEqualTo("123");
        }

        @Test
        void whenConvertingStationDataWithStringExternalId_thenSetsExternalId() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                stationData.put("ID", "123");
                stationData.put("AddressInfo", new HashMap<>());
                stationData.put("Connections", new ArrayList<>());

                // When
                Station result = service.convertMapToStation(stationData);

                // Then
                assertThat(result.getExternalId()).isEqualTo("123");
        }

        @Test
        void whenConvertingStationDataWithNullExternalId_thenExternalIdIsNull() {
                // Given
                Map<String, Object> stationData = new HashMap<>();
                stationData.put("ID", null);
                stationData.put("AddressInfo", new HashMap<>());
                stationData.put("Connections", new ArrayList<>());

                // When
                Station result = service.convertMapToStation(stationData);

                // Then
                assertThat(result.getExternalId()).isNull();
        }
}

package tqs.sparkflow.stationservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import tqs.sparkflow.stationservice.dto.RoutePlanningRequestDTO;
import tqs.sparkflow.stationservice.dto.RoutePlanningResponseDTO;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class RoutePlanningServiceTest {

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private RoutePlanningServiceImpl routePlanningService;

    private List<Station> testStations;

    @BeforeEach
    void setUp() {
        // Create test stations
        Station portoStation = new Station();
        portoStation.setId(1L);
        portoStation.setName("Porto Central Station");
        portoStation.setLatitude(41.1579);
        portoStation.setLongitude(-8.6291);
        portoStation.setQuantityOfChargers(2);
        portoStation.setPower(50);
        portoStation.setStatus("Available");
        portoStation.setCity("Porto");
        portoStation.setIsOperational(true);
        portoStation.setPrice(0.30);

        Station aveiroStation = new Station();
        aveiroStation.setId(2L);
        aveiroStation.setName("Aveiro Fast Charge");
        aveiroStation.setLatitude(40.623361);
        aveiroStation.setLongitude(-8.650256);
        aveiroStation.setQuantityOfChargers(3);
        aveiroStation.setPower(150);
        aveiroStation.setStatus("Available");
        aveiroStation.setCity("Aveiro");
        aveiroStation.setIsOperational(true);
        aveiroStation.setPrice(0.35);

        Station coimbraStation = new Station();
        coimbraStation.setId(3L);
        coimbraStation.setName("Coimbra Station");
        coimbraStation.setLatitude(40.2033);
        coimbraStation.setLongitude(-8.4103);
        coimbraStation.setQuantityOfChargers(2);
        coimbraStation.setPower(50);
        coimbraStation.setStatus("Available");
        coimbraStation.setCity("Coimbra");
        coimbraStation.setIsOperational(true);
        coimbraStation.setPrice(0.30);

        Station leiriaStation = new Station();
        leiriaStation.setId(4L);
        leiriaStation.setName("Leiria Fast Charge");
        leiriaStation.setLatitude(39.7477);
        leiriaStation.setLongitude(-8.8077);
        leiriaStation.setQuantityOfChargers(3);
        leiriaStation.setPower(150);
        leiriaStation.setStatus("Available");
        leiriaStation.setCity("Leiria");
        leiriaStation.setIsOperational(true);
        leiriaStation.setPrice(0.35);

        Station lisbonStation = new Station();
        lisbonStation.setId(5L);
        lisbonStation.setName("Lisbon Central Station");
        lisbonStation.setLatitude(38.7223);
        lisbonStation.setLongitude(-9.1393);
        lisbonStation.setQuantityOfChargers(2);
        lisbonStation.setPower(50);
        lisbonStation.setStatus("Available");
        lisbonStation.setCity("Lisbon");
        lisbonStation.setIsOperational(true);
        lisbonStation.setPrice(0.30);

        testStations = Arrays.asList(portoStation, aveiroStation, coimbraStation, leiriaStation,
                lisbonStation);
    }

    @Test
    void testPlanRouteFromPortoToLisbon() {
        // Given
        when(stationRepository.findAll()).thenReturn(testStations);

        // Create route planning request
        RoutePlanningRequestDTO request = new RoutePlanningRequestDTO();
        request.setStartLatitude(41.1579); // Porto
        request.setStartLongitude(-8.6291);
        request.setDestLatitude(38.7223); // Lisbon
        request.setDestLongitude(-9.1393);
        request.setBatteryCapacity(40.0); // 40 kWh battery
        request.setCarAutonomy(5.0); // 5 km/kWh

        // Plan route
        RoutePlanningResponseDTO response = routePlanningService.planRoute(request);

        // Verify response
        assertNotNull(response);
        assertNotNull(response.getStations());
        assertThat(response.getStations()).isNotEmpty();
        assertThat(response.getDistance()).isGreaterThan(0);
        assertThat(response.getBatteryUsage()).isGreaterThan(0);
    }

    @Test
    void whenFindingRoute_thenReturnsOptimalRoute() {
        // Given
        when(stationRepository.findAll()).thenReturn(testStations);

        RoutePlanningRequestDTO request = new RoutePlanningRequestDTO();
        request.setStartLatitude(41.1579); // Porto
        request.setStartLongitude(-8.6291);
        request.setDestLatitude(38.7223); // Lisbon
        request.setDestLongitude(-9.1393);
        request.setBatteryCapacity(40.0); // 40 kWh battery
        request.setCarAutonomy(5.0); // 5 km/kWh

        // When
        RoutePlanningResponseDTO response = routePlanningService.planRoute(request);

        // Then
        assertThat(response.getStations()).isNotEmpty();
        assertThat(response.getStations()).hasSize(1); // Only one station needed
        assertThat(response.getStations().get(0).getName()).isEqualTo("Leiria Fast Charge");
        assertThat(response.getDistance()).isGreaterThan(0);
        assertThat(response.getBatteryUsage()).isGreaterThan(0);
    }

    @Test
    void whenCoordinatesAtBoundaries_thenAcceptsValidValues() {
        // Test valid boundary coordinates with closer points to ensure route is possible
        RoutePlanningRequestDTO request = new RoutePlanningRequestDTO();
        request.setStartLatitude(41.1579); // Porto
        request.setStartLongitude(-8.6291);
        request.setDestLatitude(40.623361); // Aveiro
        request.setDestLongitude(-8.650256);
        request.setBatteryCapacity(40.0);
        request.setCarAutonomy(5.0);

        // When/Then
        assertDoesNotThrow(() -> routePlanningService.planRoute(request));
    }

    @Test
    void whenCoordinatesOutsideBoundaries_thenThrowsBadRequest() {
        // Test invalid boundary coordinates
        RoutePlanningRequestDTO request = new RoutePlanningRequestDTO();
        request.setStartLatitude(91.0); // Invalid latitude
        request.setStartLongitude(0.0);
        request.setDestLatitude(0.0);
        request.setDestLongitude(0.0);
        request.setBatteryCapacity(40.0);
        request.setCarAutonomy(5.0);

        // When/Then
        assertThatThrownBy(() -> routePlanningService.planRoute(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid start latitude");
    }

    @Test
    void whenBatteryCapacityZero_thenThrowsBadRequest() {
        RoutePlanningRequestDTO request = createValidRequest();
        request.setBatteryCapacity(0.0);

        // When/Then
        assertThatThrownBy(() -> routePlanningService.planRoute(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Battery capacity must be greater than 0");
    }

    @Test
    void whenCarAutonomyZero_thenThrowsBadRequest() {
        RoutePlanningRequestDTO request = createValidRequest();
        request.setCarAutonomy(0.0);

        // When/Then
        assertThatThrownBy(() -> routePlanningService.planRoute(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Car autonomy must be greater than 0");
    }

    @Test
    void whenInvalidCoordinates_thenThrowsBadRequest() {
        // Given
        RoutePlanningRequestDTO request = createValidRequest();
        request.setStartLatitude(91.0); // Invalid latitude

        // When/Then
        assertThatThrownBy(() -> routePlanningService.planRoute(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.BAD_REQUEST)
                .hasMessageContaining("Invalid start latitude");
    }

    @Test
    void whenInvalidBatteryCapacity_thenThrowsBadRequest() {
        // Given
        RoutePlanningRequestDTO request = createValidRequest();
        request.setBatteryCapacity(0.0); // Invalid battery capacity

        // When/Then
        assertThatThrownBy(() -> routePlanningService.planRoute(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.BAD_REQUEST)
                .hasMessageContaining("Battery capacity must be greater than 0");
    }

    @Test
    void whenInvalidCarAutonomy_thenThrowsBadRequest() {
        // Given
        RoutePlanningRequestDTO request = createValidRequest();
        request.setCarAutonomy(0.0); // Invalid car autonomy

        // When/Then
        assertThatThrownBy(() -> routePlanningService.planRoute(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.BAD_REQUEST)
                .hasMessageContaining("Car autonomy must be greater than 0");
    }

    @Test
    void whenRateLimitExceeded_thenThrowsTooManyRequests() {
        // Given
        RoutePlanningRequestDTO request = createValidRequest();
        when(stationRepository.findAll()).thenReturn(testStations);

        // Simulate rate limit exceeded
        for (int i = 0; i < 11; i++) {
            routePlanningService.planRoute(request);
        }

        // When/Then
        assertThatThrownBy(() -> routePlanningService.planRoute(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.TOO_MANY_REQUESTS)
                .hasMessageContaining("Rate limit exceeded");
    }

    // Helper method to create a valid request
    private RoutePlanningRequestDTO createValidRequest() {
        RoutePlanningRequestDTO request = new RoutePlanningRequestDTO();
        request.setStartLatitude(41.1579);
        request.setStartLongitude(-8.6291);
        request.setDestLatitude(38.7223);
        request.setDestLongitude(-9.1393);
        request.setBatteryCapacity(40.0);
        request.setCarAutonomy(5.0);
        return request;
    }
}

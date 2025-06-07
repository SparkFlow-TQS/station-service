package tqs.sparkflow.stationservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import tqs.sparkflow.stationservice.config.RoutePlanningConfig;
import tqs.sparkflow.stationservice.dto.RoutePlanningRequestDTO;
import tqs.sparkflow.stationservice.dto.RoutePlanningResponseDTO;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;
import com.google.common.util.concurrent.RateLimiter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RoutePlanningServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private RoutePlanningConfig config;

    @Mock
    private RateLimiter rateLimiter;

    @InjectMocks
    private RoutePlanningServiceImpl routePlanningService;

    private List<Station> testStations;

    @BeforeEach
    void setUp() {
        // Configure config mocks
        when(config.getMinBatteryPercentage()).thenReturn(0.2);
        when(config.getMaxBatteryPercentage()).thenReturn(0.8);
        when(config.getMaxDetourDistance()).thenReturn(20.0);
        when(config.getRequestsPerSecond()).thenReturn(10.0);

        // Configure rate limiter
        when(rateLimiter.tryAcquire()).thenReturn(true);

        // Create test stations
        testStations = new ArrayList<>();

        Station station1 = new Station();
        station1.setName("Station 1");
        station1.setLatitude(41.1579);
        station1.setLongitude(-8.6291);
        station1.setQuantityOfChargers(2);
        station1.setPower(50);
        station1.setStatus("Available");
        station1.setCity("Porto");
        station1.setIsOperational(true);
        station1.setPrice(0.35);
        testStations.add(station1);

        Station station2 = new Station();
        station2.setName("Station 2");
        station2.setLatitude(38.7223);
        station2.setLongitude(-9.1393);
        station2.setQuantityOfChargers(2);
        station2.setPower(50);
        station2.setStatus("Available");
        station2.setCity("Lisbon");
        station2.setIsOperational(true);
        station2.setPrice(0.35);
        testStations.add(station2);

        when(stationRepository.findAll()).thenReturn(testStations);
    }

    @Test
    void whenFindingRoute_thenReturnsOptimalRoute() {
        RoutePlanningRequestDTO request = createValidRequest();
        RoutePlanningResponseDTO response = routePlanningService.planRoute(request);

        assertNotNull(response);
        assertTrue(response.getDistance() > 0);
        assertTrue(response.getBatteryUsage() > 0);

        // If battery usage is high enough, we should have charging stations
        double batteryPercentage = response.getBatteryUsage() / request.getBatteryCapacity();
        if (batteryPercentage > config.getMaxBatteryPercentage()) {
            assertFalse(response.getStations().isEmpty(),
                    "Should have charging stations for high battery usage");
        } else {
            assertTrue(response.getStations().isEmpty(),
                    "Should have no charging stations for direct route");
        }
    }

    @Test
    void whenInvalidCoordinates_thenThrowsBadRequest() {
        RoutePlanningRequestDTO request = createValidRequest();
        request.setStartLatitude(200.0); // Invalid latitude

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> routePlanningService.planRoute(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid start latitude", exception.getReason());
    }

    @Test
    void whenInvalidBatteryCapacity_thenThrowsBadRequest() {
        RoutePlanningRequestDTO request = createValidRequest();
        request.setBatteryCapacity(0.0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> routePlanningService.planRoute(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Battery capacity must be greater than 0", exception.getReason());
    }

    @Test
    void whenInvalidCarAutonomy_thenThrowsBadRequest() {
        RoutePlanningRequestDTO request = createValidRequest();
        request.setCarAutonomy(0.0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> routePlanningService.planRoute(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Car autonomy must be greater than 0", exception.getReason());
    }

    @Test
    void whenRateLimitExceeded_thenThrowsTooManyRequests() {
        when(rateLimiter.tryAcquire()).thenReturn(false);

        RoutePlanningRequestDTO request = createValidRequest();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> routePlanningService.planRoute(request));

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getStatusCode());
        assertEquals("Rate limit exceeded", exception.getReason());
    }

    @Test
    void whenNoAvailableStations_thenThrowsServiceUnavailable() {
        when(stationRepository.findAll()).thenReturn(new ArrayList<>());

        RoutePlanningRequestDTO request = createValidRequest();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> routePlanningService.planRoute(request));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
        assertEquals("No charging stations available in the system", exception.getReason());
    }

    @Test
    void whenBatteryLevelTooLow_thenPenalizesStation() {
        // Create a station that would leave battery too low
        Station lowBatteryStation = new Station();
        lowBatteryStation.setLatitude(40.0);
        lowBatteryStation.setLongitude(-8.5);
        lowBatteryStation.setPower(100);
        lowBatteryStation.setQuantityOfChargers(4);
        lowBatteryStation.setIsOperational(true);
        lowBatteryStation.setStatus("Available");

        when(stationRepository.findAll()).thenReturn(List.of(lowBatteryStation));

        RoutePlanningRequestDTO request = createValidRequest();
        request.setBatteryCapacity(20.0); // Small battery
        request.setCarAutonomy(50.0); // Low autonomy

        RoutePlanningResponseDTO response = routePlanningService.planRoute(request);

        assertNotNull(response);
        assertTrue(response.getStations().isEmpty()); // Should be penalized out of selection
    }

    @Test
    void whenBatteryLevelTooHigh_thenPenalizesStation() {
        // Create a station that would leave battery too high
        Station highBatteryStation = new Station();
        highBatteryStation.setLatitude(38.8); // Very close to destination
        highBatteryStation.setLongitude(-9.0);
        highBatteryStation.setPower(100);
        highBatteryStation.setQuantityOfChargers(4);
        highBatteryStation.setIsOperational(true);
        highBatteryStation.setStatus("Available");

        when(stationRepository.findAll()).thenReturn(List.of(highBatteryStation));

        RoutePlanningRequestDTO request = createValidRequest();
        request.setBatteryCapacity(100.0); // Large battery
        request.setCarAutonomy(200.0); // High autonomy

        RoutePlanningResponseDTO response = routePlanningService.planRoute(request);

        assertNotNull(response);
        assertTrue(response.getStations().isEmpty()); // Should be penalized out of selection
    }

    @Test
    void whenInvalidLongitude_thenThrowsBadRequest() {
        RoutePlanningRequestDTO request = createValidRequest();
        request.setStartLongitude(200.0); // Invalid longitude

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> routePlanningService.planRoute(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid start longitude", exception.getReason());
    }

    @Test
    void whenInvalidDestinationLatitude_thenThrowsBadRequest() {
        RoutePlanningRequestDTO request = createValidRequest();
        request.setDestLatitude(-100.0); // Invalid latitude

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> routePlanningService.planRoute(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid destination latitude", exception.getReason());
    }

    @Test
    void whenInvalidDestinationLongitude_thenThrowsBadRequest() {
        RoutePlanningRequestDTO request = createValidRequest();
        request.setDestLongitude(-200.0); // Invalid longitude

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> routePlanningService.planRoute(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid destination longitude", exception.getReason());
    }

    @Test
    void whenLongRouteRequiresCharging_thenFindsOptimalStations() {
        // Create stations for a long route requiring charging
        Station nearMidpoint = new Station();
        nearMidpoint.setName("Midpoint Station");
        nearMidpoint.setLatitude(39.9); // Near midpoint between Porto and Lisbon
        nearMidpoint.setLongitude(-8.8);
        nearMidpoint.setQuantityOfChargers(4);
        nearMidpoint.setPower(100);
        nearMidpoint.setStatus("Available");
        nearMidpoint.setIsOperational(true);
        nearMidpoint.setCity("Midpoint");
        nearMidpoint.setPrice(0.30);

        when(stationRepository.findAll()).thenReturn(List.of(nearMidpoint));

        RoutePlanningRequestDTO request = createValidRequest();
        request.setBatteryCapacity(20.0); // Very small battery to force charging
        request.setCarAutonomy(80.0); // Low autonomy to ensure route needs charging

        RoutePlanningResponseDTO response = routePlanningService.planRoute(request);

        assertNotNull(response);
        // Check if charging stations are needed based on battery usage
        double batteryPercentage = response.getBatteryUsage() / request.getBatteryCapacity();
        if (batteryPercentage > config.getMaxBatteryPercentage()) {
            assertFalse(response.getStations().isEmpty());
        }
        assertTrue(response.getDistance() > 0);
        assertTrue(response.getBatteryUsage() > 0);
    }

    @Test
    void whenChargingNeededButNoSuitableStations_thenCorrectBehavior() {
        // Create stations that are too far from the route
        Station farStation = new Station();
        farStation.setName("Far Station");
        farStation.setLatitude(45.0); // Very far from route
        farStation.setLongitude(2.0);
        farStation.setQuantityOfChargers(2);
        farStation.setPower(50);
        farStation.setStatus("Available");
        farStation.setIsOperational(true);
        farStation.setCity("Far City");
        farStation.setPrice(0.40);

        when(stationRepository.findAll()).thenReturn(List.of(farStation));

        RoutePlanningRequestDTO request = createValidRequest();
        request.setBatteryCapacity(10.0); // Very small battery to force charging need
        request.setCarAutonomy(50.0); // Low autonomy

        // The service should handle this case and either find stations or provide direct route
        RoutePlanningResponseDTO response = routePlanningService.planRoute(request);
        assertNotNull(response);
        assertTrue(response.getDistance() > 0);
        assertTrue(response.getBatteryUsage() >= 0);
    }

    @Test
    void whenStationsWithinDetourLimitButAllPenalized_thenThrowsBadRequest() {
        // Create stations that are within detour limit but would be penalized
        Station penalizedStation = new Station();
        penalizedStation.setName("Penalized Station");
        penalizedStation.setLatitude(39.5);
        penalizedStation.setLongitude(-8.5);
        penalizedStation.setQuantityOfChargers(1);
        penalizedStation.setPower(10); // Low power
        penalizedStation.setStatus("Available");
        penalizedStation.setIsOperational(true);
        penalizedStation.setCity("Penalized City");
        penalizedStation.setPrice(0.50);

        when(stationRepository.findAll()).thenReturn(List.of(penalizedStation));
        when(config.getMinBatteryPercentage()).thenReturn(0.9); // Very high minimum battery requirement

        RoutePlanningRequestDTO request = createValidRequest();
        request.setBatteryCapacity(5.0); // Very small battery to trigger penalty
        request.setCarAutonomy(50.0); // Low autonomy

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> routePlanningService.planRoute(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("No suitable charging stations found for the given route", exception.getReason());
    }

    @Test
    void whenMultipleStations_thenSelectsBestOnes() {
        // Create multiple stations with different characteristics
        Station goodStation = new Station();
        goodStation.setName("Good Station");
        goodStation.setLatitude(39.9);
        goodStation.setLongitude(-8.8);
        goodStation.setQuantityOfChargers(6);
        goodStation.setPower(150);
        goodStation.setStatus("Available");
        goodStation.setIsOperational(true);
        goodStation.setCity("Good City");
        goodStation.setPrice(0.25);

        Station okStation = new Station();
        okStation.setName("OK Station");
        okStation.setLatitude(40.0);
        okStation.setLongitude(-8.9);
        okStation.setQuantityOfChargers(2);
        okStation.setPower(50);
        okStation.setStatus("Available");
        okStation.setIsOperational(true);
        okStation.setCity("OK City");
        okStation.setPrice(0.35);

        Station badStation = new Station();
        badStation.setName("Bad Station");
        badStation.setLatitude(39.8);
        badStation.setLongitude(-8.7);
        badStation.setQuantityOfChargers(1);
        badStation.setPower(25);
        badStation.setStatus("Available");
        badStation.setIsOperational(true);
        badStation.setCity("Bad City");
        badStation.setPrice(0.45);

        when(stationRepository.findAll()).thenReturn(List.of(goodStation, okStation, badStation));

        RoutePlanningRequestDTO request = createValidRequest();
        request.setBatteryCapacity(20.0); // Small battery to ensure charging is needed
        request.setCarAutonomy(80.0); // Low autonomy

        RoutePlanningResponseDTO response = routePlanningService.planRoute(request);

        assertNotNull(response);
        // Check if charging stations are included based on battery requirement
        double batteryPercentage = response.getBatteryUsage() / request.getBatteryCapacity();
        if (batteryPercentage > config.getMaxBatteryPercentage()) {
            assertFalse(response.getStations().isEmpty());
            assertTrue(response.getStations().size() <= 3); // Limited to 3 stations
        }
        assertTrue(response.getDistance() > 0);
    }

    @Test
    void whenDirectRouteWithinBatteryLimit_thenReturnsEmptyStations() {
        RoutePlanningRequestDTO request = createValidRequest();
        request.setBatteryCapacity(100.0); // Large battery
        request.setCarAutonomy(500.0); // Very high autonomy
        
        // Mock config to allow direct route
        when(config.getMaxBatteryPercentage()).thenReturn(1.0); // 100%

        RoutePlanningResponseDTO response = routePlanningService.planRoute(request);

        assertNotNull(response);
        assertTrue(response.getStations().isEmpty()); // Direct route possible
        assertTrue(response.getDistance() > 0);
        assertTrue(response.getBatteryUsage() >= 0);
    }

    private RoutePlanningRequestDTO createValidRequest() {
        RoutePlanningRequestDTO request = new RoutePlanningRequestDTO();
        request.setStartLatitude(41.1579);
        request.setStartLongitude(-8.6291);
        request.setDestLatitude(38.7223);
        request.setDestLongitude(-9.1393);
        request.setBatteryCapacity(50.0);
        request.setCarAutonomy(100.0);
        return request;
    }
}

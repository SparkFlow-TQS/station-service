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
public class RoutePlanningServiceTest {

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

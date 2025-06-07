package tqs.sparkflow.stationservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;
import tqs.sparkflow.stationservice.config.RoutePlanningConfig;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;
import tqs.sparkflow.stationservice.dto.RoutePlanningRequestDTO;
import tqs.sparkflow.stationservice.dto.RoutePlanningResponseDTO;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({RoutePlanningServiceImpl.class, RoutePlanningConfig.class})
@TestPropertySource(properties = {"route.planning.min-battery-percentage=0.2",
    "route.planning.max-battery-percentage=0.8", "route.planning.max-detour-distance=20.0",
    "route.planning.requests-per-second=10.0"})
public class RoutePlanningServiceIT {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private StationRepository stationRepository;

  @Autowired
  private RoutePlanningService routePlanningService;

  private List<Station> testStations;

  @BeforeEach
  void setUp() {
    testStations = new ArrayList<>();

    // Create test stations
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

    // Save stations to database
    for (Station station : testStations) {
      entityManager.persist(station);
    }
    entityManager.flush();
  }

  @Test
  void whenAllStationsUnavailable_thenThrowsServiceUnavailable() {
    // Set all stations as unavailable
    for (Station station : testStations) {
      station.setStatus("Unavailable");
      station.setIsOperational(false);
      entityManager.persist(station);
    }
    entityManager.flush();

    RoutePlanningRequestDTO request = createValidRequest();

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> routePlanningService.planRoute(request));

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
    assertEquals("No charging stations available in the system", exception.getReason());
  }

  @Test
  void whenNoStationsFound_thenThrowsServiceUnavailable() {
    // Clear all stations
    stationRepository.deleteAll();
    entityManager.flush();

    RoutePlanningRequestDTO request = createValidRequest();

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> routePlanningService.planRoute(request));

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
    assertEquals("No charging stations available in the system", exception.getReason());
  }

  @Test
  void whenInvalidRoute_thenThrowsBadRequest() {
    RoutePlanningRequestDTO request = createValidRequest();
    request.setStartLatitude(200.0); // Invalid latitude

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> routePlanningService.planRoute(request));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Invalid start latitude", exception.getReason());
  }

  @Test
  void whenLargeDataset_thenCompletesWithinTimeLimit() {
    // Create a large number of stations
    for (int i = 0; i < 100; i++) {
      Station station = new Station();
      station.setName("Station " + i);
      station.setLatitude(41.1579 + (i * 0.01));
      station.setLongitude(-8.6291 + (i * 0.01));
      station.setQuantityOfChargers(2);
      station.setPower(50);
      station.setStatus("Available");
      station.setCity("Porto");
      station.setIsOperational(true);
      station.setPrice(0.35);
      entityManager.persist(station);
    }
    entityManager.flush();

    RoutePlanningRequestDTO request = createValidRequest();

    long startTime = System.currentTimeMillis();
    RoutePlanningResponseDTO response = routePlanningService.planRoute(request);
    long endTime = System.currentTimeMillis();

    assertNotNull(response);
    assertTrue(endTime - startTime < 5000); // Should complete within 5 seconds
  }

  private RoutePlanningRequestDTO createValidRequest() {
    RoutePlanningRequestDTO request = new RoutePlanningRequestDTO();
    request.setStartLatitude(41.1579);
    request.setStartLongitude(-8.6291);
    request.setDestLatitude(38.7223);
    request.setDestLongitude(-9.1393);
    request.setBatteryCapacity(75.0);
    request.setCarAutonomy(300.0);
    return request;
  }

  @TestConfiguration
  public static class TestConfig {
    @Bean
    @Primary
    public RateLimiter routePlanningRateLimiter() {
      return RateLimiter.create(Double.MAX_VALUE); // Effectively disables rate limiting
    }
  }
}

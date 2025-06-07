package tqs.sparkflow.stationservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import tqs.sparkflow.stationservice.dto.RoutePlanningRequestDTO;
import tqs.sparkflow.stationservice.dto.RoutePlanningResponseDTO;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;

@DataJpaTest
@Import(RoutePlanningServiceImpl.class)
class RoutePlanningServiceIntegrationTest {

  @MockBean
  private StationRepository stationRepository;

  @Autowired
  private RoutePlanningService routePlanningService;

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

    testStations =
        List.of(portoStation, aveiroStation, coimbraStation, leiriaStation, lisbonStation);
  }

  @Test
  void whenNoStationsAvailable_thenThrowsServiceUnavailable() {
    // Given
    when(stationRepository.findAll()).thenReturn(new ArrayList<>());
    RoutePlanningRequestDTO request = createValidRequest();

    // When/Then
    assertThatThrownBy(() -> routePlanningService.planRoute(request))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("No charging stations available");
  }

  @Test
  void whenAllStationsUnavailable_thenThrowsServiceUnavailable() {
    // Given
    List<Station> unavailableStations = new ArrayList<>();
    for (Station station : testStations) {
      Station unavailableStation = new Station();
      unavailableStation.setId(station.getId());
      unavailableStation.setName(station.getName());
      unavailableStation.setLatitude(station.getLatitude());
      unavailableStation.setLongitude(station.getLongitude());
      unavailableStation.setQuantityOfChargers(station.getQuantityOfChargers());
      unavailableStation.setPower(station.getPower());
      unavailableStation.setStatus("Unavailable"); // Set status to Unavailable
      unavailableStation.setCity(station.getCity());
      unavailableStation.setIsOperational(false);
      unavailableStation.setPrice(station.getPrice());
      unavailableStations.add(unavailableStation);
    }

    // Ensure the mock is properly set up
    when(stationRepository.findAll()).thenReturn(unavailableStations);

    // Create a request with closer points to ensure it's not a distance issue
    RoutePlanningRequestDTO request = new RoutePlanningRequestDTO();
    request.setStartLatitude(41.1579); // Porto
    request.setStartLongitude(-8.6291);
    request.setDestLatitude(40.623361); // Aveiro
    request.setDestLongitude(-8.650256);
    request.setBatteryCapacity(40.0);
    request.setCarAutonomy(5.0);

    // When/Then
    assertThatThrownBy(() -> routePlanningService.planRoute(request))
        .isInstanceOf(ResponseStatusException.class)
        .hasFieldOrPropertyWithValue("statusCode", HttpStatus.SERVICE_UNAVAILABLE)
        .hasMessageContaining("No charging stations available in the system");

    // Verify the mock was called
    verify(stationRepository).findAll();
  }

  @Test
  void whenNoStationsFound_thenThrowsServiceUnavailable() {
    // Given
    when(stationRepository.findAll()).thenReturn(new ArrayList<>());
    RoutePlanningRequestDTO request = createValidRequest();

    // When/Then
    assertThatThrownBy(() -> routePlanningService.planRoute(request))
        .isInstanceOf(ResponseStatusException.class)
        .hasFieldOrPropertyWithValue("statusCode", HttpStatus.SERVICE_UNAVAILABLE)
        .hasMessageContaining("No charging stations available in the system");

    // Verify the mock was called
    verify(stationRepository).findAll();
  }

  @Test
  void whenInvalidRoute_thenThrowsBadRequest() {
    // Given
    when(stationRepository.findAll()).thenReturn(testStations);
    RoutePlanningRequestDTO request = new RoutePlanningRequestDTO();
    request.setStartLatitude(41.1579); // Porto
    request.setStartLongitude(-8.6291);
    request.setDestLatitude(38.7223); // Lisbon
    request.setDestLongitude(-9.1393);
    request.setBatteryCapacity(10.0); // Small battery
    request.setCarAutonomy(1.0); // Low autonomy

    // When/Then
    assertThatThrownBy(() -> routePlanningService.planRoute(request))
        .isInstanceOf(ResponseStatusException.class)
        .hasFieldOrPropertyWithValue("statusCode", HttpStatus.BAD_REQUEST)
        .hasMessageContaining("No suitable charging stations found for the given route");

    // Verify the mock was called
    verify(stationRepository).findAll();
  }

  @Test
  void whenLargeDataset_thenCompletesWithinTimeLimit() {
    // Create a large dataset of stations with closer proximity
    List<Station> largeDataset = new ArrayList<>();
    double baseLat = 41.1579; // Porto latitude
    double baseLon = -8.6291; // Porto longitude

    // Create a 10x10 grid of stations around Porto
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        Station station = new Station();
        station.setId((long) (i * 10 + j));
        station.setName("Station " + (i * 10 + j));
        station.setLatitude(baseLat + (i * 0.01));
        station.setLongitude(baseLon + (j * 0.01));
        station.setQuantityOfChargers(2);
        station.setPower(50);
        station.setStatus("Available");
        station.setCity("City " + (i * 10 + j));
        station.setIsOperational(true);
        station.setPrice(0.30);
        largeDataset.add(station);
      }
    }
    when(stationRepository.findAll()).thenReturn(largeDataset);

    // Create a request with closer destination
    RoutePlanningRequestDTO request = new RoutePlanningRequestDTO();
    request.setStartLatitude(baseLat);
    request.setStartLongitude(baseLon);
    request.setDestLatitude(baseLat + 0.05); // Very close destination
    request.setDestLongitude(baseLon + 0.05);
    request.setBatteryCapacity(40.0);
    request.setCarAutonomy(5.0);

    // Measure execution time
    long startTime = System.nanoTime();
    RoutePlanningResponseDTO response = routePlanningService.planRoute(request);
    long endTime = System.nanoTime();

    // Verify response and performance
    assertNotNull(response);
    long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
    assertThat(durationMs).isLessThan(1000); // Should complete within 1 second
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

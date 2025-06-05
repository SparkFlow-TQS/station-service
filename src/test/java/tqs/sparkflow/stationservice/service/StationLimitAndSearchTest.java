package tqs.sparkflow.stationservice.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;

/**
 * Test class for Station Service 500-limit functionality and enhanced search features.
 * These tests verify the implementation of the MAX_SEARCH_RESULTS = 500 limit
 * across all search methods and the total station count feature.
 */
@ExtendWith(MockitoExtension.class)
class StationLimitAndSearchTest {

  @Mock private StationRepository stationRepository;

  @InjectMocks private StationService stationService;

  private List<Station> createLargeStationList(int count, String namePrefix) {
    List<Station> stationList = new ArrayList<>();
    for (int i = 1; i <= count; i++) {
      Station station = createTestStation((long) i, namePrefix + " " + i);
      stationList.add(station);
    }
    return stationList;
  }

  private Station createTestStation(Long id, String name) {
    Station station = new Station();
    station.setId(id);
    station.setName(name);
    station.setAddress("Test Address");
    station.setCity("Lisbon");
    station.setCountry("Portugal");
    station.setLatitude(38.7223);
    station.setLongitude(-9.1393);
    return station;
  }

  @Test
  @XrayTest(key = "STATION-LIMIT-1")
  @Requirement("STATION-LIMIT-1")
  void whenGettingAllStations_thenLimitsTo500Results() {
    // Given - Create a list of 600 stations to test the 500 limit
    List<Station> largeStationList = createLargeStationList(600, "Station");
    when(stationRepository.findAll()).thenReturn(largeStationList);

    // When
    List<Station> result = stationService.getAllStations();

    // Then
    assertThat(result).hasSize(500);
    assertThat(result.get(0).getName()).isEqualTo("Station 1");
    assertThat(result.get(499).getName()).isEqualTo("Station 500");
    verify(stationRepository).findAll();
  }

  @Test
  @XrayTest(key = "STATION-LIMIT-2")
  @Requirement("STATION-LIMIT-2")
  void whenGettingAllStationsWithLessThan500_thenReturnsAllStations() {
    // Given - Create a list of 50 stations (less than limit)
    List<Station> smallStationList = createLargeStationList(50, "Station");
    when(stationRepository.findAll()).thenReturn(smallStationList);

    // When
    List<Station> result = stationService.getAllStations();

    // Then
    assertThat(result).hasSize(50);
    assertThat(result).isEqualTo(smallStationList);
    verify(stationRepository).findAll();
  }

  @Test
  @XrayTest(key = "STATION-SEARCH-1")
  @Requirement("STATION-SEARCH-1")
  void whenSearchingStations_thenLimitsTo500Results() {
    // Given - Create a list of 600 matching stations
    List<Station> largeStationList = createLargeStationList(600, "TestStation");
    largeStationList.forEach(station -> station.setCity("TestCity"));
    when(stationRepository.findAll()).thenReturn(largeStationList);

    // When
    List<Station> result = stationService.searchStations("TestStation", "TestCity", null, null);

    // Then
    assertThat(result).hasSize(500);
    assertThat(result.get(0).getName()).isEqualTo("TestStation 1");
    assertThat(result.get(499).getName()).isEqualTo("TestStation 500");
    verify(stationRepository).findAll();
  }

  @Test
  @XrayTest(key = "STATION-SEARCH-2")
  @Requirement("STATION-SEARCH-2")
  void whenSearchingStationsWithPartialNameMatch_thenReturnsMatchingStations() {
    // Given
    List<Station> allStations = Arrays.asList(
        createTestStationWithName(1L, "Mercadona Charging Station"),
        createTestStationWithName(2L, "Continente Power Hub"),
        createTestStationWithName(3L, "Mercadona Express Charger"),
        createTestStationWithName(4L, "Lidl Charging Point")
    );
    when(stationRepository.findAll()).thenReturn(allStations);

    // When
    List<Station> result = stationService.searchStations("Mercadona", null, null, null);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result.stream().map(Station::getName))
        .containsExactlyInAnyOrder("Mercadona Charging Station", "Mercadona Express Charger");
    verify(stationRepository).findAll();
  }

  @Test
  @XrayTest(key = "STATION-SEARCH-3")
  @Requirement("STATION-SEARCH-3")
  void whenSearchingStationsWithCaseInsensitiveMatch_thenReturnsMatchingStations() {
    // Given
    List<Station> allStations = Arrays.asList(
        createTestStationWithNameAndCity(1L, "AVEIRO Station", "aveiro"),
        createTestStationWithNameAndCity(2L, "Porto Station", "PORTO"),
        createTestStationWithNameAndCity(3L, "Lisboa Station", "Lisboa")
    );
    when(stationRepository.findAll()).thenReturn(allStations);

    // When
    List<Station> result = stationService.searchStations("aveiro", "AVEIRO", null, null);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("AVEIRO Station");
    verify(stationRepository).findAll();
  }

  @Test
  @XrayTest(key = "STATION-SEARCH-4")
  @Requirement("STATION-SEARCH-4")
  void whenSearchingStationsWithMultipleCriteria_thenReturnsMatchingStations() {
    // Given
    List<Station> allStations = Arrays.asList(
        createTestStationWithDetails(1L, "Station A", "Aveiro", "Portugal"),
        createTestStationWithDetails(2L, "Station B", "Porto", "Portugal"),
        createTestStationWithDetails(3L, "Station C", "Aveiro", "Spain"),
        createTestStationWithDetails(4L, "Station D", "Madrid", "Spain")
    );
    when(stationRepository.findAll()).thenReturn(allStations);

    // When
    List<Station> result = stationService.searchStations(null, "Aveiro", "Portugal", null);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("Station A");
    verify(stationRepository).findAll();
  }

  @Test
  @XrayTest(key = "STATION-NEARBY-1")
  @Requirement("STATION-NEARBY-1")
  void whenGettingNearbyStations_thenLimitsTo500Results() {
    // Given - Create 600 nearby stations (all within radius)
    double centerLat = 38.7223;
    double centerLon = -9.1393;
    int radius = 100; // Large radius to include all stations
    
    List<Station> largeNearbyList = new ArrayList<>();
    for (int i = 1; i <= 600; i++) {
      Station station = createTestStation((long) i, "Nearby Station " + i);
      // Set coordinates very close to center (within 1km)
      station.setLatitude(centerLat + (i * 0.001)); // Small increments
      station.setLongitude(centerLon + (i * 0.001));
      largeNearbyList.add(station);
    }
    when(stationRepository.findAll()).thenReturn(largeNearbyList);

    // When
    List<Station> result = stationService.getNearbyStations(centerLat, centerLon, radius);

    // Then
    assertThat(result).hasSize(500);
    verify(stationRepository).findAll();
  }

  // Helper methods for creating test stations with specific properties
  private Station createTestStationWithName(Long id, String name) {
    return createTestStation(id, name);
  }

  private Station createTestStationWithNameAndCity(Long id, String name, String city) {
    Station station = createTestStation(id, name);
    station.setCity(city);
    return station;
  }

  private Station createTestStationWithDetails(Long id, String name, String city, String country) {
    Station station = createTestStation(id, name);
    station.setCity(city);
    station.setCountry(country);
    return station;
  }
} 
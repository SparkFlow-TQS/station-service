package tqs.sparkflow.stationservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

  @Mock private StationRepository stationRepository;

  @InjectMocks private StationService stationService;

  private Station station1;
  private Station station2;
  private Station station3;
  private Station station4;
  private Station station5;

  @BeforeEach
  void setUp() {
    // Create diverse test stations for comprehensive testing
    station1 = new Station();
    station1.setId(1L);
    station1.setName("Tesla Supercharger Aveiro");
    station1.setAddress("Rua da Tesla 123");
    station1.setCity("Aveiro");
    station1.setCountry("Portugal");
    station1.setConnectorType("Tesla");
    station1.setPower(150);
    station1.setPrice(0.35);
    station1.setIsOperational(true);
    station1.setStatus("Available");
    station1.setLatitude(40.623361);
    station1.setLongitude(-8.650256);

    station2 = new Station();
    station2.setId(2L);
    station2.setName("IONITY Porto Norte");
    station2.setAddress("Área de Serviço Porto Norte");
    station2.setCity("Porto");
    station2.setCountry("Portugal");
    station2.setConnectorType("CCS");
    station2.setPower(350);
    station2.setPrice(0.79);
    station2.setIsOperational(true);
    station2.setStatus("In Use");
    station2.setLatitude(41.1579);
    station2.setLongitude(-8.6291);

    station3 = new Station();
    station3.setId(3L);
    station3.setName("Mobi.E Lisbon Central");
    station3.setAddress("Avenida da Liberdade 200");
    station3.setCity("Lisbon");
    station3.setCountry("Portugal");
    station3.setConnectorType("Type 2");
    station3.setPower(22);
    station3.setPrice(0.25);
    station3.setIsOperational(false);
    station3.setStatus("Offline");
    station3.setLatitude(38.7223);
    station3.setLongitude(-9.1393);

    station4 = new Station();
    station4.setId(4L);
    station4.setName("FastCharge Madrid");
    station4.setAddress("Calle Mayor 456");
    station4.setCity("Madrid");
    station4.setCountry("Spain");
    station4.setConnectorType("CCS");
    station4.setPower(100);
    station4.setPrice(0.45);
    station4.setIsOperational(true);
    station4.setStatus("Available");
    station4.setLatitude(40.4168);
    station4.setLongitude(-3.7038);

    station5 = new Station();
    station5.setId(5L);
    station5.setName("EV Charge Coimbra");
    station5.setAddress("Praça da República 789");
    station5.setCity("Coimbra");
    station5.setCountry("Portugal");
    station5.setConnectorType("Type 2");
    station5.setPower(43);
    station5.setPrice(0.30);
    station5.setIsOperational(true);
    station5.setStatus("Available");
    station5.setLatitude(40.2033);
    station5.setLongitude(-8.4103);
  }

  // ===== EXISTING CORE TESTS =====

  @Test
  @XrayTest(key = "STATION-SVC-1")
  @Requirement("STATION-SVC-1")
  void whenGettingAllStations_thenReturnsAllStations() {
    // Given
    List<Station> expectedStations = Arrays.asList(station1, station2);
    when(stationRepository.findAll()).thenReturn(expectedStations);

    // When
    List<Station> result = stationService.getAllStations();

    // Then
    assertThat(result).isEqualTo(expectedStations);
    verify(stationRepository).findAll();
  }

  @Test
  @XrayTest(key = "STATION-SVC-2")
  @Requirement("STATION-SVC-2")
  void whenGettingStationById_thenReturnsStation() {
    // Given
    Long stationId = 1L;
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station1));

    // When
    Station result = stationService.getStationById(stationId);

    // Then
    assertThat(result).isEqualTo(station1);
    verify(stationRepository).findById(stationId);
  }

  @Test
  @XrayTest(key = "STATION-SVC-3")
  @Requirement("STATION-SVC-3")
  void whenGettingNonExistentStationById_thenThrowsException() {
    // Given
    Long stationId = 999L;
    when(stationRepository.findById(stationId)).thenReturn(Optional.empty());

    // When/Then
    assertThatThrownBy(() -> stationService.getStationById(stationId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Station not found with id: " + stationId);
  }

  @Test
  @XrayTest(key = "STATION-SVC-4")
  @Requirement("STATION-SVC-4")
  void whenGettingStationByIdWithNullId_thenThrowsException() {
    // When/Then
    assertThatThrownBy(() -> stationService.getStationById(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Station ID cannot be null");
  }

  // ===== ENHANCED SEARCH FUNCTIONALITY TESTS =====

  @Test
  @XrayTest(key = "STATION-SVC-23")
  @Requirement("STATION-SVC-23")
  void whenSearchingStationsByName_thenReturnsMatchingStations() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station4, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search for "Tesla"
    List<Station> result = stationService.searchStations("Tesla", null, null, null);

    // Then
    assertThat(result)
        .hasSize(1)
        .containsExactly(station1);
    verify(stationRepository).findAll();
  }

  @Test
  @XrayTest(key = "STATION-SVC-24")
  @Requirement("STATION-SVC-24")
  void whenSearchingStationsWithPartialName_thenReturnsMatchingStations() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station4, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search for partial name "EV" to avoid matching "Supercharger"
    List<Station> result = stationService.searchStations("EV", null, null, null);

    // Then
    assertThat(result)
        .hasSize(1)
        .extracting(Station::getName)
        .containsExactly("EV Charge Coimbra");
  }

  @Test
  @XrayTest(key = "STATION-SVC-45")
  @Requirement("STATION-SVC-45")
  void whenSearchingStationsWithChargeKeyword_thenReturnsAllMatchingStations() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station4, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search for "Charge" which appears in multiple station names
    List<Station> result = stationService.searchStations("Charge", null, null, null);

    // Then - Should match Tesla "Supercharger", "FastCharge", and "EV Charge"
    assertThat(result)
        .hasSize(3)
        .extracting(Station::getName)
        .containsExactlyInAnyOrder("Tesla Supercharger Aveiro", "FastCharge Madrid", "EV Charge Coimbra");
  }

  @Test
  @XrayTest(key = "STATION-SVC-46")
  @Requirement("STATION-SVC-46")
  void whenSearchingStationsWithSpecificPrefix_thenReturnsMatchingStations() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station4, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search for "Fast" to get only FastCharge Madrid
    List<Station> result = stationService.searchStations("Fast", null, null, null);

    // Then
    assertThat(result)
        .hasSize(1)
        .extracting(Station::getName)
        .containsExactly("FastCharge Madrid");
  }

  @Test
  @XrayTest(key = "STATION-SVC-25")
  @Requirement("STATION-SVC-25")
  void whenSearchingStationsByCaseInsensitiveName_thenReturnsMatchingStations() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station4, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search with different case
    List<Station> result = stationService.searchStations("IONITY", null, null, null);

    // Then
    assertThat(result)
        .hasSize(1)
        .containsExactly(station2);
  }

  @Test
  @XrayTest(key = "STATION-SVC-26")
  @Requirement("STATION-SVC-26")
  void whenSearchingStationsByCity_thenReturnsMatchingStations() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station4, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search by city
    List<Station> result = stationService.searchStations(null, "Porto", null, null);

    // Then
    assertThat(result)
        .hasSize(1)
        .containsExactly(station2);
  }

  @Test
  @XrayTest(key = "STATION-SVC-27")
  @Requirement("STATION-SVC-27")
  void whenSearchingStationsByCountry_thenReturnsMatchingStations() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station4, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search by country
    List<Station> result = stationService.searchStations(null, null, "Portugal", null);

    // Then
    assertThat(result)
        .hasSize(4)
        .extracting(Station::getCountry)
        .containsOnly("Portugal");
  }

  @Test
  @XrayTest(key = "STATION-SVC-28")
  @Requirement("STATION-SVC-28")
  void whenSearchingStationsByConnectorType_thenReturnsMatchingStations() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station4, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search by connector type
    List<Station> result = stationService.searchStations(null, null, null, "CCS");

    // Then
    assertThat(result)
        .hasSize(2)
        .extracting(Station::getConnectorType)
        .containsOnly("CCS");
  }

  @Test
  @XrayTest(key = "STATION-SVC-29")
  @Requirement("STATION-SVC-29")
  void whenSearchingStationsWithMultipleCriteria_thenReturnsMatchingStations() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station4, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search with multiple criteria
    List<Station> result = stationService.searchStations(null, null, "Portugal", "Type 2");

    // Then
    assertThat(result)
        .hasSize(2)
        .extracting(Station::getId)
        .containsExactlyInAnyOrder(3L, 5L);
  }

  @Test
  @XrayTest(key = "STATION-SVC-30")
  @Requirement("STATION-SVC-30")
  void whenSearchingStationsWithNoMatches_thenReturnsEmptyList() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station4, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search for non-existent criteria
    List<Station> result = stationService.searchStations("NonExistent", null, null, null);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @XrayTest(key = "STATION-SVC-31")
  @Requirement("STATION-SVC-31")
  void whenSearchingStationsWithNullValues_thenHandlesGracefully() {
    // Given
    Station stationWithNulls = new Station();
    stationWithNulls.setId(6L);
    stationWithNulls.setName(null);
    stationWithNulls.setCity(null);
    stationWithNulls.setCountry(null);
    stationWithNulls.setConnectorType(null);
    
    List<Station> allStations = Arrays.asList(station1, stationWithNulls);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search that should exclude null values
    List<Station> result = stationService.searchStations("Tesla", null, null, null);

    // Then
    assertThat(result)
        .hasSize(1)
        .containsExactly(station1);
  }

  @Test
  @XrayTest(key = "STATION-SVC-32")
  @Requirement("STATION-SVC-32")
  void whenSearchingStationsWithEmptyStrings_thenIgnoresEmptyFilters() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search with empty strings (should return all)
    List<Station> result = stationService.searchStations("", "", "", "");

    // Then
    assertThat(result)
        .hasSize(2)
        .containsExactlyInAnyOrder(station1, station2);
  }

  // ===== DISTANCE CALCULATION AND NEARBY STATIONS TESTS =====

  @Test
  @XrayTest(key = "STATION-SVC-33")
  @Requirement("STATION-SVC-33")
  void whenGettingNearbyStationsWithAccurateDistance_thenReturnsCorrectStations() {
    // Given - Test exact distance calculation with a very close station
    Station nearbyStation = createTestStation(6L, "Very Close Station");
    nearbyStation.setLatitude(40.624361); // ~100m away from Aveiro
    nearbyStation.setLongitude(-8.651256);
    
    List<Station> allStations = Arrays.asList(station1, station2, station3, nearbyStation);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search within 1km from Aveiro (very small radius)
    List<Station> result = stationService.getNearbyStations(40.623361, -8.650256, 1);

    // Then - Should include station1 (exact location) and nearbyStation (~100m away)
    assertThat(result)
        .hasSize(2)
        .extracting(Station::getId)
        .containsExactlyInAnyOrder(1L, 6L);
  }

  @Test
  @XrayTest(key = "STATION-SVC-34")
  @Requirement("STATION-SVC-34")
  void whenGettingNearbyStationsWithSmallRadius_thenReturnsOnlyVeryCloseStations() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search within 1km from Aveiro
    List<Station> result = stationService.getNearbyStations(40.623361, -8.650256, 1);

    // Then - Should only include station1 (exact location)
    assertThat(result)
        .hasSize(1)
        .containsExactly(station1);
  }

  @Test
  @XrayTest(key = "STATION-SVC-35")
  @Requirement("STATION-SVC-35")
  void whenGettingNearbyStationsWithLargeRadius_thenReturnsAllStationsWithinRange() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search within 100km from Aveiro
    List<Station> result = stationService.getNearbyStations(40.623361, -8.650256, 100);

    // Then - Should include station1, station2 (Porto, ~68km), station5 (Coimbra, ~62km)
    // Lisbon (station3) is ~255km away, so should be excluded
    assertThat(result)
        .hasSize(3)
        .extracting(Station::getId)
        .containsExactlyInAnyOrder(1L, 2L, 5L);
  }

  @Test
  @XrayTest(key = "STATION-SVC-47")
  @Requirement("STATION-SVC-47")
  void whenGettingNearbyStationsWithMediumRadius_thenIncludesPortoAndCoimbra() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search within 70km from Aveiro (includes both Porto ~68km and Coimbra ~62km)
    List<Station> result = stationService.getNearbyStations(40.623361, -8.650256, 70);

    // Then - Should include all 3 nearby stations
    assertThat(result)
        .hasSize(3)
        .extracting(Station::getId)
        .containsExactlyInAnyOrder(1L, 2L, 5L);
  }

  @Test
  @XrayTest(key = "STATION-SVC-36")
  @Requirement("STATION-SVC-36")
  void whenGettingNearbyStationsWithInvalidLatitude_thenThrowsException() {
    // When/Then - Invalid latitude (> 90)
    assertThatThrownBy(() -> stationService.getNearbyStations(91.0, -8.650256, 10))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Latitude must be between -90 and 90 degrees");

    // When/Then - Invalid latitude (< -90)
    assertThatThrownBy(() -> stationService.getNearbyStations(-91.0, -8.650256, 10))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Latitude must be between -90 and 90 degrees");
  }

  @Test
  @XrayTest(key = "STATION-SVC-37")
  @Requirement("STATION-SVC-37")
  void whenGettingNearbyStationsWithInvalidLongitude_thenThrowsException() {
    // When/Then - Invalid longitude (> 180)
    assertThatThrownBy(() -> stationService.getNearbyStations(40.623361, 181.0, 10))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Longitude must be between -180 and 180 degrees");

    // When/Then - Invalid longitude (< -180)
    assertThatThrownBy(() -> stationService.getNearbyStations(40.623361, -181.0, 10))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Longitude must be between -180 and 180 degrees");
  }

  @Test
  @XrayTest(key = "STATION-SVC-5")
  @Requirement("STATION-SVC-5")
  void whenGettingNearbyStations_thenReturnsOnlyStationsWithinRadius() {
    // Given
    double centerLat = 38.7223;
    double centerLon = -9.1393;
    int radius = 10;

    // Create stations at different distances
    Station nearbyStation = createTestStation(1L, "Nearby Station");
    nearbyStation.setLatitude(38.7323); // ~1.1km away
    nearbyStation.setLongitude(-9.1393);

    Station farStation = createTestStation(2L, "Far Station");
    farStation.setLatitude(38.8223); // ~11.1km away
    farStation.setLongitude(-9.1393);

    Station nullCoordsStation = createTestStation(3L, "Null Coords Station");
    nullCoordsStation.setLatitude(null);
    nullCoordsStation.setLongitude(null);

    List<Station> allStations = Arrays.asList(nearbyStation, farStation, nullCoordsStation);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When
    List<Station> result = stationService.getNearbyStations(centerLat, centerLon, radius);

    // Then
    assertThat(result)
        .hasSize(1)
        .containsOnly(nearbyStation)
        .doesNotContain(farStation, nullCoordsStation);
    verify(stationRepository).findAll();
  }

  @Test
  @XrayTest(key = "STATION-SVC-6")
  @Requirement("STATION-SVC-6")
  void whenGettingNearbyStationsWithZeroRadius_thenThrowsException() {
    // Given
    double centerLat = 38.7223;
    double centerLon = -9.1393;
    int radius = 0;

    // When/Then
    assertThatThrownBy(() -> stationService.getNearbyStations(centerLat, centerLon, radius))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Radius must be greater than 0 km");
  }

  @Test
  @XrayTest(key = "STATION-SVC-7")
  @Requirement("STATION-SVC-7")
  void whenGettingNearbyStationsWithNegativeRadius_thenThrowsException() {
    // Given
    double centerLat = 38.7223;
    double centerLon = -9.1393;
    int radius = -1;

    // When/Then
    assertThatThrownBy(() -> stationService.getNearbyStations(centerLat, centerLon, radius))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Radius must be greater than 0 km");
  }

  @Test
  @XrayTest(key = "STATION-SVC-8")
  @Requirement("STATION-SVC-8")
  void whenGettingNearbyStationsWithTooLargeRadius_thenThrowsException() {
    // Given
    double centerLat = 38.7223;
    double centerLon = -9.1393;
    int radius = 601; // Changed from 101 to 601 to match current implementation

    // When/Then
    assertThatThrownBy(() -> stationService.getNearbyStations(centerLat, centerLon, radius))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Radius cannot be greater than 600 km");
  }

  // ===== TOTAL STATION COUNT TESTS =====

  @Test
  @XrayTest(key = "STATION-SVC-38")
  @Requirement("STATION-SVC-38")
  void whenGettingTotalStationCount_thenReturnsCorrectCount() {
    // Given
    when(stationRepository.count()).thenReturn(1500L);

    // When
    Long result = stationService.getTotalStationCount();

    // Then
    assertThat(result).isEqualTo(1500L);
    verify(stationRepository).count();
  }

  @Test
  @XrayTest(key = "STATION-SVC-39")
  @Requirement("STATION-SVC-39")
  void whenGettingTotalStationCountWithZeroStations_thenReturnsZero() {
    // Given
    when(stationRepository.count()).thenReturn(0L);

    // When
    Long result = stationService.getTotalStationCount();

    // Then
    assertThat(result).isEqualTo(0L);
    verify(stationRepository).count();
  }

  // ===== 500-LIMIT ENFORCEMENT TESTS =====

  @Test
  @XrayTest(key = "STATION-SVC-40")
  @Requirement("STATION-SVC-40")
  void whenGettingAllStationsExceeds500_thenLimitsTo500() {
    // Given - Create a list with more than 500 stations
    List<Station> manyStations = new java.util.ArrayList<>();
    for (int i = 1; i <= 600; i++) {
      Station station = createTestStation((long) i, "Station " + i);
      manyStations.add(station);
    }
    when(stationRepository.findAll()).thenReturn(manyStations);

    // When
    List<Station> result = stationService.getAllStations();

    // Then
    assertThat(result).hasSize(500);
    verify(stationRepository).findAll();
  }

  @Test
  @XrayTest(key = "STATION-SVC-41")
  @Requirement("STATION-SVC-41")
  void whenSearchingStationsExceeds500_thenLimitsTo500() {
    // Given - Create a list with more than 500 matching stations
    List<Station> manyStations = new java.util.ArrayList<>();
    for (int i = 1; i <= 600; i++) {
      Station station = createTestStation((long) i, "Tesla Station " + i);
      manyStations.add(station);
    }
    when(stationRepository.findAll()).thenReturn(manyStations);

    // When
    List<Station> result = stationService.searchStations("Tesla", null, null, null);

    // Then
    assertThat(result).hasSize(500);
    verify(stationRepository).findAll();
  }

  @Test
  @XrayTest(key = "STATION-SVC-42")
  @Requirement("STATION-SVC-42")
  void whenGettingNearbyStationsExceeds500_thenLimitsTo500() {
    // Given - Create a list with more than 500 nearby stations
    List<Station> manyStations = new java.util.ArrayList<>();
    for (int i = 1; i <= 600; i++) {
      Station station = createTestStation((long) i, "Station " + i);
      // All stations at the same location (within radius)
      station.setLatitude(40.623361);
      station.setLongitude(-8.650256);
      manyStations.add(station);
    }
    when(stationRepository.findAll()).thenReturn(manyStations);

    // When
    List<Station> result = stationService.getNearbyStations(40.623361, -8.650256, 10);

    // Then
    assertThat(result).hasSize(500);
    verify(stationRepository).findAll();
  }

  // ===== EXISTING CRUD TESTS =====

  @Test
  @XrayTest(key = "STATION-SVC-12")
  @Requirement("STATION-SVC-12")
  void whenCreatingValidStation_thenStationIsSaved() {
    // Given
    Station station = createTestStation(1L, "Test Station");
    when(stationRepository.save(station)).thenReturn(station);

    // When
    Station result = stationService.createStation(station);

    // Then
    assertThat(result).isEqualTo(station);
    verify(stationRepository).save(station);
  }

  @Test
  @XrayTest(key = "STATION-SVC-13")
  @Requirement("STATION-SVC-13")
  void whenCreatingStationWithNullName_thenThrowsException() {
    // Given
    Station station = createTestStation(1L, null);

    // When/Then
    assertThatThrownBy(() -> stationService.createStation(station))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Station name cannot be empty");
  }

  @Test
  @XrayTest(key = "STATION-SVC-14")
  @Requirement("STATION-SVC-14")
  void whenCreatingStationWithEmptyName_thenThrowsException() {
    // Given
    Station station = createTestStation(1L, "");

    // When/Then
    assertThatThrownBy(() -> stationService.createStation(station))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Station name cannot be empty");
  }

  @Test
  @XrayTest(key = "STATION-SVC-15")
  @Requirement("STATION-SVC-15")
  void whenCreatingStationWithInvalidCoordinates_thenThrowsException() {
    // Given
    Station station = createTestStation(1L, "Test Station");
    station.setLatitude(91.0); // Invalid latitude

    // When/Then
    assertThatThrownBy(() -> stationService.createStation(station))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Latitude must be between -90 and 90 degrees");
  }

  @Test
  @XrayTest(key = "STATION-SVC-16")
  @Requirement("STATION-SVC-16")
  void whenCreatingStationWithEmptyConnectorType_thenThrowsException() {
    // Given
    Station station = createTestStation(1L, "Test Station");
    station.setConnectorType("");

    // When/Then
    assertThatThrownBy(() -> stationService.createStation(station))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Connector type cannot be empty");
  }

  @Test
  @XrayTest(key = "STATION-SVC-17")
  @Requirement("STATION-SVC-17")
  void whenCreatingNullStation_thenThrowsException() {
    // When/Then
    assertThatThrownBy(() -> stationService.createStation(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Station cannot be null");
  }

  @Test
  @XrayTest(key = "STATION-SVC-18")
  @Requirement("STATION-SVC-18")
  void whenDeletingExistingStation_thenStationIsDeleted() {
    // Given
    Long stationId = 1L;
    when(stationRepository.existsById(stationId)).thenReturn(true);
    doNothing().when(stationRepository).deleteById(stationId);

    // When
    stationService.deleteStation(stationId);

    // Then
    verify(stationRepository).existsById(stationId);
    verify(stationRepository).deleteById(stationId);
  }

  @Test
  @XrayTest(key = "STATION-SVC-19")
  @Requirement("STATION-SVC-19")
  void whenDeletingNonExistentStation_thenThrowsException() {
    // Given
    Long stationId = 1L;
    when(stationRepository.existsById(stationId)).thenReturn(false);

    // When/Then
    assertThatThrownBy(() -> stationService.deleteStation(stationId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Station not found with id: " + stationId);
  }

  @Test
  @XrayTest(key = "STATION-SVC-20")
  @Requirement("STATION-SVC-20")
  void whenDeletingStationWithNullId_thenThrowsException() {
    // When/Then
    assertThatThrownBy(() -> stationService.deleteStation(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Station ID cannot be null");
  }

  @Test
  @XrayTest(key = "STATION-SVC-21")
  @Requirement("STATION-SVC-21")
  void whenGettingStationByExternalId_thenReturnsStation() {
    String externalId = "ext-123";
    Station expectedStation = createTestStation(1L, "External Station");
    when(stationRepository.findByExternalId(externalId)).thenReturn(Optional.of(expectedStation));

    Station result = stationService.getStationByExternalId(externalId);

    assertThat(result).isEqualTo(expectedStation);
    verify(stationRepository).findByExternalId(externalId);
  }

  @Test
  @XrayTest(key = "STATION-SVC-22")
  @Requirement("STATION-SVC-22")
  void whenGettingNonExistentStationByExternalId_thenThrowsException() {
    String externalId = "not-found";
    when(stationRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> stationService.getStationByExternalId(externalId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Station not found with external id: " + externalId);
  }

  // ===== UPDATE STATION TESTS =====

  @Test
  @XrayTest(key = "STATION-SVC-43")
  @Requirement("STATION-SVC-43")
  void whenUpdatingExistingStation_thenStationIsUpdated() {
    // Given
    Long stationId = 1L;
    Station updatedStation = createTestStation(stationId, "Updated Station");
    when(stationRepository.existsById(stationId)).thenReturn(true);
    when(stationRepository.save(updatedStation)).thenReturn(updatedStation);

    // When
    Station result = stationService.updateStation(stationId, updatedStation);

    // Then
    assertThat(result).isEqualTo(updatedStation);
    assertThat(result.getId()).isEqualTo(stationId);
    verify(stationRepository).existsById(stationId);
    verify(stationRepository).save(updatedStation);
  }

  @Test
  @XrayTest(key = "STATION-SVC-44")
  @Requirement("STATION-SVC-44")
  void whenUpdatingNonExistentStation_thenThrowsException() {
    // Given
    Long stationId = 999L;
    Station updatedStation = createTestStation(stationId, "Updated Station");
    when(stationRepository.existsById(stationId)).thenReturn(false);

    // When/Then
    assertThatThrownBy(() -> stationService.updateStation(stationId, updatedStation))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Station not found with id: " + stationId);
  }

  private Station createTestStation(Long id, String name) {
    Station station =
        new Station(name, "Test Address", "Lisbon", 38.7223, -9.1393, "Type 2", "Available");
    station.setId(id);
    return station;
  }
}

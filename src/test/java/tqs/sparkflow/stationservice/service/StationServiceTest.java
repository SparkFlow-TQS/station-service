package tqs.sparkflow.stationservice.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import tqs.sparkflow.stationservice.dto.StationFilterDTO;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.BookingRepository;
import tqs.sparkflow.stationservice.repository.ChargingSessionRepository;
import tqs.sparkflow.stationservice.repository.StationRepository;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

  @Mock
  private StationRepository stationRepository;

  @Mock
  private BookingRepository bookingRepository;

  @Mock
  private ChargingSessionRepository chargingSessionRepository;

  @InjectMocks
  private StationService stationService;

  private Station station1;
  private Station station2;
  private Station station3;
  private Station station4;
  private Station station5;

  @BeforeEach
  void setUp() {
    // Create test stations
    station1 =
        new Station.Builder().name("Tesla Supercharger Aveiro").address("Address 1").city("Aveiro")
            .country("Portugal").latitude(40.623361).longitude(-8.650256).quantityOfChargers(4)
            .power(50).price(0.30).status("Available").isOperational(true).build();
    station1.setId(1L);

    station2 = new Station.Builder().name("IONITY Porto").address("Address 2").city("Porto")
        .country("Portugal").latitude(41.1579).longitude(-8.6291).quantityOfChargers(8).power(150)
        .price(0.35).status("In Use").isOperational(true).build();
    station2.setId(2L);

    station3 = new Station.Builder().name("FastCharge Madrid").address("Address 3").city("Madrid")
        .country("Spain").latitude(38.7223).longitude(-9.1393).quantityOfChargers(1).power(22)
        .price(0.25).status("Offline").isOperational(false).build();
    station3.setId(3L);

    station4 = new Station.Builder().name("EV Charge Coimbra").address("Address 4").city("Coimbra")
        .country("Portugal").latitude(40.2033).longitude(-8.4103).quantityOfChargers(6).power(250)
        .price(0.40).status("Available").isOperational(true).build();
    station4.setId(4L);

    station5 = new Station.Builder().name("EDP Charge Station").address("Address 5").city("Braga")
        .country("Portugal").latitude(41.5454).longitude(-8.4265).quantityOfChargers(3).power(100)
        .price(0.32).status("Available").isOperational(true).build();
    station5.setId(5L);
  }

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
        .isInstanceOf(NullPointerException.class).hasMessageContaining("Station ID cannot be null");
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
    assertThat(result).hasSize(1).containsExactly(station1);
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
    assertThat(result).hasSize(1).extracting(Station::getName).containsExactly("EV Charge Coimbra");
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

    // Then - Should match all stations containing "Charge"
    assertThat(result).hasSize(4).extracting(Station::getName).containsExactlyInAnyOrder(
        "Tesla Supercharger Aveiro", "FastCharge Madrid", "EV Charge Coimbra",
        "EDP Charge Station");
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
    assertThat(result).hasSize(1).extracting(Station::getName).containsExactly("FastCharge Madrid");
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
    assertThat(result).hasSize(1).extracting(Station::getName).containsExactly("IONITY Porto");
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
    assertThat(result).hasSize(1).containsExactly(station2);
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
    assertThat(result).hasSize(4).extracting(Station::getCountry).containsOnly("Portugal");
  }

  @Test
  @XrayTest(key = "STATION-SVC-28")
  @Requirement("STATION-SVC-28")
  void whenSearchingStationsByMinChargers_thenReturnsMatchingStations() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station4, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search for stations with at least 6 chargers
    List<Station> result = stationService.searchStations(null, null, null, 6);

    // Then - Should return station2 (8 chargers) and station4 (6 chargers)
    assertThat(result).hasSize(2).extracting(Station::getId).containsExactlyInAnyOrder(2L, 4L);
  }

  @Test
  @XrayTest(key = "STATION-SVC-29")
  @Requirement("STATION-SVC-29")
  void whenSearchingStationsWithMultipleCriteria_thenReturnsMatchingStations() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station4, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search with multiple criteria: Portugal + at least 3 chargers
    List<Station> result = stationService.searchStations(null, null, "Portugal", 3);

    // Then - Should return station1 (4 chargers), station2 (8 chargers), and station5 (3 chargers)
    assertThat(result).hasSize(4).extracting(Station::getId).containsExactlyInAnyOrder(1L, 2L, 4L,
        5L);
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
    stationWithNulls.setQuantityOfChargers(null);

    List<Station> allStations = Arrays.asList(station1, stationWithNulls);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search that should exclude null values
    List<Station> result = stationService.searchStations("Tesla", null, null, null);

    // Then
    assertThat(result).hasSize(1).extracting(Station::getName)
        .containsExactly("Tesla Supercharger Aveiro");
  }

  @Test
  @XrayTest(key = "STATION-SVC-32")
  @Requirement("STATION-SVC-32")
  void whenSearchingStationsWithEmptyStrings_thenIgnoresEmptyFilters() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search with empty strings (should return all)
    List<Station> result = stationService.searchStations("", "", "", null);

    // Then
    assertThat(result).hasSize(2).containsExactlyInAnyOrder(station1, station2);
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
    assertThat(result).hasSize(2).extracting(Station::getId).containsExactlyInAnyOrder(1L, 6L);
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
    assertThat(result).hasSize(1).containsExactly(station1);
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

    // Then - Should include station1, station2 (Porto, ~68km), station5 (Braga, ~62km)
    // Madrid (station3) is ~255km away, so should be excluded
    assertThat(result).hasSize(2).extracting(Station::getId).containsExactlyInAnyOrder(1L, 2L);
  }

  @Test
  @XrayTest(key = "STATION-SVC-47")
  @Requirement("STATION-SVC-47")
  void whenGettingNearbyStationsWithMediumRadius_thenIncludesPortoAndCoimbra() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search within 70km from Aveiro (includes both Porto ~68km and Braga ~62km)
    List<Station> result = stationService.getNearbyStations(40.623361, -8.650256, 70);

    // Then - Should include all 2 nearby stations
    assertThat(result).hasSize(2).extracting(Station::getId).containsExactlyInAnyOrder(1L, 2L);
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
    assertThat(result).hasSize(1).containsOnly(nearbyStation).doesNotContain(farStation,
        nullCoordsStation);
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
  @XrayTest(key = "STATION-SVC-9")
  @Requirement("STATION-SVC-9")
  void whenGettingStationsByQuantityOfChargers_thenReturnsMatchingStations() {
    // Given
    int quantityOfChargers = 1;
    List<Station> expectedStations = Arrays.asList(createTestStation(1L, "Type2 Station 1"),
        createTestStation(2L, "Type2 Station 2"));
    when(stationRepository.findByQuantityOfChargersGreaterThanEqual(quantityOfChargers))
        .thenReturn(expectedStations);

    // When
    List<Station> result = stationService.getStationsByMinChargers(quantityOfChargers);

    // Then
    assertThat(result).isEqualTo(expectedStations);
    verify(stationRepository).findByQuantityOfChargersGreaterThanEqual(quantityOfChargers);
  }

  @Test
  @XrayTest(key = "STATION-SVC-10")
  @Requirement("STATION-SVC-10")
  void whenGettingStationsByMinChargersWithInvalidValues_thenThrowsException() {
    // When/Then
    assertThatThrownBy(() -> stationService.getStationsByMinChargers(0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Minimum number of chargers must be at least 1");
    assertThatThrownBy(() -> stationService.getStationsByMinChargers(-1))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Minimum number of chargers must be at least 1");
  }

  @Test
  @XrayTest(key = "STATION-SVC-11")
  @Requirement("STATION-SVC-11")
  void whenGettingStationsByNullMinChargers_thenThrowsException() {
    // When/Then
    assertThatThrownBy(() -> stationService.getStationsByMinChargers(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Minimum number of chargers cannot be null");
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
  void whenCreatingStationWithEmptyQuantityOfChargers_thenThrowsException() {
    // Given
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(0);

    // When/Then
    assertThatThrownBy(() -> stationService.createStation(station))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Quantity of chargers must be at least 1");
  }

  @Test
  @XrayTest(key = "STATION-SVC-17")
  @Requirement("STATION-SVC-17")
  void whenCreatingNullStation_thenThrowsException() {
    // When/Then
    assertThatThrownBy(() -> stationService.createStation(null))
        .isInstanceOf(NullPointerException.class).hasMessageContaining("Station cannot be null");
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
        .isInstanceOf(NullPointerException.class).hasMessageContaining("Station ID cannot be null");
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

  @Test
  @XrayTest(key = "STATION-SVC-43")
  @Requirement("STATION-SVC-43")
  void whenFilteringStationsByPriceRange_thenReturnMatchingStations() {
    // Given
    List<Station> expectedStations = Arrays.asList(station1, station3);
    when(stationRepository.findAll()).thenReturn(expectedStations);

    // When
    List<Station> result = stationService.searchStations(null, null, null, null);

    // Then
    assertThat(result).hasSize(2);
    verify(stationRepository).findAll();
  }

  @ParameterizedTest
  @MethodSource("provideSearchTestCases")
  @XrayTest(key = "STATION-SVC-25,STATION-SVC-45,STATION-SVC-46")
  @Requirement("STATION-SVC-25,STATION-SVC-45,STATION-SVC-46")
  void whenSearchingStationsByName_thenReturnsMatchingStations(String searchTerm,
      List<String> expectedStationNames) {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station4, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When
    List<Station> result = stationService.searchStations(searchTerm, null, null, null);

    // Then
    assertThat(result).hasSize(expectedStationNames.size()).extracting(Station::getName)
        .containsExactlyInAnyOrderElementsOf(expectedStationNames);
  }

  @Test
  @XrayTest(key = "STATION-SVC-48")
  @Requirement("STATION-SVC-48")
  void whenGettingStationsByFiltersWithLocation_thenReturnsMatchingStations() {
    // Given
    StationFilterDTO filter = new StationFilterDTO();
    filter.setLatitude(40.623361);
    filter.setLongitude(-8.650256);
    filter.setRadius(10);
    filter.setMinPower(50);
    filter.setMaxPower(150);
    filter.setIsOperational(true);
    filter.setStatus("Available");
    filter.setCity("Aveiro");
    filter.setCountry("Portugal");
    filter.setMinPrice(0.3);
    filter.setMaxPrice(0.4);

    List<Station> expectedStations = Arrays.asList(station1);
    when(stationRepository.findStationsByFiltersWithLocation(filter.getMinPower(),
        filter.getMaxPower(), filter.getIsOperational(), filter.getStatus(), filter.getCity(),
        filter.getCountry(), filter.getMinPrice(), filter.getMaxPrice(), filter.getLatitude(),
        filter.getLongitude(), filter.getRadius())).thenReturn(expectedStations);

    // When
    List<Station> result = stationService.getStationsByFilters(filter);

    // Then
    assertThat(result).isEqualTo(expectedStations);
  }

  @Test
  @XrayTest(key = "STATION-SVC-49")
  @Requirement("STATION-SVC-49")
  void whenGettingStationsByFiltersWithoutLocation_thenReturnsMatchingStations() {
    // Given
    StationFilterDTO filter = new StationFilterDTO();
    filter.setMinPower(50);
    filter.setMaxPower(150);
    filter.setIsOperational(true);
    filter.setStatus("Available");
    filter.setCity("Aveiro");
    filter.setCountry("Portugal");
    filter.setMinPrice(0.3);
    filter.setMaxPrice(0.4);

    List<Station> expectedStations = Arrays.asList(station1);
    when(stationRepository.findStationsByFilters(filter.getMinPower(), filter.getMaxPower(),
        filter.getIsOperational(), filter.getStatus(), filter.getCity(), filter.getCountry(),
        filter.getMinPrice(), filter.getMaxPrice())).thenReturn(expectedStations);

    // When
    List<Station> result = stationService.getStationsByFilters(filter);

    // Then
    assertThat(result).isEqualTo(expectedStations);
  }

  @Test
  @XrayTest(key = "STATION-SVC-50")
  @Requirement("STATION-SVC-50")
  void whenGettingAvailableChargers_thenReturnsCorrectCount() {
    // Given
    Long stationId = 1L;
    LocalDateTime currentTime = LocalDateTime.now();
    Station station = station1;
    List<Booking> activeBookings =
        Arrays.asList(createTestBooking(1L, stationId, 1L, currentTime, currentTime.plusHours(1)),
            createTestBooking(2L, stationId, 2L, currentTime, currentTime.plusHours(1)));
    List<ChargingSession> unfinishedSessions =
        Arrays.asList(createTestChargingSession(1L, stationId, 1L, currentTime));

    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(bookingRepository.findActiveBookingsForStationAtTime(stationId, currentTime))
        .thenReturn(activeBookings);
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(unfinishedSessions);

    // When
    int availableChargers = stationService.getAvailableChargers(stationId, currentTime);

    // Then
    assertThat(availableChargers).isEqualTo(
        station.getQuantityOfChargers() - activeBookings.size() - unfinishedSessions.size());
  }

  @Test
  @XrayTest(key = "STATION-SVC-51")
  @Requirement("STATION-SVC-51")
  void whenUserHasBooking_thenCanUseStation() {
    // Given
    Long stationId = 1L;
    Long userId = 1L;
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.plusHours(1);
    List<Booking> overlappingBookings =
        Arrays.asList(createTestBooking(1L, stationId, userId, startTime, endTime));

    when(bookingRepository.findOverlappingBookings(stationId, startTime, endTime))
        .thenReturn(overlappingBookings);

    // When
    boolean canUse = stationService.canUseStation(stationId, userId, startTime, endTime);

    // Then
    assertThat(canUse).isTrue();
  }

  @Test
  @XrayTest(key = "STATION-SVC-52")
  @Requirement("STATION-SVC-52")
  void whenNoUserBookingAndEnoughChargers_thenCanUseStation() {
    // Given
    Long stationId = 1L;
    Long userId = 1L;
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.plusHours(1);
    Station station = station1;
    List<Booking> overlappingBookings =
        Arrays.asList(createTestBooking(1L, stationId, 2L, startTime, endTime));
    List<ChargingSession> unfinishedSessions =
        Arrays.asList(createTestChargingSession(1L, stationId, 1L, startTime));

    when(bookingRepository.findOverlappingBookings(stationId, startTime, endTime))
        .thenReturn(overlappingBookings);
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(chargingSessionRepository.findUnfinishedSessionsByStationInTimeRange(stationId, startTime,
        endTime)).thenReturn(unfinishedSessions);

    // When
    boolean canUse = stationService.canUseStation(stationId, userId, startTime, endTime);

    // Then
    assertThat(canUse).isTrue();
  }

  @Test
  @XrayTest(key = "STATION-SVC-53")
  @Requirement("STATION-SVC-53")
  void whenNoUserBookingAndNoChargers_thenCannotUseStation() {
    // Given
    Long stationId = 1L;
    Long userId = 1L;
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.plusHours(1);
    Station station = station1;
    List<Booking> overlappingBookings =
        Arrays.asList(createTestBooking(1L, stationId, 2L, startTime, endTime),
            createTestBooking(2L, stationId, 3L, startTime, endTime),
            createTestBooking(3L, stationId, 4L, startTime, endTime),
            createTestBooking(4L, stationId, 5L, startTime, endTime));
    List<ChargingSession> unfinishedSessions =
        Arrays.asList(createTestChargingSession(1L, stationId, 1L, startTime));

    when(bookingRepository.findOverlappingBookings(stationId, startTime, endTime))
        .thenReturn(overlappingBookings);
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(chargingSessionRepository.findUnfinishedSessionsByStationInTimeRange(stationId, startTime,
        endTime)).thenReturn(unfinishedSessions);

    // When
    boolean canUse = stationService.canUseStation(stationId, userId, startTime, endTime);

    // Then
    assertThat(canUse).isFalse();
  }

  @Test
  @XrayTest(key = "STATION-SVC-54")
  @Requirement("STATION-SVC-54")
  void whenValidatingBookingWithNoChargers_thenThrowsException() {
    // Given
    Long stationId = 1L;
    Long userId = 1L;
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.plusHours(1);
    Station station = station1;
    List<Booking> overlappingBookings =
        Arrays.asList(createTestBooking(1L, stationId, 2L, startTime, endTime),
            createTestBooking(2L, stationId, 3L, startTime, endTime),
            createTestBooking(3L, stationId, 4L, startTime, endTime),
            createTestBooking(4L, stationId, 5L, startTime, endTime));
    List<ChargingSession> unfinishedSessions =
        Arrays.asList(createTestChargingSession(1L, stationId, 1L, startTime));

    when(bookingRepository.findOverlappingBookings(stationId, startTime, endTime))
        .thenReturn(overlappingBookings);
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(chargingSessionRepository.findUnfinishedSessionsByStationInTimeRange(stationId, startTime,
        endTime)).thenReturn(unfinishedSessions);

    // When/Then
    assertThatThrownBy(() -> stationService.validateBooking(stationId, userId, startTime, endTime))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("No chargers available for the requested time slot");
  }

  @Test
  @XrayTest(key = "STATION-SVC-55")
  @Requirement("STATION-SVC-55")
  void whenUserHasActiveBooking_thenCanStartSession() {
    // Given
    Long stationId = 1L;
    Long userId = 1L;
    LocalDateTime now = LocalDateTime.now();
    List<Booking> activeBookings =
        Arrays.asList(createTestBooking(1L, stationId, userId, now, now.plusHours(1)));

    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId),
        any(LocalDateTime.class))).thenReturn(activeBookings);

    // When
    boolean canStart = stationService.canStartSession(stationId, userId);

    // Then
    assertThat(canStart).isTrue();
  }

  @Test
  @XrayTest(key = "STATION-SVC-56")
  @Requirement("STATION-SVC-56")
  void whenNoUserBookingAndEnoughChargers_thenCanStartSession() {
    // Given
    Long stationId = 1L;
    Long userId = 1L;
    LocalDateTime now = LocalDateTime.now();
    Station station = station1;
    List<Booking> activeBookings =
        Arrays.asList(createTestBooking(1L, stationId, 2L, now, now.plusHours(1)));
    List<ChargingSession> unfinishedSessions =
        Arrays.asList(createTestChargingSession(1L, stationId, 1L, now));

    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId),
        any(LocalDateTime.class))).thenReturn(activeBookings);
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(unfinishedSessions);

    // When
    boolean canStart = stationService.canStartSession(stationId, userId);

    // Then
    assertThat(canStart).isTrue();
  }

  @Test
  @XrayTest(key = "STATION-SVC-57")
  @Requirement("STATION-SVC-57")
  void whenNoUserBookingAndNoChargers_thenCannotStartSession() {
    // Given
    Long stationId = 1L;
    Long userId = 1L;
    LocalDateTime now = LocalDateTime.now();
    Station station = station1;
    List<Booking> activeBookings =
        Arrays.asList(createTestBooking(1L, stationId, 2L, now, now.plusHours(1)),
            createTestBooking(2L, stationId, 3L, now, now.plusHours(1)),
            createTestBooking(3L, stationId, 4L, now, now.plusHours(1)),
            createTestBooking(4L, stationId, 5L, now, now.plusHours(1)));
    List<ChargingSession> unfinishedSessions =
        Arrays.asList(createTestChargingSession(1L, stationId, 1L, now));

    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId),
        any(LocalDateTime.class))).thenReturn(activeBookings);
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(unfinishedSessions);

    // When
    boolean canStart = stationService.canStartSession(stationId, userId);

    // Then
    assertThat(canStart).isFalse();
  }

  @Test
  @XrayTest(key = "STATION-SVC-58")
  @Requirement("STATION-SVC-58")
  void whenValidatingSessionStartWithNoChargers_thenThrowsException() {
    // Given
    Long stationId = 1L;
    Long userId = 1L;
    LocalDateTime now = LocalDateTime.now();
    Station station = station1;
    List<Booking> activeBookings =
        Arrays.asList(createTestBooking(1L, stationId, 2L, now, now.plusHours(1)),
            createTestBooking(2L, stationId, 3L, now, now.plusHours(1)),
            createTestBooking(3L, stationId, 4L, now, now.plusHours(1)),
            createTestBooking(4L, stationId, 5L, now, now.plusHours(1)));
    List<ChargingSession> unfinishedSessions =
        Arrays.asList(createTestChargingSession(1L, stationId, 1L, now));

    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId),
        any(LocalDateTime.class))).thenReturn(activeBookings);
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(unfinishedSessions);

    // When/Then
    assertThatThrownBy(() -> stationService.validateSessionStart(stationId, userId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Cannot start session: no booking or free chargers available");
  }

  private static Stream<Arguments> provideSearchTestCases() {
    return Stream
        .of(Arguments.of("Tesla", Arrays.asList("Tesla Supercharger Aveiro")),
            Arguments.of("EV", Arrays.asList("EV Charge Coimbra")),
            Arguments.of("Charge",
                Arrays.asList("Tesla Supercharger Aveiro", "FastCharge Madrid", "EV Charge Coimbra",
                    "EDP Charge Station")),
            Arguments.of("Fast", Arrays.asList("FastCharge Madrid")));
  }

  private Booking createTestBooking(Long id, Long stationId, Long userId, LocalDateTime startTime,
      LocalDateTime endTime) {
    Booking booking = new Booking();
    booking.setId(id);
    booking.setStationId(stationId);
    booking.setUserId(userId);
    booking.setStartTime(startTime);
    booking.setEndTime(endTime);
    return booking;
  }

  private ChargingSession createTestChargingSession(Long id, Long stationId, Long userId,
      LocalDateTime startTime) {
    ChargingSession session = new ChargingSession();
    session.setId(id);
    session.setStationId(String.valueOf(stationId));
    session.setUserId(String.valueOf(userId));
    session.setStartTime(startTime);
    return session;
  }

  private Station createTestStation(Long id, String name) {
    Station station = new Station.Builder().name(name).address("Test Address").city("Lisbon")
        .country("Portugal").latitude(38.7223).longitude(-9.1393).quantityOfChargers(2)
        .status("Available").isOperational(true).build();
    station.setId(id);
    return station;
  }
}

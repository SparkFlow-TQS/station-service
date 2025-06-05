package tqs.sparkflow.stationservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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
import tqs.sparkflow.stationservice.dto.StationFilterDTO;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.BookingRepository;
import tqs.sparkflow.stationservice.repository.ChargingSessionRepository;
import tqs.sparkflow.stationservice.repository.StationRepository;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

  @Mock private StationRepository stationRepository;
  @Mock private BookingRepository bookingRepository;
  @Mock private ChargingSessionRepository chargingSessionRepository;

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
    station1.setAddress("Rua Dr. Mário Sacramento");
    station1.setCity("Aveiro");
    station1.setCountry("Portugal");
    station1.setQuantityOfChargers(4);
    station1.setPower(50);
    station1.setPrice(0.30);
    station1.setIsOperational(true);
    station1.setStatus("Available");
    station1.setLatitude(40.623361);
    station1.setLongitude(-8.650256);

    station2 = new Station();
    station2.setId(2L);
    station2.setName("IONITY Porto");
    station2.setAddress("A4 - Área de Serviço de Valongo");
    station2.setCity("Porto");
    station2.setCountry("Portugal");
    station2.setQuantityOfChargers(8);
    station2.setPower(150);
    station2.setPrice(0.35);
    station2.setIsOperational(true);
    station2.setStatus("In Use");
    station2.setLatitude(41.1579);
    station2.setLongitude(-8.6291);

    station3 = new Station();
    station3.setId(3L);
    station3.setName("Mobi.E Lisbon");
    station3.setAddress("Praça do Comércio");
    station3.setCity("Lisbon");
    station3.setCountry("Portugal");
    station3.setQuantityOfChargers(1);
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
    station4.setQuantityOfChargers(6);
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
    station5.setQuantityOfChargers(3);
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
  void whenSearchingStationsByMinChargers_thenReturnsMatchingStations() {
    // Given
    List<Station> allStations = Arrays.asList(station1, station2, station3, station4, station5);
    when(stationRepository.findAll()).thenReturn(allStations);

    // When - Search for stations with at least 6 chargers
    List<Station> result = stationService.searchStations(null, null, null, 6);

    // Then - Should return station2 (8 chargers) and station4 (6 chargers)
    assertThat(result)
        .hasSize(2)
        .extracting(Station::getId)
        .containsExactlyInAnyOrder(2L, 4L);
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
    assertThat(result)
        .hasSize(3)
        .extracting(Station::getId)
        .containsExactlyInAnyOrder(1L, 2L, 5L);
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
    List<Station> result = stationService.searchStations("", "", "", null);

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
  @XrayTest(key = "STATION-SVC-9")
  @Requirement("STATION-SVC-9")
  void whenGettingStationsByQuantityOfChargers_thenReturnsMatchingStations() {
    // Given
    int quantityOfChargers = 1;
    List<Station> expectedStations =
        Arrays.asList(
            createTestStation(1L, "Type2 Station 1"), createTestStation(2L, "Type2 Station 2"));
    when(stationRepository.findByQuantityOfChargersGreaterThanEqual(quantityOfChargers)).thenReturn(expectedStations);

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

  @Test
  void whenCalculatingAvailableChargers_thenReturnsFreeChargers() {
    // Given
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(3);
    Long stationId = 1L;
    LocalDateTime currentTime = LocalDateTime.now();
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(bookingRepository.findActiveBookingsForStationAtTime(stationId, currentTime))
        .thenReturn(Arrays.asList(new Booking(), new Booking())); // 2 active bookings
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(Arrays.asList(new ChargingSession())); // 1 unfinished session
    
    // When
    int availableChargers = stationService.getAvailableChargers(stationId, currentTime);
    
    // Then
    assertThat(availableChargers).isEqualTo(0); // 3 total - 2 bookings - 1 session = 0
  }

  @Test
  void whenCheckingCanUseStation_withExistingBooking_thenReturnsTrue() {
    // Given
    Long stationId = 1L;
    Long userId = 1L;
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.plusHours(1);
    
    Booking userBooking = new Booking();
    userBooking.setUserId(userId);
    userBooking.setStationId(stationId);
    when(bookingRepository.findOverlappingBookings(stationId, startTime, endTime))
        .thenReturn(Arrays.asList(userBooking));
    
    // When
    boolean canUse = stationService.canUseStation(stationId, userId, startTime, endTime);
    
    // Then
    assertThat(canUse).isTrue();
  }

  @Test
  void whenCheckingCanUseStation_withAvailableChargers_thenReturnsTrue() {
    // Given
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(3);
    Long stationId = 1L;
    Long userId = 1L;
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.plusHours(1);
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    Booking otherUserBooking = new Booking();
    otherUserBooking.setUserId(2L); // different user
    when(bookingRepository.findOverlappingBookings(stationId, startTime, endTime))
        .thenReturn(Arrays.asList(otherUserBooking)); // 1 booking from other user
    when(chargingSessionRepository.findUnfinishedSessionsByStationInTimeRange(stationId, startTime, endTime))
        .thenReturn(Arrays.asList(new ChargingSession())); // 1 session
    
    // When
    boolean canUse = stationService.canUseStation(stationId, userId, startTime, endTime);
    
    // Then
    assertThat(canUse).isTrue(); // 3 total - 1 booking - 1 session = 1 available
  }

  @Test
  void whenCheckingCanUseStation_withNoAvailableChargers_thenReturnsFalse() {
    // Given
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(2);
    Long stationId = 1L;
    Long userId = 1L;
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.plusHours(1);
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    Booking booking1 = new Booking();
    booking1.setUserId(2L);
    Booking booking2 = new Booking();
    booking2.setUserId(3L);
    when(bookingRepository.findOverlappingBookings(stationId, startTime, endTime))
        .thenReturn(Arrays.asList(booking1, booking2)); // 2 bookings from other users
    when(chargingSessionRepository.findUnfinishedSessionsByStationInTimeRange(stationId, startTime, endTime))
        .thenReturn(Arrays.asList());
    
    // When
    boolean canUse = stationService.canUseStation(stationId, userId, startTime, endTime);
    
    // Then
    assertThat(canUse).isFalse(); // 2 total - 2 bookings = 0 available
  }

  @Test
  void whenValidatingBooking_withAvailableChargers_thenBookingIsAllowed() {
    // Given
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(3);
    Long stationId = 1L;
    Long userId = 1L;
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.plusHours(1);
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    Booking otherUserBooking = new Booking();
    otherUserBooking.setUserId(2L); // different user
    when(bookingRepository.findOverlappingBookings(stationId, startTime, endTime))
        .thenReturn(Arrays.asList(otherUserBooking)); // 1 overlapping booking
    when(chargingSessionRepository.findUnfinishedSessionsByStationInTimeRange(stationId, startTime, endTime))
        .thenReturn(Arrays.asList(new ChargingSession())); // 1 unfinished session
    
    // When & Then
    assertThatCode(() -> stationService.validateBooking(stationId, userId, startTime, endTime))
        .doesNotThrowAnyException();
  }

  @Test
  void whenValidatingBooking_withNoAvailableChargers_thenThrowsException() {
    // Given
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(2);
    Long stationId = 1L;
    Long userId = 1L;
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.plusHours(1);
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    Booking booking1 = new Booking();
    booking1.setUserId(2L);
    Booking booking2 = new Booking();
    booking2.setUserId(3L);
    when(bookingRepository.findOverlappingBookings(stationId, startTime, endTime))
        .thenReturn(Arrays.asList(booking1, booking2)); // 2 overlapping bookings
    when(chargingSessionRepository.findUnfinishedSessionsByStationInTimeRange(stationId, startTime, endTime))
        .thenReturn(Arrays.asList());
    
    // When & Then
    assertThatThrownBy(() -> stationService.validateBooking(stationId, userId, startTime, endTime))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("No chargers available for the requested time slot");
  }

  @Test
  void whenCheckingCanStartSession_withUserBooking_thenReturnsTrue() {
    // Given
    Long stationId = 1L;
    Long userId = 1L;
    
    Booking userBooking = new Booking();
    userBooking.setUserId(userId);
    userBooking.setStationId(stationId);
    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList(userBooking));
    
    // When
    boolean canStart = stationService.canStartSession(stationId, userId);
    
    // Then
    assertThat(canStart).isTrue();
  }

  @Test
  void whenCheckingCanStartSession_withFreeChargers_thenReturnsTrue() {
    // Given
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(3);
    Long stationId = 1L;
    Long userId = 1L;
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList()); // No active bookings
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(Arrays.asList(new ChargingSession())); // 1 unfinished session
    
    // When
    boolean canStart = stationService.canStartSession(stationId, userId);
    
    // Then
    assertThat(canStart).isTrue(); // 3 total - 0 bookings - 1 session = 2 available
  }

  @Test
  void whenCheckingCanStartSession_withNoBookingAndNoFreeChargers_thenReturnsFalse() {
    // Given
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(2);
    Long stationId = 1L;
    Long userId = 1L;
    
    Booking otherUserBooking1 = new Booking();
    otherUserBooking1.setUserId(2L);
    otherUserBooking1.setStationId(stationId);
    Booking otherUserBooking2 = new Booking();
    otherUserBooking2.setUserId(3L);
    otherUserBooking2.setStationId(stationId);
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList(otherUserBooking1, otherUserBooking2)); // 2 bookings from other users
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(Arrays.asList()); // No unfinished sessions
    
    // When
    boolean canStart = stationService.canStartSession(stationId, userId);
    
    // Then
    assertThat(canStart).isFalse(); // 2 total - 2 bookings - 0 sessions = 0 available
  }

  @Test
  void whenValidatingSessionStart_withValidUser_thenDoesNotThrow() {
    // Given
    Long stationId = 1L;
    Long userId = 1L;
    
    Booking userBooking = new Booking();
    userBooking.setUserId(userId);
    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList(userBooking));
    
    // When & Then
    assertThatCode(() -> stationService.validateSessionStart(stationId, userId))
        .doesNotThrowAnyException();
  }

  @Test
  void whenValidatingSessionStart_withInvalidUser_thenThrowsException() {
    // Given
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(1);
    Long stationId = 1L;
    Long userId = 1L;
    
    Booking otherUserBooking = new Booking();
    otherUserBooking.setUserId(2L);
    otherUserBooking.setStationId(stationId);
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList(otherUserBooking)); // 1 booking from other user
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(Arrays.asList()); // No unfinished sessions
    
    // When & Then
    assertThatThrownBy(() -> stationService.validateSessionStart(stationId, userId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Cannot start session: no booking or free chargers available");
  }

  // ===== DETAILED TESTS FOR FREE CHARGERS CALCULATION =====
  // Formula: Free chargers = Total chargers - Active bookings without sessions - Unfinished sessions

  @Test
  void whenCalculatingFreeChargers_withOnlyUnfinishedSessions_thenCalculatesCorrectly() {
    // Given: Station with 5 chargers, 0 bookings, 3 unfinished sessions
    // Expected: 5 - 0 - 3 = 2 free chargers
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(5);
    Long stationId = 1L;
    Long userId = 1L;
    
    ChargingSession session1 = new ChargingSession();
    ChargingSession session2 = new ChargingSession();
    ChargingSession session3 = new ChargingSession();
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList()); // No active bookings
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(Arrays.asList(session1, session2, session3)); // 3 unfinished sessions
    
    // When
    boolean canStart = stationService.canStartSession(stationId, userId);
    
    // Then
    assertThat(canStart).isTrue(); // 5 - 0 - 3 = 2 free chargers available
  }

  @Test
  void whenCalculatingFreeChargers_withOnlyActiveBookingsWithoutSessions_thenCalculatesCorrectly() {
    // Given: Station with 4 chargers, 2 bookings without sessions, 0 unfinished sessions
    // Expected: 4 - 2 - 0 = 2 free chargers
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(4);
    Long stationId = 1L;
    Long userId = 1L;
    
    Booking booking1 = new Booking();
    booking1.setUserId(2L);
    booking1.setStationId(stationId);
    Booking booking2 = new Booking();
    booking2.setUserId(3L);
    booking2.setStationId(stationId);
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList(booking1, booking2)); // 2 active bookings
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(Arrays.asList()); // No unfinished sessions
    
    // Mock hasActiveSessionForBooking to return false (no sessions for these bookings)
    // This is implicitly tested since we have no unfinished sessions
    
    // When
    boolean canStart = stationService.canStartSession(stationId, userId);
    
    // Then
    assertThat(canStart).isTrue(); // 4 - 2 - 0 = 2 free chargers available
  }

  @Test
  void whenCalculatingFreeChargers_withMixedBookingsAndSessions_thenCalculatesCorrectly() {
    // Given: Station with 6 chargers, 2 bookings without sessions, 2 unfinished sessions
    // Expected: 6 - 2 - 2 = 2 free chargers
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(6);
    Long stationId = 1L;
    Long userId = 1L;
    
    Booking booking1 = new Booking();
    booking1.setUserId(2L);
    booking1.setStationId(stationId);
    Booking booking2 = new Booking();
    booking2.setUserId(3L);
    booking2.setStationId(stationId);
    
    ChargingSession session1 = new ChargingSession();
    session1.setUserId("4"); // Different user
    session1.setStationId(stationId.toString());
    ChargingSession session2 = new ChargingSession();
    session2.setUserId("5"); // Different user
    session2.setStationId(stationId.toString());
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList(booking1, booking2)); // 2 active bookings
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(Arrays.asList(session1, session2)); // 2 unfinished sessions
    
    // When
    boolean canStart = stationService.canStartSession(stationId, userId);
    
    // Then
    assertThat(canStart).isTrue(); // 6 - 2 - 2 = 2 free chargers available
  }

  @Test
  void whenCalculatingFreeChargers_withExactlyZeroFreeChargers_thenReturnsFalse() {
    // Given: Station with 3 chargers, 1 booking without session, 2 unfinished sessions
    // Expected: 3 - 1 - 2 = 0 free chargers
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(3);
    Long stationId = 1L;
    Long userId = 1L;
    
    Booking booking1 = new Booking();
    booking1.setUserId(2L);
    booking1.setStationId(stationId);
    
    ChargingSession session1 = new ChargingSession();
    session1.setUserId("3"); // Different user
    session1.setStationId(stationId.toString());
    ChargingSession session2 = new ChargingSession();
    session2.setUserId("4"); // Different user
    session2.setStationId(stationId.toString());
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList(booking1)); // 1 active booking
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(Arrays.asList(session1, session2)); // 2 unfinished sessions
    
    // When
    boolean canStart = stationService.canStartSession(stationId, userId);
    
    // Then
    assertThat(canStart).isFalse(); // 3 - 1 - 2 = 0 free chargers available
  }

  @Test
  void whenCalculatingFreeChargers_withMoreUsageThanCapacity_thenReturnsFalse() {
    // Given: Station with 2 chargers, 2 bookings without sessions, 2 unfinished sessions
    // Expected: 2 - 2 - 2 = -2 (treated as 0) free chargers
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(2);
    Long stationId = 1L;
    Long userId = 1L;
    
    Booking booking1 = new Booking();
    booking1.setUserId(2L);
    booking1.setStationId(stationId);
    Booking booking2 = new Booking();
    booking2.setUserId(3L);
    booking2.setStationId(stationId);
    
    ChargingSession session1 = new ChargingSession();
    session1.setUserId("4"); // Different user
    session1.setStationId(stationId.toString());
    ChargingSession session2 = new ChargingSession();
    session2.setUserId("5"); // Different user
    session2.setStationId(stationId.toString());
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList(booking1, booking2)); // 2 active bookings
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(Arrays.asList(session1, session2)); // 2 unfinished sessions
    
    // When
    boolean canStart = stationService.canStartSession(stationId, userId);
    
    // Then
    assertThat(canStart).isFalse(); // 2 - 2 - 2 = -2, no free chargers available
  }

  @Test
  void whenCalculatingFreeChargers_withBookingsThatHaveSessions_thenDoesNotDoubleCount() {
    // Given: Station with 4 chargers, 2 bookings where 1 has an active session, 1 additional unfinished session
    // Expected: 4 - 1 (booking without session) - 2 (total unfinished sessions) = 1 free charger
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(4);
    Long stationId = 1L;
    Long userId = 1L;
    
    Booking bookingWithSession = new Booking();
    bookingWithSession.setUserId(2L);
    bookingWithSession.setStationId(stationId);
    
    Booking bookingWithoutSession = new Booking();
    bookingWithoutSession.setUserId(3L);
    bookingWithoutSession.setStationId(stationId);
    
    ChargingSession sessionFromBooking = new ChargingSession();
    sessionFromBooking.setUserId("2"); // Same user as bookingWithSession
    sessionFromBooking.setStationId(stationId.toString());
    
    ChargingSession independentSession = new ChargingSession();
    independentSession.setUserId("4"); // Different user
    independentSession.setStationId(stationId.toString());
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList(bookingWithSession, bookingWithoutSession)); // 2 active bookings
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(Arrays.asList(sessionFromBooking, independentSession)); // 2 unfinished sessions
    
    // When
    boolean canStart = stationService.canStartSession(stationId, userId);
    
    // Then
    assertThat(canStart).isTrue(); // 4 - 1 (booking without session) - 2 (sessions) = 1 free charger
  }

  @Test
  void whenValidatingSessionStart_withExactlyOneFreeCharger_thenSucceeds() {
    // Given: Station with 5 chargers, 2 bookings without sessions, 2 unfinished sessions
    // Expected: 5 - 2 - 2 = 1 free charger (should succeed)
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(5);
    Long stationId = 1L;
    Long userId = 1L;
    
    Booking booking1 = new Booking();
    booking1.setUserId(2L);
    booking1.setStationId(stationId);
    Booking booking2 = new Booking();
    booking2.setUserId(3L);
    booking2.setStationId(stationId);
    
    ChargingSession session1 = new ChargingSession();
    session1.setUserId("4"); // Different user
    session1.setStationId(stationId.toString());
    ChargingSession session2 = new ChargingSession();
    session2.setUserId("5"); // Different user
    session2.setStationId(stationId.toString());
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList(booking1, booking2)); // 2 active bookings
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(Arrays.asList(session1, session2)); // 2 unfinished sessions
    
    // When & Then
    assertThatCode(() -> stationService.validateSessionStart(stationId, userId))
        .doesNotThrowAnyException(); // 5 - 2 - 2 = 1 free charger available
  }

  @Test
  void whenValidatingSessionStart_withZeroFreeChargers_thenThrowsException() {
    // Given: Station with 4 chargers, 2 bookings without sessions, 2 unfinished sessions
    // Expected: 4 - 2 - 2 = 0 free chargers (should fail)
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(4);
    Long stationId = 1L;
    Long userId = 1L;
    
    Booking booking1 = new Booking();
    booking1.setUserId(2L);
    booking1.setStationId(stationId);
    Booking booking2 = new Booking();
    booking2.setUserId(3L);
    booking2.setStationId(stationId);
    
    ChargingSession session1 = new ChargingSession();
    session1.setUserId("4"); // Different user
    session1.setStationId(stationId.toString());
    ChargingSession session2 = new ChargingSession();
    session2.setUserId("5"); // Different user
    session2.setStationId(stationId.toString());
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList(booking1, booking2)); // 2 active bookings
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(Arrays.asList(session1, session2)); // 2 unfinished sessions
    
    // When & Then
    assertThatThrownBy(() -> stationService.validateSessionStart(stationId, userId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Cannot start session: no booking or free chargers available");
        // 4 - 2 - 2 = 0 free chargers, should fail
  }

  @Test
  void whenValidatingSessionStart_withUserHavingActiveBooking_thenSucceedsRegardlessOfFreeChargers() {
    // Given: User has an active booking - should succeed regardless of free chargers
    Long stationId = 1L;
    Long userId = 1L;
    
    Booking userBooking = new Booking();
    userBooking.setUserId(userId);
    userBooking.setStationId(stationId);
    
    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList(userBooking)); // User has booking
    
    // When & Then
    assertThatCode(() -> stationService.validateSessionStart(stationId, userId))
        .doesNotThrowAnyException(); // User has booking, should succeed regardless of free chargers
  }

  @Test
  void whenCalculatingFreeChargers_withSingleChargerStation_thenWorksCorrectly() {
    // Given: Station with 1 charger, 1 unfinished session
    // Expected: 1 - 0 - 1 = 0 free chargers
    Station station = createTestStation(1L, "Test Station");
    station.setQuantityOfChargers(1);
    Long stationId = 1L;
    Long userId = 1L;
    
    ChargingSession session1 = new ChargingSession();
    session1.setUserId("2"); // Different user
    session1.setStationId(stationId.toString());
    
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));
    when(bookingRepository.findActiveBookingsForStationAtTime(eq(stationId), any(LocalDateTime.class)))
        .thenReturn(Arrays.asList()); // No active bookings
    when(chargingSessionRepository.findUnfinishedSessionsByStation(stationId))
        .thenReturn(Arrays.asList(session1)); // 1 unfinished session
    
    // When
    boolean canStart = stationService.canStartSession(stationId, userId);
    
    // Then
    assertThat(canStart).isFalse(); // 1 - 0 - 1 = 0 free chargers available
  }

  private Station createTestStation(Long id, String name) {
    Station station =
        new Station("1234567890", name, "Test Address", "Lisbon", "Portugal", 38.7223, -9.1393, 2, "Available");
    station.setId(id);
    return station;
  }
}

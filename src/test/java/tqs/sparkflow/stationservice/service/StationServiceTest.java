package tqs.sparkflow.stationservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.sparkflow.stationservice.dto.StationFilterDTO;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

  @Mock private StationRepository stationRepository;

  @InjectMocks private StationService stationService;

  private Station station1;
  private Station station2;
  private Station station3;

  @BeforeEach
  void setUp() {
    // Create test stations
    station1 = new Station.Builder()
        .name("Station 1")
        .address("Address 1")
        .city("Aveiro")
        .country("Portugal")
        .latitude(40.623361)
        .longitude(-8.650256)
        .connectorType("Type 2")
        .power(50)
        .price(0.30)
        .status("Available")
        .isOperational(true)
        .build();
    station1.setId(1L);

    station2 = new Station.Builder()
        .name("Station 2")
        .address("Address 2")
        .city("Porto")
        .country("Portugal")
        .latitude(41.1579)
        .longitude(-8.6291)
        .connectorType("CCS")
        .power(150)
        .price(0.35)
        .status("In Use")
        .isOperational(true)
        .build();
    station2.setId(2L);

    station3 = new Station.Builder()
        .name("Station 3")
        .address("Address 3")
        .city("Lisbon")
        .country("Portugal")
        .latitude(38.7223)
        .longitude(-9.1393)
        .connectorType("Type 2")
        .power(22)
        .price(0.25)
        .status("Offline")
        .isOperational(false)
        .build();
    station3.setId(3L);
  }

  @Test
  @XrayTest(key = "STATION-SVC-1")
  @Requirement("STATION-SVC-1")
  void whenGettingAllStations_thenReturnsAllStations() {
    // Given
    List<Station> expectedStations =
        Arrays.asList(createTestStation(1L, "Station 1"), createTestStation(2L, "Station 2"));
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
    Station expectedStation = createTestStation(stationId, "Test Station");
    when(stationRepository.findById(stationId)).thenReturn(Optional.of(expectedStation));

    // When
    Station result = stationService.getStationById(stationId);

    // Then
    assertThat(result).isEqualTo(expectedStation);
    verify(stationRepository).findById(stationId);
  }

  @Test
  @XrayTest(key = "STATION-SVC-3")
  @Requirement("STATION-SVC-3")
  void whenGettingNonExistentStationById_thenThrowsException() {
    // Given
    Long stationId = 1L;
    when(stationRepository.findById(stationId)).thenReturn(Optional.empty());

    // When/Then
    assertThatThrownBy(() -> stationService.getStationById(stationId))
        .isInstanceOf(RuntimeException.class)
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
    int radius = 101;

    // When/Then
    assertThatThrownBy(() -> stationService.getNearbyStations(centerLat, centerLon, radius))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Radius cannot be greater than 100 km");
  }

  @Test
  @XrayTest(key = "STATION-SVC-9")
  @Requirement("STATION-SVC-9")
  void whenGettingStationsByConnectorType_thenReturnsMatchingStations() {
    // Given
    String connectorType = "Type2";
    List<Station> expectedStations =
        Arrays.asList(
            createTestStation(1L, "Type2 Station 1"), createTestStation(2L, "Type2 Station 2"));
    when(stationRepository.findByConnectorType(connectorType)).thenReturn(expectedStations);

    // When
    List<Station> result = stationService.getStationsByConnectorType(connectorType);

    // Then
    assertThat(result).isEqualTo(expectedStations);
    verify(stationRepository).findByConnectorType(connectorType);
  }

  @Test
  @XrayTest(key = "STATION-SVC-10")
  @Requirement("STATION-SVC-10")
  void whenGettingStationsByEmptyConnectorType_thenThrowsException() {
    // When/Then
    assertThatThrownBy(() -> stationService.getStationsByConnectorType(""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Connector type cannot be empty");
  }

  @Test
  @XrayTest(key = "STATION-SVC-11")
  @Requirement("STATION-SVC-11")
  void whenGettingStationsByNullConnectorType_thenThrowsException() {
    // When/Then
    assertThatThrownBy(() -> stationService.getStationsByConnectorType(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Connector type cannot be null");
  }

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

  @Test
  void whenFilterByConnectorType_thenReturnMatchingStations() {
    // Given
    StationFilterDTO filter = new StationFilterDTO();
    filter.setConnectorType("Type 2");
    List<Station> expectedStations = Arrays.asList(station1, station3);
    when(stationRepository.findStationsByFilters("Type 2", null, null, null, null, null, null, null, null))
        .thenReturn(expectedStations);

    // When
    List<Station> result = stationService.getStationsByFilters(filter);

    // Then
    assertThat(result)
        .hasSize(2)
        .allMatch(station -> station.getConnectorType().equals("Type 2"));
    verify(stationRepository).findStationsByFilters("Type 2", null, null, null, null, null, null, null, null);
  }

  @Test
  void whenFilterByPriceRange_thenReturnMatchingStations() {
    // Given
    StationFilterDTO filter = new StationFilterDTO();
    filter.setMinPrice(0.25);
    filter.setMaxPrice(0.30);
    List<Station> expectedStations = Arrays.asList(station1, station3);
    when(stationRepository.findStationsByFilters(null, null, null, null, null, null, null, 0.25, 0.30))
        .thenReturn(expectedStations);

    // When
    List<Station> result = stationService.getStationsByFilters(filter);

    // Then
    assertThat(result)
        .hasSize(2)
        .allMatch(station -> station.getPrice() >= 0.25 && station.getPrice() <= 0.30);
    verify(stationRepository).findStationsByFilters(null, null, null, null, null, null, null, 0.25, 0.30);
  }

  @Test
  void whenFilterByPowerRange_thenReturnMatchingStations() {
    // Arrange
    StationFilterDTO filters = new StationFilterDTO();
    filters.setMinPower(40);
    filters.setMaxPower(60);
    when(stationRepository.findStationsByFilters(null, 40, 60, null, null, null, null, null, null))
        .thenReturn(List.of(station1));

    // Act
    List<Station> result = stationService.getStationsByFilters(filters);

    // Assert
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getPower()).isEqualTo(50);
    verify(stationRepository).findStationsByFilters(null, 40, 60, null, null, null, null, null, null);
  }

  @Test
  void whenFilterByLocation_thenReturnMatchingStations() {
    // Arrange
    StationFilterDTO filters = new StationFilterDTO();
    filters.setLatitude(40.623361);
    filters.setLongitude(-8.650256);
    filters.setRadius(10);
    when(stationRepository.findStationsByFiltersWithLocation(
        null, null, null, null, null, null, null, null, null, 
        40.623361, -8.650256, 10))
        .thenReturn(List.of(station1));

    // Act
    List<Station> result = stationService.getStationsByFilters(filters);

    // Assert
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(1L);
    verify(stationRepository).findStationsByFiltersWithLocation(
        null, null, null, null, null, null, null, null, null,
        40.623361, -8.650256, 10);
  }

  @Test
  void whenFilterByOperationalStatus_thenReturnMatchingStations() {
    // Given
    StationFilterDTO filter = new StationFilterDTO();
    filter.setIsOperational(true);
    List<Station> expectedStations = Arrays.asList(station1, station2);
    when(stationRepository.findStationsByFilters(null, null, null, true, null, null, null, null, null))
        .thenReturn(expectedStations);

    // When
    List<Station> result = stationService.getStationsByFilters(filter);

    // Then
    assertThat(result)
        .hasSize(2)
        .allMatch(Station::getIsOperational);
    verify(stationRepository).findStationsByFilters(null, null, null, true, null, null, null, null, null);
  }

  @Test
  void whenFilterByCity_thenReturnMatchingStations() {
    // Arrange
    StationFilterDTO filters = new StationFilterDTO();
    filters.setCity("Aveiro");
    when(stationRepository.findStationsByFilters(null, null, null, null, null, "Aveiro", null, null, null))
        .thenReturn(List.of(station1));

    // Act
    List<Station> result = stationService.getStationsByFilters(filters);

    // Assert
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCity()).isEqualTo("Aveiro");
    verify(stationRepository).findStationsByFilters(null, null, null, null, null, "Aveiro", null, null, null);
  }

  @Test
  void whenFilterByMultipleCriteria_thenReturnMatchingStations() {
    // Arrange
    StationFilterDTO filters = new StationFilterDTO();
    filters.setConnectorType("Type 2");
    filters.setMinPrice(0.25);
    filters.setMaxPrice(0.35);
    filters.setIsOperational(true);
    when(stationRepository.findStationsByFilters("Type 2", null, null, true, null, null, null, 0.25, 0.35))
        .thenReturn(List.of(station1));

    // Act
    List<Station> result = stationService.getStationsByFilters(filters);

    // Assert
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getConnectorType()).isEqualTo("Type 2");
    assertThat(result.get(0).getPrice()).isBetween(0.25, 0.35);
    assertThat(result.get(0).getIsOperational()).isTrue();
    verify(stationRepository).findStationsByFilters("Type 2", null, null, true, null, null, null, 0.25, 0.35);
  }

  @Test
  void whenNoFiltersProvided_thenReturnAllStations() {
    // Arrange
    StationFilterDTO filters = new StationFilterDTO();
    when(stationRepository.findStationsByFilters(null, null, null, null, null, null, null, null, null))
        .thenReturn(Arrays.asList(station1, station2, station3));

    // Act
    List<Station> result = stationService.getStationsByFilters(filters);

    // Assert
    assertThat(result).hasSize(3);
    verify(stationRepository).findStationsByFilters(null, null, null, null, null, null, null, null, null);
  }

  @Test
  void whenFilterByStatus_thenReturnMatchingStations() {
    // Arrange
    StationFilterDTO filters = new StationFilterDTO();
    filters.setStatus("Available");
    when(stationRepository.findStationsByFilters(null, null, null, null, "Available", null, null, null, null))
        .thenReturn(List.of(station1));

    // Act
    List<Station> result = stationService.getStationsByFilters(filters);

    // Assert
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStatus()).isEqualTo("Available");
    verify(stationRepository).findStationsByFilters(null, null, null, null, "Available", null, null, null, null);
  }

  private Station createTestStation(Long id, String name) {
    Station station = new Station.Builder()
        .name(name)
        .address("Test Address")
        .city("Lisbon")
        .country("Portugal")
        .latitude(38.7223)
        .longitude(-9.1393)
        .connectorType("Type2")
        .status("Available")
        .isOperational(true)
        .build();
    station.setId(id);
    return station;
  }
}

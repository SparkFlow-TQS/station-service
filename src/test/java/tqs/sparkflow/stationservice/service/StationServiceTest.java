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

  @BeforeEach
  void setUp() {
    // Create test stations
    station1 = new Station();
    station1.setId(1L);
    station1.setConnectorType("Type 2");
    station1.setPower(50);
    station1.setPrice(0.30);
    station1.setIsOperational(true);
    station1.setStatus("Available");
    station1.setCity("Aveiro");
    station1.setCountry("Portugal");
    station1.setLatitude(40.623361);
    station1.setLongitude(-8.650256);

    station2 = new Station();
    station2.setId(2L);
    station2.setConnectorType("CCS");
    station2.setPower(150);
    station2.setPrice(0.35);
    station2.setIsOperational(true);
    station2.setStatus("In Use");
    station2.setCity("Porto");
    station2.setCountry("Portugal");
    station2.setLatitude(41.1579);
    station2.setLongitude(-8.6291);

    station3 = new Station();
    station3.setId(3L);
    station3.setConnectorType("Type 2");
    station3.setPower(22);
    station3.setPrice(0.25);
    station3.setIsOperational(false);
    station3.setStatus("Offline");
    station3.setCity("Lisbon");
    station3.setCountry("Portugal");
    station3.setLatitude(38.7223);
    station3.setLongitude(-9.1393);
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
    int radius = 601;

    // When/Then
    assertThatThrownBy(() -> stationService.getNearbyStations(centerLat, centerLon, radius))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Radius cannot be greater than 600 km");
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

  private Station createTestStation(Long id, String name) {
    Station station =
        new Station(name, "Test Address", "Lisbon", 38.7223, -9.1393, "Type 2", "Available");
    station.setId(id);
    return station;
  }
}

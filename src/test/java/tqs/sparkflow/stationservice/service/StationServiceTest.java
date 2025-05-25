package tqs.sparkflow.stationservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

  @Mock private StationRepository stationRepository;

  @InjectMocks private StationService stationService;

  @Test
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
  void whenGettingStationByIdWithNullId_thenThrowsException() {
    // When/Then
    assertThatThrownBy(() -> stationService.getStationById(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Station ID cannot be null");
  }

  @Test
  void whenGettingNearbyStations_thenReturnsStations() {
    // Given
    double latitude = 38.7223;
    double longitude = -9.1393;
    int radius = 10;
    List<Station> expectedStations =
        Arrays.asList(
            createTestStation(1L, "Nearby Station 1"), createTestStation(2L, "Nearby Station 2"));
    when(stationRepository.findAll()).thenReturn(expectedStations);

    // When
    List<Station> result = stationService.getNearbyStations(latitude, longitude, radius);

    // Then
    assertThat(result).isEqualTo(expectedStations);
    verify(stationRepository).findAll();
  }

  @Test
  void whenGettingNearbyStationsWithInvalidCoordinates_thenThrowsException() {
    // Given
    double invalidLatitude = 91.0;
    double longitude = -9.1393;
    int radius = 10;

    // When/Then
    assertThatThrownBy(() -> stationService.getNearbyStations(invalidLatitude, longitude, radius))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Latitude must be between -90 and 90 degrees");
  }

  @Test
  void whenGettingNearbyStationsWithInvalidRadius_thenThrowsException() {
    // Given
    double latitude = 38.7223;
    double longitude = -9.1393;
    int invalidRadius = 101;

    // When/Then
    assertThatThrownBy(() -> stationService.getNearbyStations(latitude, longitude, invalidRadius))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Radius cannot be greater than 100 km");
  }

  @Test
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
  void whenGettingStationsByEmptyConnectorType_thenThrowsException() {
    // When/Then
    assertThatThrownBy(() -> stationService.getStationsByConnectorType(""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Connector type cannot be empty");
  }

  @Test
  void whenGettingStationsByNullConnectorType_thenThrowsException() {
    // When/Then
    assertThatThrownBy(() -> stationService.getStationsByConnectorType(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Connector type cannot be null");
  }

  @Test
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
  void whenCreatingStationWithNullName_thenThrowsException() {
    // Given
    Station station = createTestStation(1L, null);

    // When/Then
    assertThatThrownBy(() -> stationService.createStation(station))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Station name cannot be empty");
  }

  @Test
  void whenCreatingStationWithEmptyName_thenThrowsException() {
    // Given
    Station station = createTestStation(1L, "");

    // When/Then
    assertThatThrownBy(() -> stationService.createStation(station))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Station name cannot be empty");
  }

  @Test
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
  void whenCreatingNullStation_thenThrowsException() {
    // When/Then
    assertThatThrownBy(() -> stationService.createStation(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Station cannot be null");
  }

  @Test
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
  void whenDeletingStationWithNullId_thenThrowsException() {
    // When/Then
    assertThatThrownBy(() -> stationService.deleteStation(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Station ID cannot be null");
  }

  private Station createTestStation(Long id, String name) {
    Station station =
        new Station(name, "Test Address", "Lisbon", 38.7223, -9.1393, "Type 2", "Available");
    station.setId(id);
    return station;
  }
}

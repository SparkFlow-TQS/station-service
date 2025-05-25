package tqs.sparkflow.stationservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.service.StationService;

@ExtendWith(MockitoExtension.class)
class StationControllerTest {

  @Mock private StationService stationService;

  @InjectMocks private StationController stationController;

  @Test
  void whenGettingAllStations_thenReturnsListOfStations() {
    // Given
    List<Station> expectedStations =
        Arrays.asList(createTestStation(1L, "Station 1"), createTestStation(2L, "Station 2"));
    when(stationService.getAllStations()).thenReturn(expectedStations);

    // When
    ResponseEntity<List<Station>> response = stationController.getAllStations();

    // Then
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isEqualTo(expectedStations);
    verify(stationService).getAllStations();
  }

  @Test
  void whenGettingStationById_thenReturnsStation() {
    // Given
    Long stationId = 1L;
    Station expectedStation = createTestStation(stationId, "Test Station");
    when(stationService.getStationById(stationId)).thenReturn(expectedStation);

    // When
    ResponseEntity<Station> response = stationController.getStationById(stationId);

    // Then
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isEqualTo(expectedStation);
    verify(stationService).getStationById(stationId);
  }

  @Test
  void whenGetNearbyStations_thenReturnStations() {
    // Given
    double latitude = 38.7223;
    double longitude = -9.1393;
    int radius = 10;
    List<Station> expectedStations =
        Arrays.asList(
            new Station(
                "Station 1", "Address 1", "Lisbon", latitude, longitude, "Type 2", "Available"),
            new Station(
                "Station 2",
                "Address 2",
                "Lisbon",
                latitude + 0.01,
                longitude + 0.01,
                "Type 2",
                "Available"));

    when(stationService.getNearbyStations(latitude, longitude, radius))
        .thenReturn(expectedStations);

    // When
    ResponseEntity<List<Station>> response =
        stationController.getNearbyStations(latitude, longitude, radius);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expectedStations);
    verify(stationService).getNearbyStations(latitude, longitude, radius);
  }

  @Test
  void whenGettingStationsByConnectorType_thenReturnsListOfStations() {
    // Given
    String connectorType = "Type2";
    List<Station> expectedStations =
        Arrays.asList(
            createTestStation(1L, "Type2 Station 1"), createTestStation(2L, "Type2 Station 2"));
    when(stationService.getStationsByConnectorType(connectorType)).thenReturn(expectedStations);

    // When
    ResponseEntity<List<Station>> response =
        stationController.getStationsByConnectorType(connectorType);

    // Then
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).isEqualTo(expectedStations);
    verify(stationService).getStationsByConnectorType(connectorType);
  }

  @Test
  void whenCreateStation_thenReturnCreatedStation() {
    // Given
    Station station =
        new Station(
            "Test Station", "Test Address", "Lisbon", 38.7223, -9.1393, "Type 2", "Available");
    station.setId(1L);

    when(stationService.createStation(any(Station.class))).thenReturn(station);

    // When
    ResponseEntity<Station> response = stationController.createStation(station);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isEqualTo(station);
    verify(stationService).createStation(station);
  }

  @Test
  void whenDeletingStation_thenCallsService() {
    // Given
    Long stationId = 1L;
    doNothing().when(stationService).deleteStation(stationId);

    // When
    stationController.deleteStation(stationId);

    // Then
    verify(stationService).deleteStation(stationId);
  }

  private Station createTestStation(Long id, String name) {
    Station station =
        new Station(name, "Test Address", "Lisbon", 38.7223, -9.1393, "Type2", "Available");
    station.setId(id);
    return station;
  }
}

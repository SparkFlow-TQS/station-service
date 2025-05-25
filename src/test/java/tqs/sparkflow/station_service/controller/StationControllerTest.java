package tqs.sparkflow.station_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tqs.sparkflow.station_service.model.Station;
import tqs.sparkflow.station_service.service.StationService;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StationControllerTest {

    @Mock
    private StationService stationService;

    private StationController stationController;

    @BeforeEach
    void setUp() {
        stationController = new StationController(stationService);
    }

    @Test
    void whenGettingAllStations_thenReturnsListOfStations() {
        // Given
        List<Station> expectedStations = Arrays.asList(
            createTestStation("1", "Station 1"),
            createTestStation("2", "Station 2")
        );
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
        String stationId = "1";
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
        List<Station> expectedStations = Arrays.asList(
            new Station("1", "Station 1", "Address 1", latitude, longitude, "Available", "Type 2"),
            new Station("2", "Station 2", "Address 2", latitude + 0.01, longitude + 0.01, "Available", "Type 2")
        );

        when(stationService.getNearbyStations(latitude, longitude, radius))
            .thenReturn(expectedStations);

        // When
        ResponseEntity<List<Station>> response = stationController.getNearbyStations(latitude, longitude, radius);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedStations);
        verify(stationService).getNearbyStations(latitude, longitude, radius);
    }

    @Test
    void whenGettingStationsByConnectorType_thenReturnsListOfStations() {
        // Given
        String connectorType = "Type2";
        List<Station> expectedStations = Arrays.asList(
            createTestStation("1", "Type2 Station 1"),
            createTestStation("2", "Type2 Station 2")
        );
        when(stationService.getStationsByConnectorType(connectorType))
            .thenReturn(expectedStations);

        // When
        ResponseEntity<List<Station>> response = stationController.getStationsByConnectorType(connectorType);

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(expectedStations);
        verify(stationService).getStationsByConnectorType(connectorType);
    }

    @Test
    void whenCreateStation_thenReturnCreatedStation() {
        // Given
        Station station = new Station();
        station.setId("1");
        station.setName("Test Station");
        station.setAddress("Test Address");
        station.setLatitude(38.7223);
        station.setLongitude(-9.1393);
        station.setStatus("Available");
        station.setConnectorType("Type 2");

        when(stationService.createStation(any(Station.class))).thenReturn(station);

        // When
        ResponseEntity<Station> response = stationController.createStation(station);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(station);
        verify(stationService).createStation(station);
    }

    @Test
    void whenDeletingStation_thenReturnsNoContent() {
        // Given
        String stationId = "1";
        doNothing().when(stationService).deleteStation(stationId);

        // When
        ResponseEntity<Void> response = stationController.deleteStation(stationId);

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNull();
        verify(stationService).deleteStation(stationId);
    }

    private Station createTestStation(String id, String name) {
        Station station = new Station();
        station.setId(id);
        station.setName(name);
        station.setAddress("Test Address");
        station.setLatitude(38.7223);
        station.setLongitude(-9.1393);
        station.setStatus("Available");
        station.setConnectorType("Type2");
        return station;
    }
} 
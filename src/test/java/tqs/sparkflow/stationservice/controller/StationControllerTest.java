package tqs.sparkflow.stationservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.service.StationService;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

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
    @XrayTest(key = "STATION-1")
    @Requirement("STATION-1")
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
    @XrayTest(key = "STATION-2")
    @Requirement("STATION-2")
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
    @XrayTest(key = "STATION-3")
    @Requirement("STATION-3")
    void whenGetNearbyStations_thenReturnStations() {
        // Given
        double latitude = 38.7223;
        double longitude = -9.1393;
        int radius = 10;
        List<Station> expectedStations =
            Arrays.asList(
                new Station(
                    "1234567890", "Station 1", "Address 1", "Lisbon", "Portugal", latitude, longitude, 1, "Available"),
                new Station(
                    "1234567891", "Station 2", "Address 2", "Lisbon", "Portugal", latitude + 0.01,
                    longitude + 0.01,
                    1,
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
    @XrayTest(key = "STATION-4")
    @Requirement("STATION-4")
    void whenGettingStationsByQuantityOfChargers_thenReturnsListOfStations() {
        // Given
        int quantityOfChargers = 1;
        List<Station> expectedStations =
            Arrays.asList(
                createTestStation(1L, "Type2 Station 1"), createTestStation(2L, "Type2 Station 2"));
        when(stationService.getStationsByMinChargers(quantityOfChargers)).thenReturn(expectedStations);

        // When
        ResponseEntity<List<Station>> response =
            stationController.getStationsByQuantityOfChargers(quantityOfChargers);

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(expectedStations);
        verify(stationService).getStationsByMinChargers(quantityOfChargers);
    }

    @Test
    @XrayTest(key = "STATION-5")
    @Requirement("STATION-5")
    void whenCreateStation_thenReturnCreatedStation() {
        // Given
        Station station =
            new Station(
                "1234567890", "Test Station", "Test Address", "Lisbon", "Portugal", 38.7223, -9.1393, 1, "Available");
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
    @XrayTest(key = "STATION-6")
    @Requirement("STATION-6")
    void whenDeletingStation_thenCallsService() {
        // Given
        Long stationId = 1L;
        doNothing().when(stationService).deleteStation(stationId);

        // When
        stationController.deleteStation(stationId);

        // Then
        verify(stationService).deleteStation(stationId);
    }

    @Test
    @XrayTest(key = "STATION-7")
    @Requirement("STATION-7")
    void whenGettingStationByIdNotFound_thenReturnsNotFound() {
        Long stationId = 99L;
        when(stationService.getStationById(stationId)).thenThrow(new IllegalArgumentException());
        ResponseEntity<Station> response = stationController.getStationById(stationId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(stationService).getStationById(stationId);
    }

    @Test
    @XrayTest(key = "STATION-8")
    @Requirement("STATION-8")
    void whenGettingStationByExternalId_thenReturnsStation() {
        String externalId = "ext-123";
        Station expectedStation = createTestStation(1L, "External Station");
        when(stationService.getStationByExternalId(externalId)).thenReturn(expectedStation);
        ResponseEntity<Station> response = stationController.getStationByExternalId(externalId);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(expectedStation);
        verify(stationService).getStationByExternalId(externalId);
    }

    @Test
    @XrayTest(key = "STATION-9")
    @Requirement("STATION-9")
    void whenGettingStationByExternalIdNotFound_thenReturnsNotFound() {
        String externalId = "not-found";
        when(stationService.getStationByExternalId(externalId)).thenThrow(new IllegalArgumentException());
        ResponseEntity<Station> response = stationController.getStationByExternalId(externalId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(stationService).getStationByExternalId(externalId);
    }

    @Test
    @XrayTest(key = "STATION-10")
    @Requirement("STATION-10")
    void whenUpdatingStation_thenReturnsUpdatedStation() {
        Long stationId = 1L;
        Station station = createTestStation(stationId, "Old Name");
        Station updatedStation = createTestStation(stationId, "New Name");
        when(stationService.updateStation(eq(stationId), any(Station.class))).thenReturn(updatedStation);
        ResponseEntity<Station> response = stationController.updateStation(stationId, station);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(updatedStation);
        verify(stationService).updateStation(eq(stationId), any(Station.class));
    }

    @Test
    @XrayTest(key = "STATION-11")
    @Requirement("STATION-11")
    void whenUpdatingStationNotFound_thenReturnsNotFound() {
        Long stationId = 99L;
        Station station = createTestStation(stationId, "Doesn't Matter");
        when(stationService.updateStation(eq(stationId), any(Station.class))).thenThrow(new IllegalArgumentException());
        ResponseEntity<Station> response = stationController.updateStation(stationId, station);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(stationService).updateStation(eq(stationId), any(Station.class));
    }

    @Test
    @XrayTest(key = "STATION-12")
    @Requirement("STATION-12")
    void whenDeletingStationNotFound_thenReturnsNotFound() {
        Long stationId = 99L;
        doThrow(new IllegalArgumentException()).when(stationService).deleteStation(stationId);
        ResponseEntity<Void> response = stationController.deleteStation(stationId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(stationService).deleteStation(stationId);
    }

    @Test
    @XrayTest(key = "STATION-13")
    @Requirement("STATION-13")
    void whenSearchingStations_thenReturnsList() {
        List<Station> expectedStations = Arrays.asList(createTestStation(1L, "Search 1"));
        when(stationService.searchStations("name", "city", "country", 1)).thenReturn(expectedStations);
        ResponseEntity<List<Station>> response = stationController.searchStations("name", "city", "country", 1);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(expectedStations);
        verify(stationService).searchStations("name", "city", "country", 1);
    }

    private Station createTestStation(Long id, String name) {
        Station station = new Station.Builder()
            .name(name)
            .address("Test Address")
            .city("Lisbon")
            .country("Portugal")
            .latitude(38.7223)
            .longitude(-9.1393)
            .quantityOfChargers(2)
            .power(22)
            .status("Available")
            .isOperational(true)
            .price(0.30)
            .build();
        station.setId(id);
        return station;
    }
}

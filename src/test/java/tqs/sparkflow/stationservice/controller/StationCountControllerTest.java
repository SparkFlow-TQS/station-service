package tqs.sparkflow.stationservice.controller;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.service.StationService;

/**
 * Test class for Station Controller endpoints related to 500-limit functionality.
 */
@ExtendWith(MockitoExtension.class)
class StationCountControllerTest {

    @Mock
    private StationService stationService;

    private StationController stationController;

    @BeforeEach
    void setUp() {
        stationController = new StationController(stationService);
    }

    @Test
    @XrayTest(key = "STATION-LIMIT-CTRL-1")
    @Requirement("STATION-LIMIT-CTRL-1")
    void whenGettingAllStations_thenReturnsMaximum500Stations() {
        // Given - Create exactly 500 stations (the limit)
        List<Station> limitedStations = createStationList(500);
        when(stationService.getAllStations()).thenReturn(limitedStations);

        // When
        ResponseEntity<List<Station>> response = stationController.getAllStations();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(500);
        verify(stationService).getAllStations();
    }

    @Test
    @XrayTest(key = "STATION-SEARCH-CTRL-1")
    @Requirement("STATION-SEARCH-CTRL-1")
    void whenSearchingStations_thenReturnsMaximum500Results() {
        // Given
        List<Station> limitedStations = createStationList(500);
        when(stationService.searchStations("test", null, null, null)).thenReturn(limitedStations);

        // When
        ResponseEntity<List<Station>> response =
                stationController.searchStations("test", null, null, null);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(500);
        verify(stationService).searchStations("test", null, null, null);
    }

    @Test
    @XrayTest(key = "STATION-NEARBY-CTRL-1")
    @Requirement("STATION-NEARBY-CTRL-1")
    void whenGettingNearbyStations_thenReturnsMaximum500Results() {
        // Given
        double latitude = 38.7223;
        double longitude = -9.1393;
        int radius = 25;
        List<Station> limitedStations = createStationList(500);
        when(stationService.getNearbyStations(latitude, longitude, radius))
                .thenReturn(limitedStations);

        // When
        ResponseEntity<List<Station>> response =
                stationController.getNearbyStations(latitude, longitude, radius);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(500);
        verify(stationService).getNearbyStations(latitude, longitude, radius);
    }

    private List<Station> createStationList(int count) {
        List<Station> stations = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Station station = new Station("ext-" + i, // externalId
                    "Station " + i, // name
                    "Address " + i, // address
                    "City", // city
                    "Country", // country
                    38.7223 + (i * 0.001), // latitude
                    -9.1393 + (i * 0.001), // longitude
                    2, // quantityOfChargers
                    "Available" // status
            );
            station.setId((long) i);
            stations.add(station);
        }
        return stations;
    }
}

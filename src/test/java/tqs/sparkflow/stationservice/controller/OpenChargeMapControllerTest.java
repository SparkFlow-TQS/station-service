package tqs.sparkflow.stationservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.sparkflow.stationservice.service.OpenChargeMapService;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Collections;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

@ExtendWith(MockitoExtension.class)
class OpenChargeMapControllerTest {
    @Mock
    private OpenChargeMapService openChargeMapService;

    private OpenChargeMapController openChargeMapController;

    @BeforeEach
    void setUp() {
        openChargeMapController = new OpenChargeMapController(openChargeMapService);
    }

    @Test
    @XrayTest(key = "OPEN-CHARGE-MAP-1")
    @Requirement("OPEN-CHARGE-MAP-1")
    void testPopulateStationsEndpoint() {
        when(openChargeMapService.populateStations(40.0, -8.0, 10))
                .thenReturn(Collections.emptyList());

        var response = openChargeMapController.populateStations(40.0, -8.0, 10);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo("Stations populated successfully");
    }

    @Test
    void testPopulateStationsEndpoint_withInvalidLatitude() {
        when(openChargeMapService.populateStations(100.0, -8.0, 10)).thenThrow(
                new IllegalArgumentException("Latitude must be between -90 and 90 degrees"));

        var response = openChargeMapController.populateStations(100.0, -8.0, 10);
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody()).isEqualTo("Latitude must be between -90 and 90 degrees");
    }

    @Test
    void testPopulateStationsEndpoint_withInvalidLongitude() {
        when(openChargeMapService.populateStations(40.0, -200.0, 10)).thenThrow(
                new IllegalArgumentException("Longitude must be between -180 and 180 degrees"));

        var response = openChargeMapController.populateStations(40.0, -200.0, 10);
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody()).isEqualTo("Longitude must be between -180 and 180 degrees");
    }

    @Test
    void testPopulateStationsEndpoint_withInvalidRadius() {
        when(openChargeMapService.populateStations(40.0, -8.0, -1))
                .thenThrow(new IllegalArgumentException("Radius must be positive"));

        var response = openChargeMapController.populateStations(40.0, -8.0, -1);
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody()).isEqualTo("Radius must be positive");
    }

    @Test
    void testPopulateStationsEndpoint_withApiError() {
        when(openChargeMapService.populateStations(40.0, -8.0, 10))
                .thenThrow(new IllegalStateException("Invalid Open Charge Map API key"));

        var response = openChargeMapController.populateStations(40.0, -8.0, 10);
        assertThat(response.getStatusCode().is5xxServerError()).isTrue();
        assertThat(response.getBody()).isEqualTo("Invalid Open Charge Map API key");
    }

    @Test
    void testPopulateStationsEndpoint_withNoStationsFound() {
        when(openChargeMapService.populateStations(40.0, -8.0, 10))
                .thenThrow(new IllegalStateException("No stations found"));

        var response = openChargeMapController.populateStations(40.0, -8.0, 10);
        assertThat(response.getStatusCode().is5xxServerError()).isTrue();
        assertThat(response.getBody()).isEqualTo("No stations found");
    }
}

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
    }
} 
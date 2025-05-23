package tqs.sparkflow.station_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import tqs.sparkflow.station_service.repository.StationRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = {
    "openchargemap.api.key=invalid-test-key"
})
class OpenChargeMapServiceInvalidKeyIT {

    @Autowired
    private OpenChargeMapService openChargeMapService;

    @Autowired
    private StationRepository stationRepository;

    @BeforeEach
    void setUp() {
        stationRepository.deleteAll();
    }

    @Test
    void whenPopulatingStationsWithInvalidApiKey_thenThrowsException() {
        // Given
        double latitude = 38.7223;
        double longitude = -9.1393;
        int radius = 10;

        // When/Then
        assertThatThrownBy(() -> openChargeMapService.populateStations(latitude, longitude, radius))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Access denied to Open Charge Map API");
    }
} 
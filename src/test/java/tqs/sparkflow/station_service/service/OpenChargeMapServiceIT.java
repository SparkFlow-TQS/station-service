package tqs.sparkflow.station_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import tqs.sparkflow.station_service.repository.StationRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = {
    "openchargemap.api.key=test-key"
})
class OpenChargeMapServiceIT {

    @Autowired
    private OpenChargeMapService openChargeMapService;

    @Autowired
    private StationRepository stationRepository;

    @BeforeEach
    void setUp() {
        stationRepository.deleteAll();
    }

    @Test
    void whenPopulatingStationsWithValidCoordinates_thenStationsAreCreated() {
        // Given
        double latitude = 38.7223;
        double longitude = -9.1393;
        int radius = 10;

        // When
        String result = openChargeMapService.populateStations(latitude, longitude, radius);

        // Then
        assertThat(result).contains("Successfully populated");
        assertThat(stationRepository.findAll()).isNotEmpty();
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
            .hasMessageContaining("Invalid Open Charge Map API key");
    }

    @Test
    void whenPopulatingStationsWithInvalidCoordinates_thenThrowsException() {
        // Given
        double invalidLatitude = 91.0;
        double longitude = -9.1393;
        int radius = 10;

        // When/Then
        assertThatThrownBy(() -> openChargeMapService.populateStations(invalidLatitude, longitude, radius))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void whenPopulatingStationsWithInvalidRadius_thenThrowsException() {
        // Given
        double latitude = 38.7223;
        double longitude = -9.1393;
        int invalidRadius = 101;

        // When/Then
        assertThatThrownBy(() -> openChargeMapService.populateStations(latitude, longitude, invalidRadius))
            .isInstanceOf(IllegalStateException.class);
    }
} 
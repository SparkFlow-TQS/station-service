package tqs.sparkflow.station_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.sparkflow.station_service.model.Station;
import tqs.sparkflow.station_service.repository.StationRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(stationRepository);
    }

    @Test
    void whenGettingStationsByConnectorType_thenReturnsMatchingStations() {
        // Given
        String connectorType = "Type2";
        Station station1 = new Station();
        station1.setId("1");
        station1.setConnectorType(connectorType);
        
        Station station2 = new Station();
        station2.setId("2");
        station2.setConnectorType("CCS");
        
        Station station3 = new Station();
        station3.setId("3");
        station3.setConnectorType(connectorType);

        when(stationRepository.findByConnectorType(connectorType))
            .thenReturn(Arrays.asList(station1, station3));

        // When
        List<Station> result = stationService.getStationsByConnectorType(connectorType);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Station::getConnectorType)
            .allMatch(type -> type.equals(connectorType));
    }

    @Test
    void whenCreatingValidStation_thenStationIsSaved() {
        // Given
        Station station = new Station();
        station.setId("1");
        station.setName("Test Station");
        station.setLatitude("38.7223");
        station.setLongitude("-9.1393");
        station.setConnectorType("Type2");
        
        when(stationRepository.save(station)).thenReturn(station);

        // When
        Station result = stationService.createStation(station);

        // Then
        assertThat(result).isEqualTo(station);
        verify(stationRepository).save(station);
    }

    @Test
    void whenCreatingStationWithInvalidCoordinates_thenThrowsException() {
        // Given
        Station station = new Station();
        station.setName("Test Station");
        station.setLatitude("invalid");
        station.setLongitude("-9.1393");
        station.setConnectorType("Type2");

        // When/Then
        assertThatThrownBy(() -> stationService.createStation(station))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid coordinate format");
    }

    @Test
    void whenCreatingStationWithEmptyName_thenThrowsException() {
        // Given
        Station station = new Station();
        station.setName("");
        station.setLatitude("38.7223");
        station.setLongitude("-9.1393");
        station.setConnectorType("Type2");

        // When/Then
        assertThatThrownBy(() -> stationService.createStation(station))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Station name cannot be empty");
    }

    @Test
    void whenCreatingStationWithEmptyConnectorType_thenThrowsException() {
        // Given
        Station station = new Station();
        station.setName("Test Station");
        station.setLatitude("38.7223");
        station.setLongitude("-9.1393");
        station.setConnectorType("");

        // When/Then
        assertThatThrownBy(() -> stationService.createStation(station))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Connector type cannot be empty");
    }

    @Test
    void whenDeletingExistingStation_thenStationIsDeleted() {
        // Given
        String stationId = "1";
        when(stationRepository.existsById(stationId)).thenReturn(true);

        // When
        stationService.deleteStation(stationId);

        // Then
        verify(stationRepository).deleteById(stationId);
    }

    @Test
    void whenDeletingNonExistentStation_thenThrowsException() {
        // Given
        String stationId = "1";
        when(stationRepository.existsById(stationId)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> stationService.deleteStation(stationId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Station not found with id: " + stationId);
    }
} 
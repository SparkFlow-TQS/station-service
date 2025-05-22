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
import static org.mockito.Mockito.when;

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
} 
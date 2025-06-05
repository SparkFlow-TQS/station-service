package tqs.sparkflow.stationservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tqs.sparkflow.stationservice.dto.RoutePlanningRequestDTO;
import tqs.sparkflow.stationservice.dto.RoutePlanningResponseDTO;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;

class RoutePlanningServiceTest {

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private RoutePlanningServiceImpl routePlanningService;

    private List<Station> testStations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test stations
        Station portoStation = new Station();
        portoStation.setId(1L);
        portoStation.setName("Porto Central Station");
        portoStation.setLatitude(41.1579);
        portoStation.setLongitude(-8.6291);
        portoStation.setConnectorType("Type 2");
        portoStation.setStatus("Available");

        Station aveiroStation = new Station();
        aveiroStation.setId(2L);
        aveiroStation.setName("Aveiro Charging Hub");
        aveiroStation.setLatitude(40.6443);
        aveiroStation.setLongitude(-8.6455);
        aveiroStation.setConnectorType("CCS");
        aveiroStation.setStatus("Available");

        Station coimbraStation = new Station();
        coimbraStation.setId(3L);
        coimbraStation.setName("Coimbra Supercharger");
        coimbraStation.setLatitude(40.2033);
        coimbraStation.setLongitude(-8.4103);
        coimbraStation.setConnectorType("Tesla");
        coimbraStation.setStatus("Available");

        Station leiriaStation = new Station();
        leiriaStation.setId(4L);
        leiriaStation.setName("Leiria Fast Charge");
        leiriaStation.setLatitude(39.7477);
        leiriaStation.setLongitude(-8.8070);
        leiriaStation.setConnectorType("Type 2");
        leiriaStation.setStatus("Available");

        Station lisbonStation = new Station();
        lisbonStation.setId(5L);
        lisbonStation.setName("Lisbon Central Station");
        lisbonStation.setLatitude(38.7223);
        lisbonStation.setLongitude(-9.1393);
        lisbonStation.setConnectorType("CCS");
        lisbonStation.setStatus("Available");

        testStations = List.of(portoStation, aveiroStation, coimbraStation, leiriaStation, lisbonStation);
        when(stationRepository.findAll()).thenReturn(testStations);
    }

    @Test
    void testPlanRouteFromPortoToLisbon() {
        // Create route planning request
        RoutePlanningRequestDTO request = new RoutePlanningRequestDTO();
        request.setStartLatitude(41.1579); // Porto
        request.setStartLongitude(-8.6291);
        request.setDestLatitude(38.7223); // Lisbon
        request.setDestLongitude(-9.1393);
        request.setBatteryCapacity(40.0); // Reduced from 60.0 to 40.0 kWh
        request.setCarAutonomy(5.0); // 5 km/kWh

        // Plan route
        RoutePlanningResponseDTO response = routePlanningService.planRoute(request);

        // Verify response
        assertNotNull(response);
        assertNotNull(response.getStations());
        assertTrue(response.getStations().size() > 0);
        assertTrue(response.getDistance() > 0);
        assertTrue(response.getBatteryUsage() > 0);

        // Print route details
        System.out.println("Route from Porto to Lisbon:");
        System.out.println("Total distance: " + response.getDistance() + " km");
        System.out.println("Estimated battery usage: " + response.getBatteryUsage() + " kWh");
        System.out.println("Charging stations along the route:");
        response.getStations().forEach(station -> 
            System.out.println("- " + station.getName() + " (" + station.getCity() + ")")
        );
    }
} 
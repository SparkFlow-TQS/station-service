package tqs.sparkflow.stationservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.sparkflow.stationservice.dto.RoutePlanningRequestDTO;
import tqs.sparkflow.stationservice.dto.RoutePlanningResponseDTO;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
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
        portoStation.setQuantityOfChargers(2);
        portoStation.setPower(50);
        portoStation.setStatus("Available");
        portoStation.setCity("Porto");
        portoStation.setIsOperational(true);
        portoStation.setPrice(0.30);

        Station aveiroStation = new Station();
        aveiroStation.setId(2L);
        aveiroStation.setName("Aveiro Fast Charge");
        aveiroStation.setLatitude(40.623361);
        aveiroStation.setLongitude(-8.650256);
        aveiroStation.setQuantityOfChargers(3);
        aveiroStation.setPower(150);
        aveiroStation.setStatus("Available");
        aveiroStation.setCity("Aveiro");
        aveiroStation.setIsOperational(true);
        aveiroStation.setPrice(0.35);

        Station coimbraStation = new Station();
        coimbraStation.setId(3L);
        coimbraStation.setName("Coimbra Station");
        coimbraStation.setLatitude(40.2033);
        coimbraStation.setLongitude(-8.4103);
        coimbraStation.setQuantityOfChargers(2);
        coimbraStation.setPower(50);
        coimbraStation.setStatus("Available");
        coimbraStation.setCity("Coimbra");
        coimbraStation.setIsOperational(true);
        coimbraStation.setPrice(0.30);

        Station leiriaStation = new Station();
        leiriaStation.setId(4L);
        leiriaStation.setName("Leiria Fast Charge");
        leiriaStation.setLatitude(39.7477);
        leiriaStation.setLongitude(-8.8077);
        leiriaStation.setQuantityOfChargers(3);
        leiriaStation.setPower(150);
        leiriaStation.setStatus("Available");
        leiriaStation.setCity("Leiria");
        leiriaStation.setIsOperational(true);
        leiriaStation.setPrice(0.35);

        Station lisbonStation = new Station();
        lisbonStation.setId(5L);
        lisbonStation.setName("Lisbon Central Station");
        lisbonStation.setLatitude(38.7223);
        lisbonStation.setLongitude(-9.1393);
        lisbonStation.setQuantityOfChargers(2);
        lisbonStation.setPower(50);
        lisbonStation.setStatus("Available");
        lisbonStation.setCity("Lisbon");
        lisbonStation.setIsOperational(true);
        lisbonStation.setPrice(0.30);

        testStations = Arrays.asList(portoStation, aveiroStation, coimbraStation, leiriaStation, lisbonStation);
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

    @Test
    void whenFindingRoute_thenReturnsOptimalRoute() {
        // Given
        RoutePlanningRequestDTO request = new RoutePlanningRequestDTO();
        request.setStartLatitude(41.1579); // Porto
        request.setStartLongitude(-8.6291);
        request.setDestLatitude(38.7223); // Lisbon
        request.setDestLongitude(-9.1393);
        request.setBatteryCapacity(40.0); // 40 kWh battery
        request.setCarAutonomy(5.0); // 5 km/kWh

        // When
        RoutePlanningResponseDTO response = routePlanningService.planRoute(request);

        // Then
        assertThat(response.getStations()).isNotEmpty();
        assertThat(response.getStations().size()).isEqualTo(1); // Only one station needed
        assertThat(response.getStations().get(0).getName()).isEqualTo("Leiria Fast Charge");
        assertThat(response.getDistance()).isGreaterThan(0);
        assertThat(response.getBatteryUsage()).isGreaterThan(0);
    }
} 
package tqs.sparkflow.stationservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import tqs.sparkflow.stationservice.dto.RoutePlanningRequestDTO;
import tqs.sparkflow.stationservice.dto.RoutePlanningResponseDTO;
import tqs.sparkflow.stationservice.service.RoutePlanningService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import java.util.ArrayList;

class RoutePlanningControllerTest {

    @Mock
    private RoutePlanningService routePlanningService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private RoutePlanningController routePlanningController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenValidRequest_thenReturnOk() {
        // Arrange
        RoutePlanningRequestDTO request = new RoutePlanningRequestDTO();
        request.setStartLatitude(40.7128);
        request.setStartLongitude(-8.0);
        request.setDestLatitude(40.7128);
        request.setDestLongitude(-9.0);
        request.setBatteryCapacity(55.0);
        request.setCarAutonomy(20.0);

        RoutePlanningResponseDTO response =
                new RoutePlanningResponseDTO(new ArrayList<>(), 100.0, 5.0);
        when(routePlanningService.planRoute(request)).thenReturn(response);
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        ResponseEntity<?> result = routePlanningController.planRoute(request, bindingResult);

        // Assert
        assertEquals(200, result.getStatusCode().value());
        verify(routePlanningService).planRoute(request);
    }

    @Test
    void whenInvalidRequest_thenReturnBadRequest() {
        // Arrange
        RoutePlanningRequestDTO request = new RoutePlanningRequestDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        ResponseEntity<?> result = routePlanningController.planRoute(request, bindingResult);

        // Assert
        assertEquals(400, result.getStatusCode().value());
        verify(routePlanningService, never()).planRoute(any());
    }

    @Test
    void whenServiceThrowsException_thenReturnBadRequest() {
        // Arrange
        RoutePlanningRequestDTO request = new RoutePlanningRequestDTO();
        request.setStartLatitude(40.7128);
        request.setStartLongitude(-8.0);
        request.setDestLatitude(40.7128);
        request.setDestLongitude(-9.0);
        request.setBatteryCapacity(55.0);
        request.setCarAutonomy(20.0);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(routePlanningService.planRoute(request))
                .thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<?> result = routePlanningController.planRoute(request, bindingResult);

        // Assert
        assertEquals(400, result.getStatusCode().value());
    }
}

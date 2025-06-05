package tqs.sparkflow.stationservice.service;

import tqs.sparkflow.stationservice.dto.RoutePlanningRequestDTO;
import tqs.sparkflow.stationservice.dto.RoutePlanningResponseDTO;
 
public interface RoutePlanningService {
    RoutePlanningResponseDTO planRoute(RoutePlanningRequestDTO request);
} 
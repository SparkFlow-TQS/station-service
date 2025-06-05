package tqs.sparkflow.stationservice.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.sparkflow.stationservice.dto.RoutePlanningRequestDTO;
import tqs.sparkflow.stationservice.dto.RoutePlanningResponseDTO;
import tqs.sparkflow.stationservice.dto.StationDTO;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;

@Service
public class RoutePlanningServiceImpl implements RoutePlanningService {

    private static final double MIN_BATTERY_PERCENTAGE = 0.2; // 20% minimum battery level
    private static final double MAX_BATTERY_PERCENTAGE = 0.8; // 80% maximum battery level for charging
    private static final double MAX_DETOUR_DISTANCE = 20.0; // Maximum 20km detour from route

    @Autowired
    private StationRepository stationRepository;

    @Override
    public RoutePlanningResponseDTO planRoute(RoutePlanningRequestDTO request) {
        // Calculate direct distance between points (in km)
        double distance = calculateDistance(
            request.getStartLatitude(), request.getStartLongitude(),
            request.getDestLatitude(), request.getDestLongitude()
        );

        // Calculate estimated battery usage (kWh)
        double batteryUsage = distance / request.getCarAutonomy();
        
        // If the route can be completed without charging, return empty list
        if (batteryUsage <= request.getBatteryCapacity() * (1 - MIN_BATTERY_PERCENTAGE)) {
            return new RoutePlanningResponseDTO(new ArrayList<>(), distance, batteryUsage);
        }

        // Find all stations
        List<Station> allStations = stationRepository.findAll();

        // Find optimal charging stations
        List<Station> optimalStations = findOptimalChargingStations(allStations, request, distance);

        return new RoutePlanningResponseDTO(optimalStations, distance, batteryUsage);
    }

    private List<Station> findOptimalChargingStations(List<Station> stations, RoutePlanningRequestDTO request, double totalDistance) {
        List<Station> optimalStations = new ArrayList<>();
        double currentBattery = request.getBatteryCapacity(); // Start with full battery
        double distanceTraveled = 0;
        double remainingDistance = totalDistance;

        while (remainingDistance > 0 && currentBattery > MIN_BATTERY_PERCENTAGE * request.getBatteryCapacity()) {
            // Calculate how far we can go with current battery
            double maxDistanceWithBattery = currentBattery * request.getCarAutonomy();
            
            // If we can reach destination with current battery, break
            if (maxDistanceWithBattery >= remainingDistance) {
                break;
            }

            // Find the best station to stop at
            Station bestStation = findBestChargingStation(
                stations,
                request.getStartLatitude() + (distanceTraveled / totalDistance) * (request.getDestLatitude() - request.getStartLatitude()),
                request.getStartLongitude() + (distanceTraveled / totalDistance) * (request.getDestLongitude() - request.getStartLongitude()),
                maxDistanceWithBattery,
                remainingDistance,
                request.getDestLatitude(),
                request.getDestLongitude()
            );

            if (bestStation == null) {
                break; // No suitable station found
            }

            // Calculate distance to station
            double distanceToStation = calculateDistance(
                request.getStartLatitude() + (distanceTraveled / totalDistance) * (request.getDestLatitude() - request.getStartLatitude()),
                request.getStartLongitude() + (distanceTraveled / totalDistance) * (request.getDestLongitude() - request.getStartLongitude()),
                bestStation.getLatitude(),
                bestStation.getLongitude()
            );

            // Update battery and distance
            currentBattery -= distanceToStation / request.getCarAutonomy();
            distanceTraveled += distanceToStation;
            remainingDistance -= distanceToStation;

            // Add station to route
            optimalStations.add(bestStation);
            
            // Reset battery to 80% after charging
            currentBattery = MAX_BATTERY_PERCENTAGE * request.getBatteryCapacity();
        }

        return optimalStations;
    }

    private Station findBestChargingStation(List<Station> stations, double currentLat, double currentLon, 
                                          double maxDistanceWithBattery, double remainingDistance,
                                          double destLat, double destLon) {
        Station bestStation = null;
        double minScore = Double.MAX_VALUE;

        for (Station station : stations) {
            double distanceToStation = calculateDistance(currentLat, currentLon, station.getLatitude(), station.getLongitude());
            double distanceToDestination = calculateDistance(station.getLatitude(), station.getLongitude(), destLat, destLon);
            
            // Check if station is within range and doesn't deviate too much from route
            if (distanceToStation <= maxDistanceWithBattery) {
                // Calculate detour score (lower is better)
                double directDistance = calculateDistance(currentLat, currentLon, destLat, destLon);
                double detourDistance = distanceToStation + distanceToDestination;
                double detourScore = detourDistance - directDistance;
                
                // Only consider stations that don't require too much detour
                if (detourScore <= MAX_DETOUR_DISTANCE) {
                    // Calculate final score based on detour and distance to destination
                    double score = detourScore + (distanceToDestination / 100); // Penalize stations far from destination
                    
                    if (score < minScore) {
                        minScore = score;
                        bestStation = station;
                    }
                }
            }
        }

        return bestStation;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
} 
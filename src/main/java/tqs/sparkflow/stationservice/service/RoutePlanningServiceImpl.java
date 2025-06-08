package tqs.sparkflow.stationservice.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.google.common.util.concurrent.RateLimiter;
import tqs.sparkflow.stationservice.dto.RoutePlanningRequestDTO;
import tqs.sparkflow.stationservice.dto.RoutePlanningResponseDTO;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;
import tqs.sparkflow.stationservice.config.RoutePlanningConfig;

@Service
public class RoutePlanningServiceImpl implements RoutePlanningService {

  private final StationRepository stationRepository;
  private final RoutePlanningConfig config;
  private final RateLimiter rateLimiter;

  @Autowired
  public RoutePlanningServiceImpl(StationRepository stationRepository, RoutePlanningConfig config,
      RateLimiter routePlanningRateLimiter) {
    this.stationRepository = stationRepository;
    this.config = config;
    this.rateLimiter = routePlanningRateLimiter;
  }

  @Override
  public RoutePlanningResponseDTO planRoute(RoutePlanningRequestDTO request) {
    // Check rate limit
    if (!rateLimiter.tryAcquire()) {
      throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
    }

    // Validate coordinates
    validateCoordinates(request.getStartLatitude(), request.getStartLongitude(), "start");
    validateCoordinates(request.getDestLatitude(), request.getDestLongitude(), "destination");

    // Validate battery capacity and autonomy
    if (request.getBatteryCapacity() <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Battery capacity must be greater than 0");
    }
    if (request.getCarAutonomy() <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Car autonomy must be greater than 0");
    }

    // Get all stations and filter for available ones
    List<Station> allStations = stationRepository.findAll();
    List<Station> availableStations = allStations.stream()
        .filter(station -> station.getIsOperational() && "Available".equals(station.getStatus()))
        .toList();

    if (availableStations.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
          "No charging stations available in the system");
    }

    // Calculate total route distance
    double totalDistance = calculateDistance(request.getStartLatitude(),
        request.getStartLongitude(), request.getDestLatitude(), request.getDestLongitude());

    // Calculate if direct route is possible
    double batteryNeeded = totalDistance / request.getCarAutonomy();
    double batteryPercentage = batteryNeeded / request.getBatteryCapacity();

    if (batteryPercentage <= config.getMaxBatteryPercentage()) {
      // Direct route is possible
      RoutePlanningResponseDTO response = new RoutePlanningResponseDTO();
      response.setStations(new ArrayList<>());
      response.setDistance(totalDistance);
      response.setBatteryUsage(batteryNeeded);
      return response;
    }

    // Find optimal charging stations
    List<Station> optimalStations = findOptimalChargingStations(availableStations,
        request.getStartLatitude(), request.getStartLongitude(), request.getDestLatitude(),
        request.getDestLongitude(), request.getBatteryCapacity(), request.getCarAutonomy());

    // Create response
    RoutePlanningResponseDTO response = new RoutePlanningResponseDTO();
    response.setStations(optimalStations);
    response.setDistance(totalDistance);
    response.setBatteryUsage(batteryNeeded);
    return response;
  }

  private void validateCoordinates(double latitude, double longitude, String point) {
    if (latitude < -90 || latitude > 90) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + point + " latitude");
    }
    if (longitude < -180 || longitude > 180) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + point + " longitude");
    }
  }

  private double calculateDistance(double startLat, double startLon, double destLat,
      double destLon) {
    // Haversine formula implementation
    final int R = 6371; // Earth's radius in kilometers

    double latDistance = Math.toRadians(destLat - startLat);
    double lonDistance = Math.toRadians(destLon - startLon);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(destLat))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  }

  private List<Station> findOptimalChargingStations(List<Station> stations, double startLat,
      double startLon, double destLat, double destLon, double batteryCapacity, double carAutonomy) {

    // Calculate direct route distance
    double directDistance = calculateDistance(startLat, startLon, destLat, destLon);

    // Filter stations within max detour distance and not too far from the route
    List<Station> candidateStations = stations.stream().filter(station -> {
      double detourDistance = calculateDetourDistance(startLat, startLon, destLat, destLon,
          station.getLatitude(), station.getLongitude());
      double totalDistance =
          calculateDistance(startLat, startLon, station.getLatitude(), station.getLongitude())
              + calculateDistance(station.getLatitude(), station.getLongitude(), destLat, destLon);
      double detourRatio = totalDistance / directDistance;

      // Station must be within max detour distance and not require a detour more than 50% longer
      // than direct route
      return detourDistance <= config.getMaxDetourDistance() && detourRatio <= 1.5;
    }).toList();

    // If no stations are within the acceptable detour distance, throw an exception
    if (candidateStations.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "No suitable charging stations found for the given route");
    }

    // Score and sort stations by optimality
    List<Station> optimalStations = candidateStations.stream()
        .map(station -> new Object[] {station,
            calculateStationScore(station, startLat, startLon, destLat, destLon, batteryCapacity,
                carAutonomy)})
        .filter(arr -> (double) arr[1] < Double.MAX_VALUE)
        .sorted(Comparator.comparingDouble(arr -> (double) arr[1])).limit(3)
        .map(arr -> (Station) arr[0]).toList();

    // If no stations are suitable after scoring, throw an exception
    if (optimalStations.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "No suitable charging stations found for the given route");
    }

    return optimalStations;
  }

  private double calculateStationScore(Station station, double startLat, double startLon,
      double destLat, double destLon, double batteryCapacity, double carAutonomy) {

    // Calculate distances
    double distanceToStart =
        calculateDistance(startLat, startLon, station.getLatitude(), station.getLongitude());
    double distanceToDest =
        calculateDistance(station.getLatitude(), station.getLongitude(), destLat, destLon);
    double totalDistance = distanceToStart + distanceToDest;

    // Calculate battery usage
    double batteryToStation = distanceToStart / carAutonomy;

    // Calculate optimality score (lower is better)
    double score = 0;

    // Prefer stations that are closer to the midpoint of the route
    double directDistance = calculateDistance(startLat, startLon, destLat, destLon);
    double detourRatio = totalDistance / directDistance;

    // Heavily penalize stations that are too far from the route
    if (detourRatio > 1.5 || totalDistance > directDistance * 2.5) { // If detour is more than 50%
                                                                     // longer than direct route or
                                                                     // total distance is more than
                                                                     // 2.5x
      return Double.MAX_VALUE; // Effectively exclude this station
    }

    // Calculate battery at station
    double batteryAtStation = batteryCapacity - batteryToStation;

    // Exclude stations that would leave battery too low or too high
    if (batteryAtStation < config.getMinBatteryPercentage() * batteryCapacity) {
      return Double.MAX_VALUE;
    }
    if (batteryAtStation > config.getMaxBatteryPercentage() * batteryCapacity) {
      return Double.MAX_VALUE;
    }

    // Score based on detour ratio (lower is better)
    score += detourRatio * 1000; // Increased weight for detour distance

    // Prefer stations with higher power (faster charging)
    score -= station.getPower() * 0.1;

    // Prefer stations with more available chargers
    score -= station.getQuantityOfChargers() * 10;

    return score;
  }

  double calculateDetourDistance(double startLat, double startLon, double destLat, double destLon,
      double stationLat, double stationLon) {
    // Calculate direct distance between start and destination
    double directDistance = calculateDistance(startLat, startLon, destLat, destLon);

    // Calculate distance from start to station
    double startToStation = calculateDistance(startLat, startLon, stationLat, stationLon);

    // Calculate distance from station to destination
    double stationToDest = calculateDistance(stationLat, stationLon, destLat, destLon);

    // Calculate total distance via station
    double viaStationDistance = startToStation + stationToDest;

    // If the station is too far from the route, return a large value to exclude it
    if (viaStationDistance > directDistance * 1.5) { // If detour is more than 50% longer than
                                                     // direct route
      return Double.MAX_VALUE;
    }

    // Return the additional distance required to go via the station
    return viaStationDistance - directDistance;
  }
}

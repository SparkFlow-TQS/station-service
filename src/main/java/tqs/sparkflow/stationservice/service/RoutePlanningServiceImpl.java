package tqs.sparkflow.stationservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

import java.util.stream.Collectors;

@Service
public class RoutePlanningServiceImpl implements RoutePlanningService {

  private static final double MIN_BATTERY_PERCENTAGE = 0.2; // 20% minimum battery level
  private static final double MAX_BATTERY_PERCENTAGE = 0.8; // 80% maximum battery level for
                                                            // charging
  private static final double MAX_DETOUR_DISTANCE = 20.0; // Maximum 20km detour from route
  private static final double MIN_LATITUDE = -90.0;
  private static final double MAX_LATITUDE = 90.0;
  private static final double MIN_LONGITUDE = -180.0;
  private static final double MAX_LONGITUDE = 180.0;
  private static final double REQUESTS_PER_SECOND = 10.0; // Rate limit: 10 requests per second

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
        .collect(Collectors.toList());

    if (availableStations.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
          "No charging stations available in the system");
    }

    // Calculate route and find optimal charging stations
    double distance = calculateDistance(request.getStartLatitude(), request.getStartLongitude(),
        request.getDestLatitude(), request.getDestLongitude());

    double batteryUsage = distance / request.getCarAutonomy();
    double batteryPercentage = batteryUsage / request.getBatteryCapacity();

    if (batteryPercentage > config.getMaxBatteryPercentage()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "No suitable charging stations found for the given route");
    }

    // Find optimal charging stations
    List<Station> optimalStations = findOptimalChargingStations(availableStations,
        request.getStartLatitude(), request.getStartLongitude(), request.getDestLatitude(),
        request.getDestLongitude(), request.getBatteryCapacity(), request.getCarAutonomy());

    if (optimalStations.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "No suitable charging stations found for the given route");
    }

    // Create response
    RoutePlanningResponseDTO response = new RoutePlanningResponseDTO();
    response.setStations(optimalStations);
    response.setDistance(distance);
    response.setBatteryUsage(batteryUsage);
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
    // Filter stations within max detour distance
    return stations.stream().filter(station -> {
      double detourDistance = calculateDetourDistance(startLat, startLon, destLat, destLon,
          station.getLatitude(), station.getLongitude());
      return detourDistance <= config.getMaxDetourDistance();
    }).collect(Collectors.toList());
  }

  private double calculateDetourDistance(double startLat, double startLon, double destLat,
      double destLon, double stationLat, double stationLon) {
    double directDistance = calculateDistance(startLat, startLon, destLat, destLon);
    double viaStationDistance = calculateDistance(startLat, startLon, stationLat, stationLon)
        + calculateDistance(stationLat, stationLon, destLat, destLon);
    return viaStationDistance - directDistance;
  }
}

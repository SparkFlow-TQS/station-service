package tqs.sparkflow.stationservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;

/** Service for managing charging stations. */
@Service
public class StationService {

  private final StationRepository stationRepository;

  // Maximum number of stations to return per search to prevent performance issues
  private static final int MAX_SEARCH_RESULTS = 500;

  public StationService(StationRepository stationRepository) {
    this.stationRepository = stationRepository;
  }

  /**
   * Gets all stations.
   *
   * @return List of all stations (limited to first 500 for performance)
   */
  public List<Station> getAllStations() {
    List<Station> allStations = stationRepository.findAll();
    return allStations.size() > MAX_SEARCH_RESULTS ? 
           allStations.subList(0, MAX_SEARCH_RESULTS) : allStations;
  }

  /**
   * Gets the total count of stations in the database.
   *
   * @return Total number of stations
   */
  public Long getTotalStationCount() {
    return stationRepository.count();
  }

  /**
   * Gets a station by ID.
   *
   * @param id The station ID
   * @return The station if found
   * @throws IllegalArgumentException if station not found
   * @throws NullPointerException if id is null
   */
  public Station getStationById(Long id) {
    if (id == null) {
      throw new NullPointerException("Station ID cannot be null");
    }
    return stationRepository
      .findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Station not found with id: " + id));
  }

  /**
   * Retrieves a station by its external ID.
   *
   * @param externalId the external ID of the station to retrieve
   * @return the station with the given external ID
   * @throws IllegalArgumentException if no station exists with the given external ID
   */
  public Station getStationByExternalId(String externalId) {
    return stationRepository
      .findByExternalId(externalId)
      .orElseThrow(
        () ->
          new IllegalArgumentException("Station not found with external id: " + externalId));
  }

  /**
   * Creates a new station.
   *
   * @param station The station to create
   * @return The created station
   * @throws NullPointerException if station is null
   * @throws IllegalArgumentException if station has invalid data
   */
  public Station createStation(Station station) {
    if (station == null) {
      throw new NullPointerException("Station cannot be null");
    }
    if (station.getName() == null || station.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Station name cannot be empty");
    }
    if (station.getConnectorType() == null || station.getConnectorType().trim().isEmpty()) {
      throw new IllegalArgumentException("Connector type cannot be empty");
    }
    if (station.getLatitude() != null
        && (station.getLatitude() < -90 || station.getLatitude() > 90)) {
      throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
    }
    if (station.getLongitude() != null
        && (station.getLongitude() < -180 || station.getLongitude() > 180)) {
      throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
    }
    return stationRepository.save(station);
  }

  /**
   * Updates an existing station.
   *
   * @param id The station ID
   * @param station The updated station data
   * @return The updated station
   * @throws IllegalArgumentException if station not found
   */
  public Station updateStation(Long id, Station station) {
    if (!stationRepository.existsById(id)) {
      throw new IllegalArgumentException("Station not found with id: " + id);
    }
    station.setId(id);
    return stationRepository.save(station);
  }

  /**
   * Deletes a station.
   *
   * @param id The station ID
   * @throws IllegalArgumentException if station not found
   * @throws NullPointerException if id is null
   */
  public void deleteStation(Long id) {
    if (id == null) {
      throw new NullPointerException("Station ID cannot be null");
    }
    if (!stationRepository.existsById(id)) {
      throw new IllegalArgumentException("Station not found with id: " + id);
    }
    stationRepository.deleteById(id);
  }

  /**
   * Searches for stations based on criteria.
   *
   * @param name The station name
   * @param city The city name
   * @param country The country name
   * @param connectorType The connector type
   * @return List of matching stations (limited to 500 results)
   */
  public List<Station> searchStations(
      String name, String city, String country, String connectorType) {
    
    // Use a more flexible search approach - search all stations and filter
    List<Station> allStations = stationRepository.findAll();
    
    List<Station> filteredStations = allStations.stream()
        .filter(station -> {
            // Name filter
            if (name != null && !name.trim().isEmpty()) {
                if (station.getName() == null || 
                    !station.getName().toLowerCase().contains(name.toLowerCase())) {
                    return false;
                }
            }
            
            // City filter
            if (city != null && !city.trim().isEmpty()) {
                if (station.getCity() == null || 
                    !station.getCity().toLowerCase().contains(city.toLowerCase())) {
                    return false;
                }
            }
            
            // Country filter
            if (country != null && !country.trim().isEmpty()) {
                if (station.getCountry() == null || 
                    !station.getCountry().toLowerCase().contains(country.toLowerCase())) {
                    return false;
                }
            }
            
            // Connector type filter
            if (connectorType != null && !connectorType.trim().isEmpty()) {
                if (station.getConnectorType() == null || 
                    !station.getConnectorType().toLowerCase().contains(connectorType.toLowerCase())) {
                    return false;
                }
            }
            
            return true;
        })
        .limit(MAX_SEARCH_RESULTS)  // Limit to maximum results
        .toList();
    
    return filteredStations;
  }

  /**
   * Gets stations within a radius of the given coordinates.
   *
   * @param latitude The latitude coordinate
   * @param longitude The longitude coordinate
   * @param radius The search radius in kilometers
   * @return List of stations within the radius (limited to 500 results)
   * @throws IllegalArgumentException if coordinates or radius are invalid
   */
  public List<Station> getNearbyStations(double latitude, double longitude, int radius) {
    if (latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
    }
    if (longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
    }
    if (radius <= 0) {
      throw new IllegalArgumentException("Radius must be greater than 0 km");
    }
    if (radius > 600) {
      throw new IllegalArgumentException("Radius cannot be greater than 600 km");
    }
    
    return stationRepository.findAll().stream()
      .filter(station -> {
        if (station.getLatitude() == null || station.getLongitude() == null) {
          return false;
        }
        double distance = calculateDistance(
            latitude, longitude,
            station.getLatitude(), station.getLongitude()
        );
        return distance <= radius;
      })
      .limit(MAX_SEARCH_RESULTS)  // Limit to maximum results
      .toList();
  }

  /**
   * Calculates the distance between two points using the Haversine formula.
   * @param lat1 Latitude of first point
   * @param lon1 Longitude of first point
   * @param lat2 Latitude of second point
   * @param lon2 Longitude of second point
   * @return Distance in kilometers
   */
  private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    final int R = 6371; // Earth's radius in kilometers

    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    
    return R * c;
  }
}

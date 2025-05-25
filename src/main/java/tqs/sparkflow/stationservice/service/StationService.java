package tqs.sparkflow.stationservice.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;

/**
 * Service for managing charging stations.
 */
@Service
public class StationService {

  @Autowired
  private StationRepository stationRepository;

  /**
   * Gets all stations.
   *
   * @return List of all stations
   */
  public List<Station> getAllStations() {
    return stationRepository.findAll();
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
    return stationRepository.findById(id)
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
    return stationRepository.findByExternalId(externalId).orElseThrow(
        () -> new IllegalArgumentException("Station not found with external id: " + externalId));
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
   * @return List of matching stations
   */
  public List<Station> searchStations(String name, String city, String country,
      String connectorType) {
    return stationRepository
        .findByNameContainingAndCityContainingAndCountryContainingAndConnectorTypeContaining(
            Optional.ofNullable(name).orElse(""), Optional.ofNullable(city).orElse(""),
            Optional.ofNullable(country).orElse(""), Optional.ofNullable(connectorType).orElse(""));
  }

  /**
   * Gets stations within a radius of the given coordinates.
   *
   * @param latitude The latitude coordinate
   * @param longitude The longitude coordinate
   * @param radius The search radius in kilometers
   * @return List of stations within the radius
   * @throws IllegalArgumentException if coordinates or radius are invalid
   */
  public List<Station> getNearbyStations(double latitude, double longitude, int radius) {
    if (latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
    }
    if (longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
    }
    if (radius <= 0 || radius > 100) {
      throw new IllegalArgumentException("Radius cannot be greater than 100 km");
    }
    // TODO: Implement actual distance calculation
    return stationRepository.findAll();
  }

  /**
   * Gets stations by connector type.
   *
   * @param connectorType The type of connector to search for
   * @return List of stations with the given connector type
   * @throws NullPointerException if connectorType is null
   * @throws IllegalArgumentException if connectorType is empty
   */
  public List<Station> getStationsByConnectorType(String connectorType) {
    if (connectorType == null) {
      throw new NullPointerException("Connector type cannot be null");
    }
    if (connectorType.trim().isEmpty()) {
      throw new IllegalArgumentException("Connector type cannot be empty");
    }
    return stationRepository.findByConnectorType(connectorType);
  }
}

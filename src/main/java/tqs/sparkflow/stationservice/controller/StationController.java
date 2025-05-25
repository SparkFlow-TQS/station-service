package tqs.sparkflow.stationservice.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.service.StationService;

/**
 * Controller for managing charging stations.
 */
@RestController
@RequestMapping("/stations")
public class StationController {

  @Autowired
  private StationService stationService;

  /**
   * Gets all stations.
   *
   * @return List of all stations
   */
  @GetMapping
  public ResponseEntity<List<Station>> getAllStations() {
    return ResponseEntity.ok(stationService.getAllStations());
  }

  /**
   * Gets a station by ID.
   *
   * @param id The station ID
   * @return The station if found
   */
  @GetMapping("/{id}")
  public ResponseEntity<Station> getStationById(@PathVariable Long id) {
    try {
      Station station = stationService.getStationById(id);
      return ResponseEntity.ok(station);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Retrieves a station by its external ID.
   *
   * @param externalId the external ID of the station to retrieve
   * @return the station with the given external ID
   */
  @GetMapping("/external/{externalId}")
  public ResponseEntity<Station> getStationByExternalId(@PathVariable String externalId) {
    return ResponseEntity.ok(stationService.getStationByExternalId(externalId));
  }

  /**
   * Creates a new station.
   *
   * @param station The station to create
   * @return The created station
   */
  @PostMapping
  public ResponseEntity<Station> createStation(@RequestBody Station station) {
    return ResponseEntity.status(HttpStatus.CREATED).body(stationService.createStation(station));
  }

  /**
   * Updates an existing station.
   *
   * @param id The station ID
   * @param station The updated station data
   * @return The updated station
   */
  @PutMapping("/{id}")
  public ResponseEntity<Station> updateStation(@PathVariable Long id,
      @RequestBody Station station) {
    return ResponseEntity.ok(stationService.updateStation(id, station));
  }

  /**
   * Deletes a station.
   *
   * @param id The station ID
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
    try {
      stationService.deleteStation(id);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
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
  @GetMapping("/search")
  public ResponseEntity<List<Station>> searchStations(@RequestParam(required = false) String name,
      @RequestParam(required = false) String city, @RequestParam(required = false) String country,
      @RequestParam(required = false) String connectorType) {
    return ResponseEntity.ok(stationService.searchStations(name, city, country, connectorType));
  }

  /**
   * Gets stations within a radius of the given coordinates.
   *
   * @param latitude The latitude coordinate
   * @param longitude The longitude coordinate
   * @param radius The search radius in kilometers
   * @return List of stations within the radius
   */
  @GetMapping("/nearby")
  public ResponseEntity<List<Station>> getNearbyStations(@RequestParam double latitude,
      @RequestParam double longitude, @RequestParam int radius) {
    return ResponseEntity.ok(stationService.getNearbyStations(latitude, longitude, radius));
  }

  /**
   * Gets stations by connector type.
   *
   * @param connectorType The type of connector to search for
   * @return List of stations with the given connector type
   */
  @GetMapping("/connector/{connectorType}")
  public ResponseEntity<List<Station>> getStationsByConnectorType(
      @PathVariable String connectorType) {
    return ResponseEntity.ok(stationService.getStationsByConnectorType(connectorType));
  }
}

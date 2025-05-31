package tqs.sparkflow.stationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.service.StationService;

/**
 * Controller for managing charging stations.
 * Provides endpoints for CRUD operations and advanced search functionality.
 */
@RestController
@RequestMapping("/stations")
@Tag(name = "Station", description = "The Station API")
public class StationController {

  private final StationService stationService;

  public StationController(StationService stationService) {
    this.stationService = stationService;
  }

  /**
   * Gets all stations.
   *
   * @return List of all stations
   */
  @Operation(summary = "Get all stations", description = "Retrieves a list of all charging stations in the system")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved all stations",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = Station.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
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
  @Operation(summary = "Get station by ID", description = "Retrieves a specific charging station by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved the station",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = Station.class))),
      @ApiResponse(responseCode = "404", description = "Station not found")
  })
  @GetMapping("/{id}")
  public ResponseEntity<Station> getStationById(
      @Parameter(description = "ID of the station to retrieve", required = true) @PathVariable Long id) {
    try {
      Station station = stationService.getStationById(id);
      return ResponseEntity.ok(station);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Retrieves a station by its external ID.
   *
   * @param externalId the external ID of the station to retrieve
   * @return the station with the given external ID
   */
  @Operation(summary = "Get station by external ID", description = "Retrieves a charging station by its external ID (e.g., from OpenChargeMap)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved the station",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = Station.class))),
      @ApiResponse(responseCode = "404", description = "Station not found")
  })
  @GetMapping("/external/{externalId}")
  public ResponseEntity<Station> getStationByExternalId(
      @Parameter(description = "External ID of the station to retrieve", required = true) @PathVariable String externalId) {
    try {
      Station station = stationService.getStationByExternalId(externalId);
      return ResponseEntity.ok(station);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Creates a new station.
   *
   * @param station The station to create
   * @return The created station
   */
  @Operation(summary = "Create a new station", description = "Creates a new charging station in the system")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Station successfully created",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = Station.class))),
      @ApiResponse(responseCode = "400", description = "Invalid station data provided")
  })
  @PostMapping
  public ResponseEntity<Station> createStation(
      @Parameter(description = "Station object to create", required = true) @RequestBody Station station) {
    return ResponseEntity.status(HttpStatus.CREATED).body(stationService.createStation(station));
  }

  /**
   * Updates an existing station.
   *
   * @param id The station ID
   * @param station The updated station data
   * @return The updated station
   */
  @Operation(summary = "Update a station", description = "Updates an existing charging station's information")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Station successfully updated",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = Station.class))),
      @ApiResponse(responseCode = "404", description = "Station not found"),
      @ApiResponse(responseCode = "400", description = "Invalid station data provided")
  })
  @PutMapping("/{id}")
  public ResponseEntity<Station> updateStation(
      @Parameter(description = "ID of the station to update", required = true) @PathVariable Long id,
      @Parameter(description = "Updated station data", required = true) @RequestBody Station station) {
    try {
      Station updatedStation = stationService.updateStation(id, station);
      return ResponseEntity.ok(updatedStation);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Deletes a station.
   *
   * @param id The station ID
   */
  @Operation(summary = "Delete a station", description = "Removes a charging station from the system")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Station successfully deleted"),
      @ApiResponse(responseCode = "404", description = "Station not found")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStation(
      @Parameter(description = "ID of the station to delete", required = true) @PathVariable Long id) {
    try {
      stationService.deleteStation(id);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
  @Operation(summary = "Search stations", description = "Searches for stations based on various criteria")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved matching stations",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = Station.class))),
      @ApiResponse(responseCode = "400", description = "Invalid search parameters")
  })
  @GetMapping("/search")
  public ResponseEntity<List<Station>> searchStations(
      @Parameter(description = "Station name to search for") @RequestParam(required = false) String name,
      @Parameter(description = "City to search in") @RequestParam(required = false) String city,
      @Parameter(description = "Country to search in") @RequestParam(required = false) String country,
      @Parameter(description = "Type of connector to search for") @RequestParam(required = false) String connectorType) {
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
  @Operation(summary = "Find nearby stations", description = "Finds charging stations within a specified radius of given coordinates")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved nearby stations",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = Station.class))),
      @ApiResponse(responseCode = "400", description = "Invalid coordinates or radius")
  })
  @GetMapping("/nearby")
  public ResponseEntity<List<Station>> getNearbyStations(
      @Parameter(description = "Latitude coordinate", required = true) @RequestParam double latitude,
      @Parameter(description = "Longitude coordinate", required = true) @RequestParam double longitude,
      @Parameter(description = "Search radius in kilometers", required = true) @RequestParam int radius) {
    return ResponseEntity.ok(stationService.getNearbyStations(latitude, longitude, radius));
  }

  /**
   * Gets stations by connector type.
   *
   * @param connectorType The type of connector to search for
   * @return List of stations with the given connector type
   */
  @Operation(summary = "Get stations by connector type", description = "Retrieves all stations that support a specific connector type")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved stations with the specified connector type",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = Station.class))),
      @ApiResponse(responseCode = "400", description = "Invalid connector type")
  })
  @GetMapping("/connector/{connectorType}")
  public ResponseEntity<List<Station>> getStationsByConnectorType(
      @Parameter(description = "Type of connector to search for", required = true) @PathVariable String connectorType) {
    return ResponseEntity.ok(stationService.getStationsByConnectorType(connectorType));
  }
}

package tqs.sparkflow.stationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.sparkflow.stationservice.service.OpenChargeMapService;

/**
 * Controller for OpenChargeMap API integration.
 * This controller provides endpoints to interact with the OpenChargeMap API and populate the local database with charging stations.
 */
@RestController
@RequestMapping("/api/openchargemap")
@Tag(name = "OpenChargeMap", description = "OpenChargeMap API integration endpoints")
public class OpenChargeMapController {

  private final OpenChargeMapService openChargeMapService;

  public OpenChargeMapController(OpenChargeMapService openChargeMapService) {
    this.openChargeMapService = openChargeMapService;
  }

  /**
   * Populates the local database with charging stations from OpenChargeMap API based on geographic coordinates.
   * This endpoint searches for charging stations within a specified radius of the given coordinates
   * and stores them in the local database.
   *
   * @param latitude The latitude coordinate (-90 to 90 degrees)
   * @param longitude The longitude coordinate (-180 to 180 degrees)
   * @param radius The search radius in kilometers (must be positive)
   * @return ResponseEntity with success message
   * @throws IllegalArgumentException if coordinates or radius are invalid
   * @throws IllegalStateException if there's an error accessing the OpenChargeMap API
   */
  @Operation(
      summary = "Populate stations from OpenChargeMap",
      description = "Retrieves charging stations from OpenChargeMap API within a specified radius of given coordinates and stores them in the local database")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Stations populated successfully",
          content = @Content(schema = @Schema(implementation = String.class))),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid input parameters",
          content = @Content(schema = @Schema(implementation = String.class))),
      @ApiResponse(
          responseCode = "403",
          description = "Access denied to OpenChargeMap API (check API key)",
          content = @Content(schema = @Schema(implementation = String.class))),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(schema = @Schema(implementation = String.class)))
  })
  @PostMapping("/populate")
  public ResponseEntity<String> populateStations(
      @Parameter(description = "Latitude coordinate (-90 to 90 degrees)", example = "40.7128")
      @RequestParam double latitude,
      @Parameter(description = "Longitude coordinate (-180 to 180 degrees)", example = "-74.0060")
      @RequestParam double longitude,
      @Parameter(description = "Search radius in kilometers (must be positive)", example = "50")
      @RequestParam int radius) {
    openChargeMapService.populateStations(latitude, longitude, radius);
    return ResponseEntity.ok("Stations populated successfully");
  }
}

package tqs.sparkflow.stationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tqs.sparkflow.stationservice.service.OpenChargeMapService;

/** Controller for OpenChargeMap API integration. */
@RestController
@RequestMapping("/api/openchargemap")
public class OpenChargeMapController {

  private final OpenChargeMapService openChargeMapService;

  public OpenChargeMapController(OpenChargeMapService openChargeMapService) {
    this.openChargeMapService = openChargeMapService;
  }

  /**
   * Populates stations from OpenChargeMap API.
   *
   * @param latitude The latitude coordinate
   * @param longitude The longitude coordinate
   * @param radius The search radius in kilometers
   * @return ResponseEntity with success message
   */
  @PostMapping("/populate")
  public ResponseEntity<String> populateStations(
      @RequestParam double latitude, @RequestParam double longitude, @RequestParam int radius) {
    openChargeMapService.populateStations(latitude, longitude, radius);
    return ResponseEntity.ok("Stations populated successfully");
  }
}

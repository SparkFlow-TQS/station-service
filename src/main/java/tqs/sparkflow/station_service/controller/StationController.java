package tqs.sparkflow.station_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.sparkflow.station_service.model.Station;
import tqs.sparkflow.station_service.service.StationService;
import java.util.List;

/**
 * REST controller for managing charging stations.
 */
@RestController
@RequestMapping("/stations")
@CrossOrigin(origins = "*")
public class StationController {

    private final StationService stationService;

    @Autowired
    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    /**
     * Get all charging stations.
     *
     * @return list of all stations
     */
    @GetMapping
    public ResponseEntity<List<Station>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllStations());
    }

    /**
     * Get a station by its ID.
     *
     * @param id the station ID
     * @return the station
     */
    @GetMapping("/{id}")
    public ResponseEntity<Station> getStationById(@PathVariable String id) {
        return ResponseEntity.ok(stationService.getStationById(id));
    }

    /**
     * Get nearby stations based on coordinates and radius.
     *
     * @param latitude the latitude coordinate
     * @param longitude the longitude coordinate
     * @param radius the search radius in kilometers (default: 10)
     * @return list of nearby stations
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<Station>> getNearbyStations(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10") int radius) {
        return ResponseEntity.ok(stationService.getNearbyStations(latitude, longitude, radius));
    }

    /**
     * Get stations by connector type.
     *
     * @param type the connector type (e.g., "Type2", "CCS", "CHAdeMO")
     * @return list of stations with the specified connector type
     */
    @GetMapping("/connector/{type}")
    public ResponseEntity<List<Station>> getStationsByConnectorType(@PathVariable String type) {
        return ResponseEntity.ok(stationService.getStationsByConnectorType(type));
    }
} 
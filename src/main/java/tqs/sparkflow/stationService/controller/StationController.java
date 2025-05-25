package tqs.sparkflow.stationService.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.sparkflow.stationService.model.Station;
import tqs.sparkflow.stationService.service.StationService;
import java.util.List;

/**
 * REST controller for managing charging stations.
 */
@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;

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

    /**
     * Create a new charging station.
     *
     * @param station the station to create
     * @return the created station
     */
    @PostMapping
    public ResponseEntity<Station> createStation(@Valid @RequestBody Station station) {
        Station createdStation = stationService.createStation(station);
        return new ResponseEntity<>(createdStation, HttpStatus.CREATED);
    }

    /**
     * Delete a station by its ID.
     *
     * @param id the station ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable String id) {
        stationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
} 
package tqs.sparkflow.station_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tqs.sparkflow.station_service.model.Station;
import tqs.sparkflow.station_service.service.StationService;
import java.util.List;

@RestController
@RequestMapping("/stations")
@CrossOrigin(origins = "*")
public class StationController {

    private final StationService stationService;

    @Autowired
    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public List<Station> getAllStations() {
        return stationService.getAllStations();
    }

    @GetMapping("/{id}")
    public Station getStationById(@PathVariable String id) {
        return stationService.getStationById(id);
    }

    @GetMapping("/nearby")
    public List<Station> getNearbyStations(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10") int radius) {
        return stationService.getNearbyStations(latitude, longitude, radius);
    }

    @GetMapping("/connector/{type}")
    public List<Station> getStationsByConnectorType(@PathVariable String type) {
        return stationService.getStationsByConnectorType(type);
    }
} 
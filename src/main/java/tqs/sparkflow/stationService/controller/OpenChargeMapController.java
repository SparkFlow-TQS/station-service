package tqs.sparkflow.stationService.controller;

import org.springframework.web.bind.annotation.*;
import tqs.sparkflow.stationService.service.OpenChargeMapService;

@RestController
@RequestMapping("/admin/openchargemap")
public class OpenChargeMapController {

    private final OpenChargeMapService openChargeMapService;

    public OpenChargeMapController(OpenChargeMapService openChargeMapService) {
        this.openChargeMapService = openChargeMapService;
    }

    @PostMapping("/populate")
    public String populateStations(
            @RequestParam(defaultValue = "38.7223") double latitude,
            @RequestParam(defaultValue = "-9.1393") double longitude,
            @RequestParam(defaultValue = "50") int radius) {
        return openChargeMapService.populateStations(latitude, longitude, radius);
    }
}

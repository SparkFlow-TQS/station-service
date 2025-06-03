package tqs.sparkflow.stationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.service.ChargingSessionService;

@RestController
@RequestMapping("/api/v1/charging-sessions")
public class ChargingSessionController {
    private final ChargingSessionService chargingSessionService;

    public ChargingSessionController(ChargingSessionService chargingSessionService) {
        this.chargingSessionService = chargingSessionService;
    }

    // Controller endpoints will be implemented later
} 
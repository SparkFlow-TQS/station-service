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

  @PostMapping("/unlock")
  public ResponseEntity<ChargingSession> unlockStation(
        @RequestParam String stationId,
        @RequestParam String userId) {
    return ResponseEntity.ok(chargingSessionService.unlockStation(stationId, userId));
  }

  @PostMapping("/{sessionId}/start")
  public ResponseEntity<ChargingSession> startCharging(@PathVariable String sessionId) {
    return ResponseEntity.ok(chargingSessionService.startCharging(sessionId));
  }

  @PostMapping("/{sessionId}/end")
  public ResponseEntity<ChargingSession> endCharging(@PathVariable String sessionId) {
    return ResponseEntity.ok(chargingSessionService.endCharging(sessionId));
  }

  @GetMapping("/{sessionId}/status")
  public ResponseEntity<ChargingSession> getSessionStatus(@PathVariable String sessionId) {
    return ResponseEntity.ok(chargingSessionService.getSessionStatus(sessionId));
  }
} 
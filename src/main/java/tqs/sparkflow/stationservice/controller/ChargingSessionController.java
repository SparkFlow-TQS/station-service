package tqs.sparkflow.stationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.service.ChargingSessionService;

/**
 * REST controller for managing charging sessions.
 * Provides endpoints for the complete lifecycle of a charging session.
 */
@RestController
@RequestMapping("/charging-sessions")
@Tag(name = "Charging Session", description = "APIs for managing charging sessions")
public class ChargingSessionController {
  private final ChargingSessionService chargingSessionService;

  public ChargingSessionController(ChargingSessionService chargingSessionService) {
    this.chargingSessionService = chargingSessionService;
  }

  /**
   * Unlocks a charging station and creates a new charging session.
   * 
   * @param stationId The ID of the station to unlock
   * @param userId The ID of the user requesting the unlock
   * @return The created charging session with 200 OK status
   */
  @Operation(
    summary = "Unlock a charging station",
    description = "Creates a new charging session by unlocking a station"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Station unlocked successfully",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ChargingSession.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid input parameters"
    )
  })
  @PostMapping("/unlock")
  public ResponseEntity<ChargingSession> unlockStation(
        @Parameter(description = "ID of the station to unlock", required = true)
        @RequestParam String stationId,
        @Parameter(description = "ID of the user requesting the unlock", required = true)
        @RequestParam String userId) {
    return ResponseEntity.ok(chargingSessionService.unlockStation(stationId, userId));
  }

  /**
   * Starts a charging session.
   * 
   * @param sessionId The ID of the session to start
   * @return The updated charging session with 200 OK status
   * @throws ChargingSessionNotFoundException if the session is not found (404)
   */
  @Operation(
    summary = "Start a charging session",
    description = "Updates the session status to CHARGING and sets the start time"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Charging started successfully",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ChargingSession.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Charging session not found"
    )
  })
  @PostMapping("/{sessionId}/start")
  public ResponseEntity<ChargingSession> startCharging(
        @Parameter(description = "ID of the session to start", required = true)
        @PathVariable String sessionId) {
    return ResponseEntity.ok(chargingSessionService.startCharging(sessionId));
  }

  /**
   * Ends a charging session.
   * 
   * @param sessionId The ID of the session to end
   * @return The updated charging session with 200 OK status
   * @throws ChargingSessionNotFoundException if the session is not found (404)
   */
  @Operation(
    summary = "End a charging session",
    description = "Updates the session status to COMPLETED and sets the end time"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Charging ended successfully",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ChargingSession.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Charging session not found"
    )
  })
  @PostMapping("/{sessionId}/end")
  public ResponseEntity<ChargingSession> endCharging(
        @Parameter(description = "ID of the session to end", required = true)
        @PathVariable String sessionId) {
    return ResponseEntity.ok(chargingSessionService.endCharging(sessionId));
  }

  /**
   * Retrieves the current status of a charging session.
   * 
   * @param sessionId The ID of the session to get status for
   * @return The charging session with 200 OK status
   * @throws ChargingSessionNotFoundException if the session is not found (404)
   */
  @Operation(
    summary = "Get charging session status",
    description = "Retrieves the current status and details of a charging session"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Session status retrieved successfully",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ChargingSession.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Charging session not found"
    )
  })
  @GetMapping("/{sessionId}/status")
  public ResponseEntity<ChargingSession> getSessionStatus(
        @Parameter(description = "ID of the session to get status for", required = true)
        @PathVariable String sessionId) {
    return ResponseEntity.ok(chargingSessionService.getSessionStatus(sessionId));
  }
} 
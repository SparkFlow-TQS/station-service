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
   * Creates and starts a new charging session immediately.
   * 
   * @param stationId The ID of the station to use
   * @param userId The ID of the user starting the session
   * @return The created charging session with 200 OK status
   */
  @Operation(
    summary = "Start a charging session",
    description = "Creates a new charging session and starts it immediately"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Charging session started successfully",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ChargingSession.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Cannot start session: no booking or free chargers available"
    )
  })
  @PostMapping("/start")
  public ResponseEntity<ChargingSession> startSession(
        @Parameter(description = "ID of the station to use", required = true)
        @RequestParam String stationId,
        @Parameter(description = "ID of the user starting the session", required = true)
        @RequestParam String userId) {
    return ResponseEntity.ok(chargingSessionService.createSession(stationId, userId));
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
    description = "Marks the session as finished and sets the end time"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Charging session ended successfully",
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
  public ResponseEntity<ChargingSession> endSession(
        @Parameter(description = "ID of the session to end", required = true)
        @PathVariable String sessionId) {
    return ResponseEntity.ok(chargingSessionService.endSession(sessionId));
  }

  /**
   * Retrieves a charging session.
   * 
   * @param sessionId The ID of the session to retrieve
   * @return The charging session with 200 OK status
   * @throws ChargingSessionNotFoundException if the session is not found (404)
   */
  @Operation(
    summary = "Get charging session",
    description = "Retrieves the details of a charging session"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Session retrieved successfully",
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
  @GetMapping("/{sessionId}")
  public ResponseEntity<ChargingSession> getSession(
        @Parameter(description = "ID of the session to retrieve", required = true)
        @PathVariable String sessionId) {
    return ResponseEntity.ok(chargingSessionService.getSession(sessionId));
  }
} 
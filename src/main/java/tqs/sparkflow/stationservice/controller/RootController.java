package tqs.sparkflow.stationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Root controller for the station service.
 * Provides basic service information at the root endpoint.
 */
@RestController
@Tag(name = "Root", description = "Service information endpoints")
public class RootController {

  /**
   * Root endpoint for the station service.
   *
   * @return Service information
   */
  @Operation(summary = "Get service info", description = "Returns basic information about the station service")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Service information",
          content = @Content(mediaType = "application/json"))
  })
  @GetMapping("/")
  public ResponseEntity<String> getServiceInfo() {
    return ResponseEntity.ok("{\"service\":\"Station Service\",\"status\":\"Running\",\"version\":\"1.0.0\"}");
  }
} 
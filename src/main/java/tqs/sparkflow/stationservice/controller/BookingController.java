package tqs.sparkflow.stationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.service.BookingService;

@RestController
@RequestMapping("/bookings")
@Tag(name = "Booking", description = "Booking management APIs")
public class BookingController {
  private final BookingService bookingService;

  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  /**
   * Creates a new booking for a charging station.
   *
   * @param booking The booking details
   * @param principal The authenticated user
   * @return ResponseEntity containing the created booking or error status
   */
  @PostMapping
  @Operation(summary = "Create a new booking",
      description = "Creates a new booking for a charging station")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Booking created successfully",
          content = @Content(schema = @Schema(implementation = Booking.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input or station not operational"),
      @ApiResponse(responseCode = "401", description = "User not authenticated"),
      @ApiResponse(responseCode = "404", description = "User or station not found")})
  public ResponseEntity<Booking> createBooking(
      @Parameter(description = "Booking details", required = true) @RequestBody Booking booking,
      Principal principal) {
    // Validate required fields first
    if (booking.getStationId() == null || booking.getUserId() == null
        || booking.getStartTime() == null || booking.getEndTime() == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Then check authentication
    if (principal == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    try {
      Booking createdBooking = bookingService.createBooking(booking);
      return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    } catch (IllegalStateException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Creates a recurring booking for a charging station.
   *
   * @param userId The ID of the user making the booking
   * @param stationId The ID of the station to book
   * @param startTime The start time of the booking
   * @param endTime The end time of the booking
   * @param recurringDays The days of the week when the booking should recur
   * @return ResponseEntity containing the created booking or error status
   */
  @PostMapping("/recurring")
  @Operation(summary = "Create a recurring booking",
      description = "Creates a recurring booking for a charging station")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Recurring booking created successfully",
          content = @Content(schema = @Schema(implementation = Booking.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input or station not operational"),
      @ApiResponse(responseCode = "401", description = "User not authenticated"),
      @ApiResponse(responseCode = "404", description = "User or station not found")})
  public ResponseEntity<Booking> createRecurringBooking(
      @Parameter(description = "User ID", required = true) @RequestParam Long userId,
      @Parameter(description = "Station ID", required = true) @RequestParam Long stationId,
      @Parameter(description = "Start time (ISO-8601 format)",
          required = true) @RequestParam LocalDateTime startTime,
      @Parameter(description = "End time (ISO-8601 format)",
          required = true) @RequestParam LocalDateTime endTime,
      @Parameter(description = "Set of days for recurring booking (0-6, where 0 is Sunday)",
          required = true) @RequestParam Set<Integer> recurringDays) {
    try {
      Booking booking = bookingService.createRecurringBooking(userId, stationId, startTime, endTime,
          recurringDays);
      return new ResponseEntity<>(booking, HttpStatus.CREATED);
    } catch (IllegalStateException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Retrieves a booking by its ID.
   *
   * @param id The ID of the booking to retrieve
   * @param principal The authenticated user
   * @return ResponseEntity containing the booking if found and user has permission
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get booking by ID", description = "Retrieves a booking by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Booking found",
          content = @Content(schema = @Schema(implementation = Booking.class))),
      @ApiResponse(responseCode = "401", description = "User not authenticated"),
      @ApiResponse(responseCode = "403",
          description = "User not authorized to access this booking"),
      @ApiResponse(responseCode = "404", description = "Booking not found")})
  public ResponseEntity<Booking> getBookingById(
      @Parameter(description = "Booking ID", required = true) @PathVariable Long id,
      Principal principal) {
    Long requestingUserId = Long.valueOf(principal.getName());
    return bookingService.getBookingById(id, requestingUserId).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Retrieves all bookings for a user.
   *
   * @param userId The ID of the user whose bookings to retrieve
   * @return ResponseEntity containing the list of bookings or error status
   */
  @GetMapping
  @Operation(summary = "Get all bookings", description = "Retrieves all bookings for a user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Bookings found",
          content = @Content(schema = @Schema(implementation = Booking.class))),
      @ApiResponse(responseCode = "204", description = "No bookings found"),
      @ApiResponse(responseCode = "400", description = "Invalid user ID"),
      @ApiResponse(responseCode = "401", description = "User not authenticated"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  public ResponseEntity<List<Booking>> getAllBookings(
      @Parameter(description = "User ID", required = true) @RequestParam Long userId) {
    try {
      List<Booking> bookings = bookingService.getAllBookings(userId);
      if (bookings.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return ResponseEntity.ok(bookings);
    } catch (IllegalStateException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Cancels an existing booking.
   *
   * @param id The ID of the booking to cancel
   * @return ResponseEntity with no content if successful
   */
  @PostMapping("/{id}/cancel")
  @Operation(summary = "Cancel a booking", description = "Cancels an existing booking")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "204", description = "Booking cancelled successfully"),
          @ApiResponse(responseCode = "401", description = "User not authenticated"),
          @ApiResponse(responseCode = "403",
              description = "User not authorized to cancel this booking"),
          @ApiResponse(responseCode = "404", description = "Booking not found")})
  public ResponseEntity<Void> cancelBooking(
      @Parameter(description = "Booking ID", required = true) @PathVariable Long id) {
    try {
      bookingService.cancelBooking(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (IllegalStateException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Retrieves all bookings for a specific station.
   *
   * @param stationId The ID of the station
   * @return ResponseEntity containing the list of bookings or error status
   */
  @GetMapping("/station/{stationId}")
  @Operation(summary = "Get bookings by station ID",
      description = "Retrieves all bookings for a specific station")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Bookings found",
          content = @Content(schema = @Schema(implementation = Booking.class))),
      @ApiResponse(responseCode = "204", description = "No bookings found for this station"),
      @ApiResponse(responseCode = "400", description = "Invalid station ID"),
      @ApiResponse(responseCode = "401", description = "User not authenticated"),
      @ApiResponse(responseCode = "404", description = "Station not found")})
  public ResponseEntity<List<Booking>> getBookingsByStationId(
      @Parameter(description = "Station ID", required = true) @PathVariable Long stationId,
      Principal principal) {
    if (principal == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    try {
      Long requestingUserId = Long.valueOf(principal.getName());
      List<Booking> bookings = bookingService.getBookingsByStationId(stationId, requestingUserId);
      if (bookings.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return ResponseEntity.ok(bookings);
    } catch (IllegalStateException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Retrieves all bookings for a specific user.
   *
   * @param userId The ID of the user whose bookings to retrieve
   * @return ResponseEntity containing the list of bookings or error status
   */
  @GetMapping("/user/{userId}")
  @Operation(summary = "Get bookings by user ID",
      description = "Retrieves all bookings for a specific user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Bookings found",
          content = @Content(schema = @Schema(implementation = Booking.class))),
      @ApiResponse(responseCode = "204", description = "No bookings found for this user"),
      @ApiResponse(responseCode = "400", description = "Invalid user ID"),
      @ApiResponse(responseCode = "401", description = "User not authenticated"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  public ResponseEntity<List<Booking>> getBookingsByUserId(
      @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
    try {
      List<Booking> bookings = bookingService.getBookingsByUserId(userId);
      if (bookings.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return ResponseEntity.ok(bookings);
    } catch (IllegalStateException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }
}

package tqs.sparkflow.stationservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
@Tag(name = "Booking", description = "The Booking API")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }
        return Long.parseLong(authentication.getName());
    }

    @Operation(summary = "Create a new booking", description = "Creates a new booking for a charging station")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Booking successfully created",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Booking.class))),
        @ApiResponse(responseCode = "400", description = "Invalid booking data provided"),
        @ApiResponse(responseCode = "403", description = "User not authorized to create booking")
    })
    @PostMapping
    public ResponseEntity<Booking> createBooking(
        @Parameter(description = "Booking object to create", required = true) 
        @RequestBody Booking booking) {
        Long userId = getCurrentUserId();
        booking.setUserId(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(booking));
    }

    @Operation(summary = "Get booking by ID", description = "Retrieves a specific booking by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the booking",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Booking.class))),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "403", description = "User not authorized to view booking")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(
        @Parameter(description = "ID of the booking to retrieve", required = true) 
        @PathVariable Long id) {
        Optional<Booking> booking = bookingService.getBookingById(id);
        return booking.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all bookings", description = "Retrieves a list of all bookings the user has permission to view")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all bookings",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Booking.class)))
    })
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(bookingService.getAllBookings(userId));
    }

    @Operation(summary = "Cancel a booking", description = "Cancels an existing booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking successfully cancelled",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Booking.class))),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "403", description = "User not authorized to cancel booking")
    })
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(
        @Parameter(description = "ID of the booking to cancel", required = true) 
        @PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @Operation(summary = "Get bookings by station ID", description = "Retrieves all bookings for a specific station")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved bookings for the station",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Booking.class)))
    })
    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<Booking>> getBookingsByStationId(
        @Parameter(description = "ID of the station", required = true) 
        @PathVariable Long stationId) {
        return ResponseEntity.ok(bookingService.getBookingsByStationId(stationId));
    }

    @Operation(summary = "Get bookings by user ID", description = "Retrieves all bookings for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved bookings for the user",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Booking.class))),
        @ApiResponse(responseCode = "403", description = "User not authorized to view these bookings")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUserId(
        @Parameter(description = "ID of the user", required = true) 
        @PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
    }
}
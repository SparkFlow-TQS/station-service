package tqs.sparkflow.stationservice.service;

import tqs.sparkflow.stationservice.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingService {
    /**
     * Creates a recurring booking for a user at a station.
     * Validates that the user exists and has permission to make bookings.
     *
     * @param userId The ID of the user making the booking
     * @param stationId The ID of the station to book
     * @param startTime The start time of the booking
     * @param endTime The end time of the booking
     * @param recurringDays The days of the week when the booking should recur
     * @return The created booking
     * @throws IllegalStateException if the user is not found or not authorized
     */
    Booking createRecurringBooking(Long userId, Long stationId, LocalDateTime startTime, 
                                 LocalDateTime endTime, Set<Integer> recurringDays);

    /**
     * Creates a new booking.
     * Validates that the user exists and has permission to make bookings.
     *
     * @param booking The booking to create
     * @return The created booking
     * @throws IllegalStateException if the user is not found or not authorized
     */
    Booking createBooking(Booking booking);

    /**
     * Gets a booking by its ID.
     * Validates that the requesting user has permission to view the booking.
     *
     * @param id The booking ID
     * @param requestingUserId The ID of the user requesting the booking
     * @return The booking if found and user has permission
     * @throws IllegalStateException if the user is not authorized to view the booking
     */
    Optional<Booking> getBookingById(Long id, Long requestingUserId);

    /**
     * Gets all bookings.
     * Only returns bookings that the user has permission to view.
     *
     * @return List of bookings the user has permission to view
     */
    List<Booking> getAllBookings(Long userId);

    /**
     * Cancels a booking.
     * Validates that the user has permission to cancel the booking.
     *
     * @param id The booking ID
     * @return The cancelled booking
     * @throws IllegalStateException if the user is not authorized to cancel the booking
     */
    Booking cancelBooking(Long id);

    /**
     * Gets all bookings for a specific station.
     * Only returns bookings that the user has permission to view.
     *
     * @param stationId The station ID
     * @return List of bookings for the station that the user has permission to view
     */
    List<Booking> getBookingsByStationId(Long stationId);

    /**
     * Gets all bookings for a specific user.
     * Validates that the requesting user has permission to view the bookings.
     *
     * @param userId The ID of the user whose bookings to retrieve
     * @return List of bookings for the user that the requesting user has permission to view
     * @throws IllegalStateException if the requesting user is not authorized to view the bookings
     */
    List<Booking> getBookingsByUserId(Long userId);
} 
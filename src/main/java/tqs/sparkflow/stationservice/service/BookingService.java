package tqs.sparkflow.stationservice.service;

import org.springframework.stereotype.Service;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final StationService stationService;

    public BookingService(BookingRepository bookingRepository, StationService stationService) {
        this.bookingRepository = bookingRepository;
        this.stationService = stationService;
    }

    public Booking createRecurringBooking(String userId, Long stationId, LocalDateTime startTime, 
                                        LocalDateTime endTime, Set<Integer> recurringDays) {
        // Validate input parameters
        if (userId == null || userId.trim().isEmpty()) {
            throw new NullPointerException("User ID cannot be null or empty");
        }
        if (stationId == null) {
            throw new NullPointerException("Station ID cannot be null");
        }
        if (startTime == null) {
            throw new NullPointerException("Start time cannot be null");
        }
        if (endTime == null) {
            throw new NullPointerException("End time cannot be null");
        }
        if (recurringDays == null || recurringDays.isEmpty()) {
            throw new IllegalArgumentException("Recurring days cannot be null or empty");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Check if station exists and is operational
        Station station = stationService.getStationById(stationId);
        if (!station.getIsOperational()) {
            throw new IllegalStateException("Station is not operational");
        }

        // Check for overlapping bookings
        if (!bookingRepository.findOverlappingBookings(stationId, startTime, endTime).isEmpty()) {
            throw new IllegalStateException("Time slot is already booked");
        }

        // Create and save the booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setStationId(stationId);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setRecurringDays(recurringDays);
        booking.setStatus(Booking.BookingStatus.ACTIVE);

        return bookingRepository.save(booking);
    }
} 
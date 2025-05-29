package tqs.sparkflow.stationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final StationService stationService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, StationService stationService) {
        this.bookingRepository = bookingRepository;
        this.stationService = stationService;
    }

    @Override
    public Booking createRecurringBooking(Long userId, Long stationId, LocalDateTime startTime, 
                                        LocalDateTime endTime, Set<Integer> recurringDays) {
        // Validate input parameters
        if (userId == null) {
            throw new NullPointerException("User ID cannot be null");
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
        booking.setStatus(BookingStatus.ACTIVE);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking createBooking(Booking booking) {
        booking.setStatus(BookingStatus.ACTIVE);
        return bookingRepository.save(booking);
    }

    @Override
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingsByStationId(Long stationId) {
        return bookingRepository.findByStationId(stationId);
    }

    @Override
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }
} 
package tqs.sparkflow.stationservice.service;

import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;
import tqs.sparkflow.stationservice.model.Station;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingService {
    Booking createRecurringBooking(Long userId, Long stationId, LocalDateTime startTime, 
                                 LocalDateTime endTime, Set<Integer> recurringDays);
    Booking createBooking(Booking booking);
    Optional<Booking> getBookingById(Long id);
    List<Booking> getAllBookings();
    Booking cancelBooking(Long id);
    List<Booking> getBookingsByStationId(Long stationId);
    List<Booking> getBookingsByUserId(Long userId);
} 
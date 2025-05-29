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
        // This is a minimal implementation that will make the tests fail
        throw new UnsupportedOperationException("Not implemented yet");
    }
} 
package tqs.sparkflow.stationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tqs.sparkflow.stationservice.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findOverlappingBookings(Long stationId, LocalDateTime startTime, LocalDateTime endTime);
} 
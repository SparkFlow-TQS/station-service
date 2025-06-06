package tqs.sparkflow.stationservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tqs.sparkflow.stationservice.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
  @Query("SELECT b FROM Booking b WHERE b.stationId = :stationId AND b.status = 'ACTIVE' "
      + "AND ((b.startTime <= :endTime AND b.endTime >= :startTime))")
  List<Booking> findOverlappingBookings(@Param("stationId") Long stationId,
      @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

  @Query("SELECT b FROM Booking b WHERE b.stationId = :stationId AND b.status = 'ACTIVE' "
      + "AND b.startTime <= :currentTime AND b.endTime >= :currentTime")
  List<Booking> findActiveBookingsForStationAtTime(@Param("stationId") Long stationId,
      @Param("currentTime") LocalDateTime currentTime);

  List<Booking> findByStationId(Long stationId);

  List<Booking> findByUserId(Long userId);

  List<Booking> findByStationIdAndUserId(Long stationId, Long userId);
}

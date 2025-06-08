package tqs.sparkflow.stationservice.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tqs.sparkflow.stationservice.model.ChargingSession;

@Repository
public interface ChargingSessionRepository extends JpaRepository<ChargingSession, Long> {

  @Query("SELECT cs FROM ChargingSession cs WHERE cs.stationId = :stationId AND cs.finished = false")
  List<ChargingSession> findUnfinishedSessionsByStation(@Param("stationId") Long stationId);
  
  @Query("SELECT cs FROM ChargingSession cs WHERE cs.stationId = :stationId AND cs.finished = false " +
         "AND cs.startTime >= :startTime AND cs.startTime <= :endTime")
  List<ChargingSession> findUnfinishedSessionsByStationInTimeRange(
      @Param("stationId") Long stationId,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime
  );

  @Query("SELECT cs FROM ChargingSession cs WHERE cs.userId = :userId AND cs.finished = true " +
         "AND cs.startTime >= :startDate AND cs.startTime <= :endDate")
  List<ChargingSession> findFinishedSessionsByUserInPeriod(
      @Param("userId") String userId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate
  );

  @Query("SELECT cs FROM ChargingSession cs WHERE cs.userId = :userId " +
         "ORDER BY cs.startTime DESC")
  List<ChargingSession> findRecentSessionsByUser(@Param("userId") String userId);
} 

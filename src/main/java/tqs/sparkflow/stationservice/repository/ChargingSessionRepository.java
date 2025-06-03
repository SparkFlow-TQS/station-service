package tqs.sparkflow.stationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tqs.sparkflow.stationservice.model.ChargingSession;

@Repository
public interface ChargingSessionRepository extends JpaRepository<ChargingSession, Long> {
    // Custom query methods will be added as needed
} 
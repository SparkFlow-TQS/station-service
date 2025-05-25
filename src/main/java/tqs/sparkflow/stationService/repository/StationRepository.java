package tqs.sparkflow.stationService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tqs.sparkflow.stationService.model.Station;
import java.util.List;

@Repository
public interface StationRepository extends JpaRepository<Station, String> {
    List<Station> findByConnectorType(String connectorType);
} 
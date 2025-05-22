package tqs.sparkflow.station_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.sparkflow.station_service.model.Station;
import tqs.sparkflow.station_service.repository.StationRepository;
import java.util.List;
import java.util.Objects;

@Service
public class StationService {

    private final StationRepository stationRepository;

    @Autowired
    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    public Station getStationById(String id) {
        Objects.requireNonNull(id, "Station ID cannot be null");
        return stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found with id: " + id));
    }

    public List<Station> getNearbyStations(double latitude, double longitude, int radius) {
        validateCoordinates(latitude, longitude);
        validateRadius(radius);
        // TODO: Implement nearby stations search using spatial queries
        // For now, return all stations
        return stationRepository.findAll();
    }

    public List<Station> getStationsByConnectorType(String connectorType) {
        Objects.requireNonNull(connectorType, "Connector type cannot be null");
        if (connectorType.trim().isEmpty()) {
            throw new IllegalArgumentException("Connector type cannot be empty");
        }
        return stationRepository.findByConnectorType(connectorType);
    }

    private void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }
    }

    private void validateRadius(int radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be greater than 0");
        }
        if (radius > 100) {
            throw new IllegalArgumentException("Radius cannot be greater than 100 km");
        }
    }
} 
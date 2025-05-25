package tqs.sparkflow.station_service.service;

import org.springframework.stereotype.Service;
import tqs.sparkflow.station_service.model.Station;
import tqs.sparkflow.station_service.repository.StationRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationRepository stationRepository;

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
        return stationRepository.findAll().stream()
                .filter(station -> {
                    double lat = station.getLatitude();
                    double lon = station.getLongitude();
                    return calculateDistance(latitude, longitude, lat, lon) <= radius;
                })
                .collect(Collectors.toList());
    }

    public List<Station> getStationsByConnectorType(String connectorType) {
        Objects.requireNonNull(connectorType, "Connector type cannot be null");
        if (connectorType.trim().isEmpty()) {
            throw new IllegalArgumentException("Connector type cannot be empty");
        }
        return stationRepository.findByConnectorType(connectorType);
    }

    public Station createStation(Station station) {
        Objects.requireNonNull(station, "Station cannot be null");
        validateStation(station);
        return stationRepository.save(station);
    }

    public void deleteStation(String id) {
        Objects.requireNonNull(id, "Station ID cannot be null");
        if (!stationRepository.existsById(id)) {
            throw new RuntimeException("Station not found with id: " + id);
        }
        stationRepository.deleteById(id);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
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

    private void validateStation(Station station) {
        if (station.getName() == null || station.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Station name cannot be empty");
        }
        validateCoordinates(station.getLatitude(), station.getLongitude());
        if (station.getConnectorType() == null || station.getConnectorType().trim().isEmpty()) {
            throw new IllegalArgumentException("Connector type cannot be empty");
        }
    }
} 
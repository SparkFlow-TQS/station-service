package tqs.sparkflow.station_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.sparkflow.station_service.model.Station;
import tqs.sparkflow.station_service.repository.StationRepository;
import java.util.List;

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
        return stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found with id: " + id));
    }

    public List<Station> getNearbyStations(double latitude, double longitude, int radius) {
        // TODO: Implement nearby stations search using spatial queries
        // For now, return all stations
        return stationRepository.findAll();
    }
} 
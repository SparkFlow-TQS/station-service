package tqs.sparkflow.station_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import tqs.sparkflow.station_service.model.Station;
import tqs.sparkflow.station_service.repository.StationRepository;

@Service
public class OpenChargeMapService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl = "https://api.openchargemap.io/v3/poi";
    private final StationRepository stationRepository;

    @Autowired
    public OpenChargeMapService(
            @Value("${openchargemap.api.key:#{null}}") String apiKey,
            StationRepository stationRepository) {
        this.restTemplate = new RestTemplate();
        this.stationRepository = stationRepository;
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("Open Charge Map API key not found. Please set the OPENCHARGEMAP_API_KEY environment variable.");
        }
        this.apiKey = apiKey;
    }

    public String populateStations(double latitude, double longitude, int radius) {
        try {
            List<Map<String, Object>> stationsData = getStationsFromAPI(latitude, longitude, radius);
            if (stationsData == null || stationsData.isEmpty()) {
                throw new IllegalStateException("No stations found in the specified area");
            }
            List<Station> stations = convertToStations(stationsData);
            stationRepository.saveAll(stations);
            return "Successfully populated " + stations.size() + " stations";
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new IllegalStateException("Invalid Open Charge Map API key");
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new IllegalStateException("Access denied to Open Charge Map API");
            }
            throw new IllegalStateException("Error accessing Open Charge Map API: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalStateException("Error populating stations: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> getStationsFromAPI(double latitude, double longitude, int radius) {
        String url = String.format("%s?key=%s&latitude=%f&longitude=%f&distance=%d&distanceunit=KM&maxresults=100",
                baseUrl, apiKey, latitude, longitude, radius);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        return response.getBody();
    }

    private List<Station> convertToStations(List<Map<String, Object>> stationsData) {
        return stationsData.stream()
            .map(data -> {
                try {
                    Map<String, Object> addressInfo = (Map<String, Object>) data.get("AddressInfo");
                    List<Map<String, Object>> connections = (List<Map<String, Object>>) data.get("Connections");
                    
                    Station station = new Station();
                    
                    // Handle ID which could be Integer or String
                    Object id = data.get("ID");
                    station.setId(id != null ? id.toString() : null);
                    
                    // Handle name which could be null
                    Object name = addressInfo.get("Title");
                    station.setName(name != null ? name.toString() : "Unknown");
                    
                    // Handle address which could be null
                    Object address = addressInfo != null ? addressInfo.get("AddressLine1") : null;
                    station.setAddress(address != null ? address.toString() : "Unknown");
                    
                    // Handle coordinates which could be Double
                    Object lat = addressInfo != null ? addressInfo.get("Latitude") : null;
                    Object lon = addressInfo != null ? addressInfo.get("Longitude") : null;
                    station.setLatitude(lat != null ? lat.toString() : "0.0");
                    station.setLongitude(lon != null ? lon.toString() : "0.0");
                    
                    station.setStatus("Available");
                    
                    // Handle connector type which could be null
                    if (connections != null && !connections.isEmpty()) {
                        Map<String, Object> firstConnection = connections.get(0);
                        Object connectorType = firstConnection.get("ConnectionTypeID");
                        station.setConnectorType(connectorType != null ? connectorType.toString() : "Unknown");
                    } else {
                        station.setConnectorType("Unknown");
                    }
                    
                    return station;
                } catch (Exception e) {
                    throw new IllegalStateException("Error converting station data: " + e.getMessage());
                }
            })
            .toList();
    }

  public String getStation(String id) {
    return "Hello, World!";
  }

  public String createStation(Station station) {
    return "Hello, World!";
  }

  public String deleteStation(String id) {
    return "Hello, World!";
  }

  public String updateStation(String id, Station station) {
    return "Hello, World!";
  }

  public String searchStations(String query) {
    return "Hello, World!";
  }

  public String getNearbyStations(String latitude, String longitude) {
    return "Hello, World!";
  }
}

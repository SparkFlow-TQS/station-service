package tqs.sparkflow.stationservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tqs.sparkflow.stationservice.model.OpenChargeMapResponse;
import tqs.sparkflow.stationservice.model.OpenChargeMapStation;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.StationRepository;

/** Service for interacting with the OpenChargeMap API. */
@Service
public class OpenChargeMapService {

  private static final String UNKNOWN_VALUE = "Unknown";
  private final RestTemplate restTemplate;
  private final StationRepository stationRepository;
  private final String apiKey;
  private final String baseUrl;

  /**
   * Creates a new instance of OpenChargeMapService.
   *
   * @param restTemplate The RestTemplate for making HTTP requests
   * @param stationRepository The repository for station operations
   * @param apiKey The OpenChargeMap API key
   * @param baseUrl The base URL for the OpenChargeMap API
   */
  public OpenChargeMapService(RestTemplate restTemplate, StationRepository stationRepository,
      @Value("${openchargemap.api.key}") String apiKey,
      @Value("${openchargemap.api.url}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.stationRepository = stationRepository;
    this.apiKey = apiKey;
    this.baseUrl = baseUrl;
  }

  /**
   * Gets stations by city from OpenChargeMap API.
   *
   * @param city The city to search for
   * @return List of stations in the city
   */
  public List<Station> getStationsByCity(String city) {
    try {
      String url = String.format("%s?key=%s&city=%s", baseUrl, apiKey, city);
      OpenChargeMapResponse response = restTemplate.getForObject(url, OpenChargeMapResponse.class);
      List<Station> stations = new ArrayList<>();
      if (response != null && response.getStations() != null) {
        for (OpenChargeMapStation ocmStation : response.getStations()) {
          if (ocmStation != null) {
            Station station = convertToStation(ocmStation);
            stations.add(station);
          }
        }
      }
      return stations;
    } catch (HttpClientErrorException e) {
      throw new IllegalStateException("Error accessing Open Charge Map API: " + e.getMessage());
    } catch (Exception e) {
      throw new IllegalStateException("Error fetching stations: " + e.getMessage());
    }
  }

  /**
   * Populates the database with stations from OpenChargeMap API.
   *
   * @param latitude The latitude coordinate
   * @param longitude The longitude coordinate
   * @param radius The search radius in kilometers
   * @return List of populated stations
   */
  public List<Station> populateStations(double latitude, double longitude, int radius) {
    // Validate coordinates
    if (latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
    }
    if (longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
    }
    if (radius <= 0) {
      throw new IllegalArgumentException("Radius must be positive");
    }

    try {
      String url = String.format("%s?key=%s&latitude=%f&longitude=%f&distance=%d", baseUrl, apiKey,
          latitude, longitude, radius);
      ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(url,
          HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String, Object>>>() {});

      List<Map<String, Object>> responseBody = response.getBody();
      if (responseBody == null || responseBody.isEmpty()) {
        throw new IllegalStateException("No stations found");
      }

      List<Station> stations = convertToStations(responseBody);
      return stationRepository.saveAll(stations);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        throw new IllegalStateException("Invalid Open Charge Map API key");
      } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
        throw new IllegalStateException("Access denied to Open Charge Map API");
      }
      throw new IllegalStateException("Error accessing Open Charge Map API: " + e.getMessage());
    } catch (Exception e) {
      throw new IllegalStateException("Error fetching stations: " + e.getMessage());
    }
  }

  /**
   * Converts an OpenChargeMap station to our Station model.
   *
   * @param ocmStation The OpenChargeMap station
   * @return The converted Station
   */
  private Station convertToStation(OpenChargeMapStation ocmStation) {
    if (ocmStation == null) {
      return null;
    }
    Station station = new Station();
    station.setExternalId(ocmStation.getId());
    station.setName(ocmStation.getName());
    station.setAddress(ocmStation.getAddress());
    station.setCity(ocmStation.getCity());
    station.setCountry(ocmStation.getCountry());
    station.setLatitude(ocmStation.getLatitude());
    station.setLongitude(ocmStation.getLongitude());
    station.setQuantityOfChargers(ocmStation.calculateQuantityOfChargers());
    station.setStatus(ocmStation.getStatus());
    station.setPower(ocmStation.getPower());
    station.setIsOperational(true); // Default to operational
    return station;
  }

  private List<Station> convertToStations(List<Map<String, Object>> stationsData) {
    return stationsData.stream().map(this::convertMapToStation).toList();
  }

  protected Station convertMapToStation(Map<String, Object> data) {
    try {
      Map<String, Object> addressInfo = getAddressInfo(data);
      List<Map<String, Object>> connections = getConnections(data);

      Station station = new Station();
      setStationId(data, station);
      setStationName(addressInfo, station);
      setStationAddress(addressInfo, station);
      setStationCoordinates(addressInfo, station);
      setStationCity(addressInfo, station);
      setStationCountry(addressInfo, station);
      station.setStatus("Available");
      setStationQuantityOfChargers(connections, station);

      return station;
    } catch (Exception e) {
      throw new IllegalStateException("Error converting station data: " + e.getMessage());
    }
  }

  private Map<String, Object> getAddressInfo(Map<String, Object> data) {
    Object addressInfo = data.get("AddressInfo");
    if (!(addressInfo instanceof Map)) {
      throw new IllegalStateException("AddressInfo is not a valid map");
    }
    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) addressInfo;
    return result;
  }

  private List<Map<String, Object>> getConnections(Map<String, Object> data) {
    Object connections = data.get("Connections");
    if (!(connections instanceof List)) {
      throw new IllegalStateException("Connections is not a valid list");
    }
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> result = (List<Map<String, Object>>) connections;
    return result;
  }

  private void setStationId(Map<String, Object> data, Station station) {
    Object id = data.get("ID");
    if (id instanceof Number) {
      station.setId(((Number) id).longValue());
    } else if (id != null) {
      station.setId(Long.parseLong(id.toString()));
    }
  }

  private void setStationName(Map<String, Object> addressInfo, Station station) {
    Object name = addressInfo != null ? addressInfo.get("Title") : null;
    station.setName(name != null ? name.toString() : UNKNOWN_VALUE);
  }

  private void setStationAddress(Map<String, Object> addressInfo, Station station) {
    Object address = addressInfo != null ? addressInfo.get("AddressLine1") : null;
    station.setAddress(address != null ? address.toString() : UNKNOWN_VALUE);
  }

  private void setStationCoordinates(Map<String, Object> addressInfo, Station station) {
    Object lat = addressInfo != null ? addressInfo.get("Latitude") : null;
    Object lon = addressInfo != null ? addressInfo.get("Longitude") : null;
    station.setLatitude(lat != null ? ((Number) lat).doubleValue() : 0.0);
    station.setLongitude(lon != null ? ((Number) lon).doubleValue() : 0.0);
  }

  private void setStationCity(Map<String, Object> addressInfo, Station station) {
    Object city = addressInfo != null ? addressInfo.get("Town") : null;
    station.setCity(city != null ? city.toString() : UNKNOWN_VALUE);
  }

  private void setStationCountry(Map<String, Object> addressInfo, Station station) {
    Object country = addressInfo != null ? addressInfo.get("Country") : null;
    station.setCountry(country != null ? country.toString() : UNKNOWN_VALUE);
  }

  private void setStationQuantityOfChargers(List<Map<String, Object>> connections,
      Station station) {
    if (connections == null || connections.isEmpty()) {
      station.setQuantityOfChargers(1); // Default to 1 if no connections
      return;
    }

    int totalChargers = 0;
    for (Map<String, Object> connection : connections) {
      // Get quantity from connection
      Object quantity = connection.get("Quantity");
      if (quantity != null) {
        if (quantity instanceof Number) {
          totalChargers += ((Number) quantity).intValue();
        } else if (quantity instanceof String) {
          try {
            totalChargers += Integer.parseInt((String) quantity);
          } catch (NumberFormatException e) {
            // If parsing fails, count as 1
            totalChargers += 1;
          }
        }
      } else {
        // If quantity is null, count as 1
        totalChargers += 1;
      }
    }

    // Ensure at least 1 charger
    station.setQuantityOfChargers(totalChargers > 0 ? totalChargers : 1);
  }
}

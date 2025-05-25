package tqs.sparkflow.stationservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  public OpenChargeMapService(
      RestTemplate restTemplate,
      StationRepository stationRepository,
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
    String url = String.format("%s?key=%s&city=%s", baseUrl, apiKey, city);
    OpenChargeMapResponse response = restTemplate.getForObject(url, OpenChargeMapResponse.class);
    List<Station> stations = new ArrayList<>();
    if (response != null && response.getStations() != null) {
      for (OpenChargeMapStation ocmStation : response.getStations()) {
        Station station = convertToStation(ocmStation);
        if (station != null) {
          stations.add(station);
        }
      }
    }
    return stations;
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
      ResponseEntity<List<Map<String, Object>>> response =
          restTemplate.exchange(
              String.format(
                  "%s?key=%s&latitude=%f&longitude=%f&distance=%d",
                  baseUrl, apiKey, latitude, longitude, radius),
              HttpMethod.GET,
              null,
              new ParameterizedTypeReference<List<Map<String, Object>>>() {});

      if (response == null || response.getBody() == null || response.getBody().isEmpty()) {
        throw new IllegalStateException("No stations found");
      }

      List<Station> stations = convertToStations(response.getBody());
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
    return new Station(
        ocmStation.getId(),
        ocmStation.getName(),
        ocmStation.getAddress(),
        ocmStation.getCity(),
        ocmStation.getCountry(),
        ocmStation.getLatitude(),
        ocmStation.getLongitude(),
        ocmStation.getConnectorType(),
        null,
        true);
  }

  @SuppressWarnings("unchecked")
  private List<Station> convertToStations(List<Map<String, Object>> stationsData) {
    return stationsData.stream()
        .map(
            data -> {
              try {
                Map<String, Object> addressInfo = (Map<String, Object>) data.get("AddressInfo");
                List<Map<String, Object>> connections =
                    (List<Map<String, Object>>) data.get("Connections");

                Station station = new Station();

                // Handle ID which could be Integer or String
                Object id = data.get("ID");
                if (id != null) {
                  if (id instanceof Number) {
                    station.setId(((Number) id).longValue());
                  } else {
                    station.setId(Long.parseLong(id.toString()));
                  }
                }

                // Handle name which could be null
                Object name = addressInfo.get("Title");
                station.setName(name != null ? name.toString() : UNKNOWN_VALUE);

                // Handle address which could be null
                Object address = addressInfo != null ? addressInfo.get("AddressLine1") : null;
                station.setAddress(address != null ? address.toString() : UNKNOWN_VALUE);

                // Handle coordinates which could be Double
                Object lat = addressInfo != null ? addressInfo.get("Latitude") : null;
                Object lon = addressInfo != null ? addressInfo.get("Longitude") : null;
                station.setLatitude(lat != null ? ((Number) lat).doubleValue() : 0.0);
                station.setLongitude(lon != null ? ((Number) lon).doubleValue() : 0.0);

                station.setStatus("Available");

                // Handle connector type which could be null
                if (connections != null && !connections.isEmpty()) {
                  Map<String, Object> firstConnection = connections.get(0);
                  Object connectorType = firstConnection.get("ConnectionTypeID");
                  station.setConnectorType(
                      connectorType != null ? connectorType.toString() : UNKNOWN_VALUE);
                } else {
                  station.setConnectorType(UNKNOWN_VALUE);
                }

                return station;
              } catch (Exception e) {
                throw new IllegalStateException("Error converting station data: " + e.getMessage());
              }
            })
        .toList();
  }
}

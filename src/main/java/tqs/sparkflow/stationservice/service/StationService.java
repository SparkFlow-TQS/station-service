package tqs.sparkflow.stationservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import tqs.sparkflow.stationservice.dto.StationFilterDTO;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.BookingRepository;
import tqs.sparkflow.stationservice.repository.ChargingSessionRepository;
import tqs.sparkflow.stationservice.repository.StationRepository;

/** Service for managing charging stations. */
@Service
public class StationService {

  private final StationRepository stationRepository;
  private final BookingRepository bookingRepository;
  private final ChargingSessionRepository chargingSessionRepository;

  // Maximum number of stations to return per search to prevent performance issues
  private static final int MAX_SEARCH_RESULTS = 500;

  public StationService(StationRepository stationRepository, BookingRepository bookingRepository, ChargingSessionRepository chargingSessionRepository) {
    this.stationRepository = stationRepository;
    this.bookingRepository = bookingRepository;
    this.chargingSessionRepository = chargingSessionRepository;
  }

  /**
   * Gets all stations.
   *
   * @return List of all stations (limited to first 500 for performance)
   */
  public List<Station> getAllStations() {
    List<Station> allStations = stationRepository.findAll();
    return allStations.size() > MAX_SEARCH_RESULTS ? 
           allStations.subList(0, MAX_SEARCH_RESULTS) : allStations;
  }

  /**
   * Gets the total count of stations in the database.
   *
   * @return Total number of stations
   */
  public Long getTotalStationCount() {
    return stationRepository.count();
  }

  /**
   * Gets a station by ID.
   *
   * @param id The station ID
   * @return The station if found
   * @throws IllegalArgumentException if station not found
   * @throws NullPointerException if id is null
   */
  public Station getStationById(Long id) {
    if (id == null) {
      throw new NullPointerException("Station ID cannot be null");
    }
    return stationRepository
      .findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Station not found with id: " + id));
  }

  /**
   * Retrieves a station by its external ID.
   *
   * @param externalId the external ID of the station to retrieve
   * @return the station with the given external ID
   * @throws IllegalArgumentException if no station exists with the given external ID
   */
  public Station getStationByExternalId(String externalId) {
    return stationRepository
      .findByExternalId(externalId)
      .orElseThrow(
        () ->
          new IllegalArgumentException("Station not found with external id: " + externalId));
  }

  /**
   * Creates a new station.
   *
   * @param station The station to create
   * @return The created station
   * @throws NullPointerException if station is null
   * @throws IllegalArgumentException if station has invalid data
   */
  public Station createStation(Station station) {
    if (station == null) {
      throw new NullPointerException("Station cannot be null");
    }
    if (station.getName() == null || station.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Station name cannot be empty");
    }
    if (station.getQuantityOfChargers() == null || station.getQuantityOfChargers() < 1) {
      throw new IllegalArgumentException("Quantity of chargers must be at least 1");
    }
    if (station.getLatitude() != null
        && (station.getLatitude() < -90 || station.getLatitude() > 90)) {
      throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
    }
    if (station.getLongitude() != null
        && (station.getLongitude() < -180 || station.getLongitude() > 180)) {
      throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
    }
    return stationRepository.save(station);
  }

  /**
   * Updates an existing station.
   *
   * @param id The station ID
   * @param station The updated station data
   * @return The updated station
   * @throws IllegalArgumentException if station not found
   */
  public Station updateStation(Long id, Station station) {
    if (!stationRepository.existsById(id)) {
      throw new IllegalArgumentException("Station not found with id: " + id);
    }
    station.setId(id);
    return stationRepository.save(station);
  }

  /**
   * Deletes a station.
   *
   * @param id The station ID
   * @throws IllegalArgumentException if station not found
   * @throws NullPointerException if id is null
   */
  public void deleteStation(Long id) {
    if (id == null) {
      throw new NullPointerException("Station ID cannot be null");
    }
    if (!stationRepository.existsById(id)) {
      throw new IllegalArgumentException("Station not found with id: " + id);
    }
    stationRepository.deleteById(id);
  }

  /**
   * Searches for stations based on criteria.
   *
   * @param name The station name
   * @param city The city name
   * @param country The country name
   * @param minChargers The minimum number of chargers
   * @return List of matching stations
   */
  public List<Station> searchStations(
      String name, String city, String country, Integer minChargers) {
        List<Station> allStations = stationRepository.findAll();
    
    List<Station> filteredStations = allStations.stream()
        .filter(station -> {
            // Name filter
            if (name != null && !name.trim().isEmpty()) {
                if (station.getName() == null || 
                    !station.getName().toLowerCase().contains(name.toLowerCase())) {
                    return false;
                }
            }
            
            // City filter
            if (city != null && !city.trim().isEmpty()) {
                if (station.getCity() == null || 
                    !station.getCity().toLowerCase().contains(city.toLowerCase())) {
                    return false;
                }
            }
            
            // Country filter
            if (country != null && !country.trim().isEmpty()) {
                if (station.getCountry() == null || 
                    !station.getCountry().toLowerCase().contains(country.toLowerCase())) {
                    return false;
                }
            }
            
            // Minimum chargers filter
            if (minChargers != null && minChargers > 0) {
                if (station.getQuantityOfChargers() == null || 
                    station.getQuantityOfChargers() < minChargers) {
                    return false;
                }
            }
            
            return true;
        })
        .limit(MAX_SEARCH_RESULTS)  // Limit to maximum results
        .toList();
    
    return filteredStations;

  }

  /**
   * Gets stations within a radius of the given coordinates.
   *
   * @param latitude The latitude coordinate
   * @param longitude The longitude coordinate
   * @param radius The search radius in kilometers
   * @return List of stations within the radius (limited to 500 results)
   * @throws IllegalArgumentException if coordinates or radius are invalid
   */
  public List<Station> getNearbyStations(double latitude, double longitude, int radius) {
    if (latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
    }
    if (longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
    }
    if (radius <= 0) {
      throw new IllegalArgumentException("Radius must be greater than 0 km");
    }
    if (radius > 600) {
      throw new IllegalArgumentException("Radius cannot be greater than 600 km");
    }
    
    return stationRepository.findAll().stream()
      .filter(station -> {
        if (station.getLatitude() == null || station.getLongitude() == null) {
          return false;
        }
        double distance = calculateDistance(
            latitude, longitude,
            station.getLatitude(), station.getLongitude()
        );
        return distance <= radius;
      })
      .limit(MAX_SEARCH_RESULTS)  // Limit to maximum results
      .toList();
  }

  /**
   * Calculates the distance between two points using the Haversine formula.
   * @param lat1 Latitude of first point
   * @param lon1 Longitude of first point
   * @param lat2 Latitude of second point
   * @param lon2 Longitude of second point
   * @return Distance in kilometers
   */
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

  /**
   * Gets stations by minimum number of chargers.
   *
   * @param minChargers The minimum number of chargers to search for
   * @return List of stations with at least the given number of chargers
   * @throws NullPointerException if minChargers is null
   * @throws IllegalArgumentException if minChargers is less than 1
   */
  public List<Station> getStationsByMinChargers(Integer minChargers) {
    if (minChargers == null) {
      throw new NullPointerException("Minimum number of chargers cannot be null");
    }
    if (minChargers < 1) {
      throw new IllegalArgumentException("Minimum number of chargers must be at least 1");
    }
    return stationRepository.findByQuantityOfChargersGreaterThanEqual(minChargers);
  }

  /**
   * Gets stations based on filter criteria.
   *
   * @param filter The filter criteria
   * @return List of stations matching the filter criteria
   */
  public List<Station> getStationsByFilters(StationFilterDTO filter) {
    if (filter.getLatitude() != null && filter.getLongitude() != null && filter.getRadius() != null) {
      return stationRepository.findStationsByFiltersWithLocation(
          filter.getMinPower(),
          filter.getMaxPower(),
          filter.getIsOperational(),
          filter.getStatus(),
          filter.getCity(),
          filter.getCountry(),
          filter.getMinPrice(),
          filter.getMaxPrice(),
          filter.getLatitude(),
          filter.getLongitude(),
          filter.getRadius()
      );
    } else {
      return stationRepository.findStationsByFilters(
          filter.getMinPower(),
          filter.getMaxPower(),
          filter.getIsOperational(),
          filter.getStatus(),
          filter.getCity(),
          filter.getCountry(),
          filter.getMinPrice(),
          filter.getMaxPrice()
      );
    }
  }

  /**
   * Calculates the number of available chargers at a station at the current time.
   *
   * @param stationId The station ID
   * @param currentTime The current time
   * @return Number of available chargers
   */
  public int getAvailableChargers(Long stationId, LocalDateTime currentTime) {
    Station station = getStationById(stationId);
    int totalChargers = station.getQuantityOfChargers();
    
    List<Booking> activeBookings = bookingRepository.findActiveBookingsForStationAtTime(stationId, currentTime);
    List<ChargingSession> unfinishedSessions = chargingSessionRepository.findUnfinishedSessionsByStation(stationId);
    
    return totalChargers - activeBookings.size() - unfinishedSessions.size();
  }

  /**
   * Checks if a user can use a station for a given time period.
   *
   * @param stationId The station ID
   * @param userId The user ID
   * @param startTime The start time
   * @param endTime The end time
   * @return true if the user can use the station, false otherwise
   */
  public boolean canUseStation(Long stationId, Long userId, LocalDateTime startTime, LocalDateTime endTime) {
    List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(stationId, startTime, endTime);
    
    boolean hasUserBooking = overlappingBookings.stream()
        .anyMatch(booking -> booking.getUserId().equals(userId));
    
    if (hasUserBooking) {
      return true;
    }
    
    Station station = getStationById(stationId);
    int totalChargers = station.getQuantityOfChargers();
    
    List<ChargingSession> unfinishedSessions = chargingSessionRepository.findUnfinishedSessionsByStationInTimeRange(stationId, startTime, endTime);
    
    int usedChargers = overlappingBookings.size() + unfinishedSessions.size();
    
    return usedChargers < totalChargers;
  }

  /**
   * Validates if a booking can be made for the given parameters.
   *
   * @param stationId The station ID
   * @param userId The user ID
   * @param startTime The start time
   * @param endTime The end time
   * @throws IllegalStateException if no chargers are available
   */
  public void validateBooking(Long stationId, Long userId, LocalDateTime startTime, LocalDateTime endTime) {
    if (!canUseStation(stationId, userId, startTime, endTime)) {
      throw new IllegalStateException("No chargers available for the requested time slot");
    }
  }

  /**
   * Checks if a user can start a charging session immediately.
   * A user can start a session if:
   * 1. They have an active booking for this station at the current time, OR
   * 2. There are free chargers available (total - active bookings without sessions - unfinished sessions)
   *
   * @param stationId The station ID
   * @param userId The user ID
   * @return true if the user can start a session, false otherwise
   */
  public boolean canStartSession(Long stationId, Long userId) {
    LocalDateTime now = LocalDateTime.now();
    
    // Check if user has an active booking for this station at current time
    List<Booking> activeBookings = bookingRepository.findActiveBookingsForStationAtTime(stationId, now);
    boolean hasUserBooking = activeBookings.stream()
        .anyMatch(booking -> booking.getUserId().equals(userId));
    
    if (hasUserBooking) {
      return true;
    }
    
    // Check if there are free chargers available
    Station station = getStationById(stationId);
    int totalChargers = station.getQuantityOfChargers();
    
    // Count bookings where owner hasn't started a session yet
    long bookingsWithoutSessions = activeBookings.stream()
        .filter(booking -> !hasActiveSessionForBooking(booking))
        .count();
    
    // Count unfinished sessions
    List<ChargingSession> unfinishedSessions = chargingSessionRepository.findUnfinishedSessionsByStation(stationId);
    
    int usedChargers = (int) bookingsWithoutSessions + unfinishedSessions.size();
    
    return usedChargers < totalChargers;
  }

  /**
   * Checks if a booking owner has an active (unfinished) session at the station.
   *
   * @param booking The booking to check
   * @return true if the booking owner has an active session, false otherwise
   */
  private boolean hasActiveSessionForBooking(Booking booking) {
    List<ChargingSession> userUnfinishedSessions = chargingSessionRepository.findUnfinishedSessionsByStation(booking.getStationId())
        .stream()
        .filter(session -> session.getUserId().equals(booking.getUserId().toString()))
        .toList();
    
    return !userUnfinishedSessions.isEmpty();
  }

  /**
   * Validates if a user can start a charging session.
   *
   * @param stationId The station ID
   * @param userId The user ID
   * @throws IllegalStateException if the user cannot start a session
   */
  public void validateSessionStart(Long stationId, Long userId) {
    if (!canStartSession(stationId, userId)) {
      throw new IllegalStateException("Cannot start session: no booking or free chargers available");
    }
  }
}

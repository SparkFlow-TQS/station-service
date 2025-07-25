package tqs.sparkflow.stationservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.BookingRepository;
import tqs.sparkflow.stationservice.repository.StationRepository;

@Service
public class BookingServiceImpl implements BookingService {
  @Value("${api.paths.users}")
  private String usersPath;

  private final BookingRepository bookingRepository;
  private final StationRepository stationRepository;
  private final RestTemplate restTemplate;
  private final String userServiceUrl;
  private static final String ADMIN_ROLE_CHECK = "/has-role/ADMIN";

  /**
   * Creates a new instance of BookingServiceImpl.
   *
   * @param bookingRepository The repository for booking operations
   * @param stationRepository The repository for station operations
   * @param restTemplate The RestTemplate for making HTTP requests
   * @param userServiceUrl The URL of the user service
   */
  public BookingServiceImpl(BookingRepository bookingRepository,
      StationRepository stationRepository, RestTemplate restTemplate, String userServiceUrl) {
    this.bookingRepository = bookingRepository;
    this.stationRepository = stationRepository;
    this.restTemplate = restTemplate;
    this.userServiceUrl = userServiceUrl;
  }

  private void validateUser(Long userId) {
    try {
      restTemplate.getForObject(userServiceUrl + usersPath + userId, Object.class);
    } catch (Exception e) {
      throw new IllegalStateException("User not found or not authorized");
    }
  }

  private void validateUserPermission(Long userId, Long bookingUserId) {
    if (!userId.equals(bookingUserId)) {
      try {
        restTemplate.getForObject(userServiceUrl + usersPath + userId + ADMIN_ROLE_CHECK,
            Boolean.class);
      } catch (Exception e) {
        throw new IllegalStateException("User not authorized to access this booking");
      }
    }
  }

  @Override
  public Booking createRecurringBooking(Long userId, Long stationId, LocalDateTime startTime,
      LocalDateTime endTime, Set<Integer> recurringDays) {
    validateUser(userId);

    Station station = stationRepository.findById(stationId)
        .orElseThrow(() -> new IllegalStateException("Station not found"));
    if (Boolean.FALSE.equals(station.getIsOperational())) {
      throw new IllegalStateException("Station is not operational");
    }

    // Check for overlapping bookings
    List<Booking> overlappingBookings =
        bookingRepository.findOverlappingBookings(stationId, startTime, endTime);

    if (!overlappingBookings.isEmpty()) {
      throw new IllegalStateException("There are overlapping bookings for this time slot");
    }

    Booking booking = new Booking();
    booking.setUserId(userId);
    booking.setStationId(stationId);
    booking.setStartTime(startTime);
    booking.setEndTime(endTime);
    booking.setRecurringDays(recurringDays);
    booking.setStatus(BookingStatus.ACTIVE);

    return bookingRepository.save(booking);
  }

  @Override
  public Booking createBooking(Booking booking) {
    validateUser(booking.getUserId());
    return createRecurringBooking(booking.getUserId(), booking.getStationId(),
        booking.getStartTime(), booking.getEndTime(), booking.getRecurringDays());
  }

  @Override
  public Optional<Booking> getBookingById(Long id, Long requestingUserId) {
    Optional<Booking> booking = bookingRepository.findById(id);
    if (booking.isPresent()) {
      validateUserPermission(requestingUserId, booking.get().getUserId());
    }
    return booking;
  }

  @Override
  public List<Booking> getAllBookings(Long userId) {
    validateUser(userId);
    try {
      restTemplate.getForObject(userServiceUrl + usersPath + userId + ADMIN_ROLE_CHECK,
          Boolean.class);
    } catch (Exception e) {
      throw new IllegalStateException("User not authorized to access all bookings");
    }
    return bookingRepository.findAll();
  }

  @Override
  public Booking cancelBooking(Long id) {
    Optional<Booking> bookingOpt = bookingRepository.findById(id);
    if (bookingOpt.isEmpty()) {
      throw new IllegalStateException("Booking not found");
    }

    Booking booking = bookingOpt.get();
    validateUserPermission(booking.getUserId(), booking.getUserId());

    booking.setStatus(BookingStatus.CANCELLED);
    return bookingRepository.save(booking);
  }

  @Override
  public List<Booking> getBookingsByStationId(Long stationId, Long requestingUserId) {
    validateUser(requestingUserId);
    try {
      restTemplate.getForObject(userServiceUrl + usersPath + requestingUserId + ADMIN_ROLE_CHECK,
          Boolean.class);
    } catch (Exception e) {
      // If not admin, only return bookings for this user
      return bookingRepository.findByStationIdAndUserId(stationId, requestingUserId);
    }
    return bookingRepository.findByStationId(stationId);
  }

  @Override
  public List<Booking> getBookingsByUserId(Long userId) {
    validateUser(userId);
    return bookingRepository.findByUserId(userId);
  }
}

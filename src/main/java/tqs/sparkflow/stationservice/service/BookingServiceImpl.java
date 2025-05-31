package tqs.sparkflow.stationservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.BookingRepository;

@Service
public class BookingServiceImpl implements BookingService {
  private final BookingRepository bookingRepository;
  private final StationService stationService;
  private final RestTemplate restTemplate;
  private final String userServiceUrl;

  /**
   * Creates a new instance of BookingServiceImpl.
   *
   * @param bookingRepository The repository for booking operations
   * @param stationService The service for station operations
   * @param restTemplate The RestTemplate for making HTTP requests
   * @param userServiceUrl The URL of the user service
  */
  public BookingServiceImpl(
      BookingRepository bookingRepository,
      StationService stationService,
      RestTemplate restTemplate,
      String userServiceUrl) {
    this.bookingRepository = bookingRepository;
    this.stationService = stationService;
    this.restTemplate = restTemplate;
    this.userServiceUrl = userServiceUrl;
  }

  private void validateUser(Long userId) {
    try {
      restTemplate.getForObject(userServiceUrl + "/users/" + userId, Object.class);
    } catch (Exception e) {
      throw new IllegalStateException("User not found or not authorized");
    }
  }

  private void validateUserPermission(Long userId, Long bookingUserId) {
    if (!userId.equals(bookingUserId)) {
      try {
        restTemplate.getForObject(
            userServiceUrl 
            + "/users/" 
            + userId 
            + "/has-role/ADMIN",
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

    Station station = stationService.getStationById(stationId);
    if (Boolean.FALSE.equals(station.getIsOperational())) {
      throw new IllegalStateException("Station is not operational");
    }

    List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                                                            stationId, startTime, endTime);
    if (!overlappingBookings.isEmpty()) {
      throw new IllegalStateException("Time slot is already booked");
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
    return createRecurringBooking(
        booking.getUserId(),
        booking.getStationId(),
        booking.getStartTime(),
        booking.getEndTime(),
        booking.getRecurringDays());
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
  public List<Booking> getBookingsByStationId(Long stationId) {
    validateUser(stationId);
    return bookingRepository.findByStationId(stationId);
  }

  @Override
  public List<Booking> getBookingsByUserId(Long userId) {
    validateUser(userId);
    return bookingRepository.findByUserId(userId);
  }
} 
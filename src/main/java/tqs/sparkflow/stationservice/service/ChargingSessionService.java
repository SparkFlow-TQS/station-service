package tqs.sparkflow.stationservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tqs.sparkflow.stationservice.exception.ChargingSessionNotFoundException;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;
import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.repository.BookingRepository;
import tqs.sparkflow.stationservice.repository.ChargingSessionRepository;

/**
 * Service class responsible for managing charging sessions.
 * Handles the lifecycle of a charging session from unlocking a station to completing the charging process.
 */
@Service
public class ChargingSessionService {
  private static final String SESSION_NOT_FOUND_MESSAGE = "Session not found: ";
  private final ChargingSessionRepository chargingSessionRepository;
  private final BookingRepository bookingRepository;
  private final StationService stationService;

  public ChargingSessionService(ChargingSessionRepository chargingSessionRepository, BookingRepository bookingRepository, StationService stationService) {
    this.chargingSessionRepository = chargingSessionRepository;
    this.bookingRepository = bookingRepository;
    this.stationService = stationService;
  }

  /**
   * Creates a new charging session and starts it immediately.
   * Records the start time and closes any active booking for the user.
   * 
   * @param stationId The ID of the station to use
   * @param userId The ID of the user starting the session
   * @return The created charging session
   * @throws IllegalStateException if the user cannot start a session
   */
  @Transactional
  public ChargingSession createSession(String stationId, String userId) {
    // Validate that the user can start a session
    stationService.validateSessionStart(Long.valueOf(stationId), Long.valueOf(userId));
    
    ChargingSession session = new ChargingSession(stationId, userId);
    session = chargingSessionRepository.save(session);
    
    closeUserBooking(session);
    
    return session;
  }


  /**
   * Closes any active booking for the user at the station when a session starts.
   *
   * @param session The charging session that is starting
   */
  private void closeUserBooking(ChargingSession session) {
    Long stationId = Long.valueOf(session.getStationId());
    Long userId = Long.valueOf(session.getUserId());
    LocalDateTime now = LocalDateTime.now();
    
    List<Booking> activeBookings = bookingRepository.findActiveBookingsForStationAtTime(stationId, now);
    
    activeBookings.stream()
        .filter(booking -> booking.getUserId().equals(userId))
        .forEach(booking -> {
          booking.setStatus(BookingStatus.COMPLETED);
          bookingRepository.save(booking);
        });
  }

  /**
   * Ends a charging session.
   * Marks the session as finished and sets the end time.
   * 
   * @param sessionId The ID of the session to end
   * @return The updated charging session
   * @throws ChargingSessionNotFoundException if the session is not found
   */
  @Transactional
  public ChargingSession endSession(String sessionId) {
    ChargingSession session = chargingSessionRepository.findById(Long.valueOf(sessionId))
      .orElseThrow(() -> new ChargingSessionNotFoundException(SESSION_NOT_FOUND_MESSAGE + sessionId));
    
    session.setFinished(true);
    session.setEndTime(LocalDateTime.now());
    return chargingSessionRepository.save(session);
  }


  /**
   * Retrieves a charging session by its ID.
   * 
   * @param sessionId The ID of the session to retrieve
   * @return The charging session
   * @throws ChargingSessionNotFoundException if the session is not found
   */
  public ChargingSession getSession(String sessionId) {
    return chargingSessionRepository.findById(Long.valueOf(sessionId))
      .orElseThrow(() -> new ChargingSessionNotFoundException(SESSION_NOT_FOUND_MESSAGE + sessionId));
  }
} 
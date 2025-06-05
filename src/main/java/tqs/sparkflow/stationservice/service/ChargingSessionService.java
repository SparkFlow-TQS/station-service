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

  public ChargingSessionService(ChargingSessionRepository chargingSessionRepository, BookingRepository bookingRepository) {
    this.chargingSessionRepository = chargingSessionRepository;
    this.bookingRepository = bookingRepository;
  }

  /**
   * Creates a new charging session by unlocking a station.
   * 
   * @param stationId The ID of the station to unlock
   * @param userId The ID of the user requesting the unlock
   * @return The created charging session in UNLOCKED state
   */
  @Transactional
  public ChargingSession unlockStation(String stationId, String userId) {
    ChargingSession session = new ChargingSession();
    session.setStationId(stationId);
    session.setUserId(userId);
    session.setStatus(ChargingSession.ChargingSessionStatus.UNLOCKED);
    return chargingSessionRepository.save(session);
  }

  /**
   * Starts a charging session.
   * Updates the session status to CHARGING and sets the start time.
   * 
   * @param sessionId The ID of the session to start
   * @return The updated charging session
   * @throws ChargingSessionNotFoundException if the session is not found
   */
  @Transactional
  public ChargingSession startCharging(String sessionId) {
    ChargingSession session = chargingSessionRepository.findById(Long.valueOf(sessionId))
      .orElseThrow(() -> new ChargingSessionNotFoundException(SESSION_NOT_FOUND_MESSAGE + sessionId));
    
    session.setStatus(ChargingSession.ChargingSessionStatus.CHARGING);
    session.setStartTime(LocalDateTime.now());
    
    closeUserBooking(session);
    
    return chargingSessionRepository.save(session);
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
   * Updates the session status to COMPLETED and sets the end time.
   * 
   * @param sessionId The ID of the session to end
   * @return The updated charging session
   * @throws ChargingSessionNotFoundException if the session is not found
   */
  @Transactional
  public ChargingSession endCharging(String sessionId) {
    ChargingSession session = chargingSessionRepository.findById(Long.valueOf(sessionId))
      .orElseThrow(() -> new ChargingSessionNotFoundException(SESSION_NOT_FOUND_MESSAGE + sessionId));
    
    session.setStatus(ChargingSession.ChargingSessionStatus.COMPLETED);
    session.setEndTime(LocalDateTime.now());
    return chargingSessionRepository.save(session);
  }

  /**
   * Reports an error for a charging session.
   * Updates the session status to ERROR and records the error message.
   * 
   * @param sessionId The ID of the session to report error for
   * @param errorMessage The error message to record
   * @return The updated charging session
   * @throws ChargingSessionNotFoundException if the session is not found
   */
  @Transactional
  public ChargingSession reportError(String sessionId, String errorMessage) {
    ChargingSession session = chargingSessionRepository.findById(Long.valueOf(sessionId))
      .orElseThrow(() -> new ChargingSessionNotFoundException(SESSION_NOT_FOUND_MESSAGE + sessionId));
    
    session.setStatus(ChargingSession.ChargingSessionStatus.ERROR);
    session.setErrorMessage(errorMessage);
    return chargingSessionRepository.save(session);
  }

  /**
   * Retrieves the current status of a charging session.
   * 
   * @param sessionId The ID of the session to get status for
   * @return The charging session with its current status
   * @throws ChargingSessionNotFoundException if the session is not found
   */
  public ChargingSession getSessionStatus(String sessionId) {
    return chargingSessionRepository.findById(Long.valueOf(sessionId))
      .orElseThrow(() -> new ChargingSessionNotFoundException(SESSION_NOT_FOUND_MESSAGE + sessionId));
  }
} 
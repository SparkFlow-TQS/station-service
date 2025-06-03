package tqs.sparkflow.stationservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tqs.sparkflow.stationservice.exception.ChargingSessionNotFoundException;
import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.repository.ChargingSessionRepository;

@Service
public class ChargingSessionService {
  private static final String SESSION_NOT_FOUND_MESSAGE = "Session not found: ";
  private final ChargingSessionRepository chargingSessionRepository;

  public ChargingSessionService(ChargingSessionRepository chargingSessionRepository) {
    this.chargingSessionRepository = chargingSessionRepository;
  }

  @Transactional
  public ChargingSession unlockStation(String stationId, String userId) {
    ChargingSession session = new ChargingSession();
    session.setStationId(stationId);
    session.setUserId(userId);
    session.setStatus(ChargingSession.ChargingSessionStatus.UNLOCKED);
    return chargingSessionRepository.save(session);
  }

  @Transactional
  public ChargingSession startCharging(String sessionId) {
    ChargingSession session = chargingSessionRepository.findById(Long.valueOf(sessionId))
      .orElseThrow(() -> new ChargingSessionNotFoundException(SESSION_NOT_FOUND_MESSAGE + sessionId));
    
    session.setStatus(ChargingSession.ChargingSessionStatus.CHARGING);
    session.setStartTime(java.time.LocalDateTime.now());
    return chargingSessionRepository.save(session);
  }

  @Transactional
  public ChargingSession endCharging(String sessionId) {
    ChargingSession session = chargingSessionRepository.findById(Long.valueOf(sessionId))
      .orElseThrow(() -> new ChargingSessionNotFoundException(SESSION_NOT_FOUND_MESSAGE + sessionId));
    
    session.setStatus(ChargingSession.ChargingSessionStatus.COMPLETED);
    session.setEndTime(java.time.LocalDateTime.now());
    return chargingSessionRepository.save(session);
  }

  @Transactional
  public ChargingSession reportError(String sessionId, String errorMessage) {
    ChargingSession session = chargingSessionRepository.findById(Long.valueOf(sessionId))
      .orElseThrow(() -> new ChargingSessionNotFoundException(SESSION_NOT_FOUND_MESSAGE + sessionId));
    
    session.setStatus(ChargingSession.ChargingSessionStatus.ERROR);
    session.setErrorMessage(errorMessage);
    return chargingSessionRepository.save(session);
  }

  public ChargingSession getSessionStatus(String sessionId) {
    return chargingSessionRepository.findById(Long.valueOf(sessionId))
      .orElseThrow(() -> new ChargingSessionNotFoundException(SESSION_NOT_FOUND_MESSAGE + sessionId));
  }
} 
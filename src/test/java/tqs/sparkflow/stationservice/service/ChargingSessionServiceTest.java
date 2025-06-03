package tqs.sparkflow.stationservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.repository.ChargingSessionRepository;

@ExtendWith(MockitoExtension.class)
class ChargingSessionServiceTest {

    @Mock
    private ChargingSessionRepository chargingSessionRepository;

    private ChargingSessionService chargingSessionService;

    @BeforeEach
    void setUp() {
        chargingSessionService = new ChargingSessionService(chargingSessionRepository);
    }

    @Test
    void whenUnlockStation_thenSessionIsCreated() {
        // Given
        String stationId = "STATION-001";
        String userId = "USER-001";
        ChargingSession expectedSession = new ChargingSession();
        when(chargingSessionRepository.save(any(ChargingSession.class))).thenReturn(expectedSession);

        // When
        ChargingSession result = chargingSessionService.unlockStation(stationId, userId);

        // Then
        assertNotNull(result);
        verify(chargingSessionRepository).save(any(ChargingSession.class));
    }

    @Test
    void whenStartCharging_thenSessionStatusIsUpdated() {
        // Given
        String sessionId = "SESSION-001";
        ChargingSession session = new ChargingSession();
        when(chargingSessionRepository.findById(Long.valueOf(sessionId))).thenReturn(java.util.Optional.of(session));
        when(chargingSessionRepository.save(any(ChargingSession.class))).thenReturn(session);

        // When
        ChargingSession result = chargingSessionService.startCharging(sessionId);

        // Then
        assertNotNull(result);
        assertEquals(ChargingSession.ChargingSessionStatus.CHARGING, result.getStatus());
        verify(chargingSessionRepository).save(any(ChargingSession.class));
    }

    @Test
    void whenEndCharging_thenSessionIsCompleted() {
        // Given
        String sessionId = "SESSION-001";
        ChargingSession session = new ChargingSession();
        when(chargingSessionRepository.findById(Long.valueOf(sessionId))).thenReturn(java.util.Optional.of(session));
        when(chargingSessionRepository.save(any(ChargingSession.class))).thenReturn(session);

        // When
        ChargingSession result = chargingSessionService.endCharging(sessionId);

        // Then
        assertNotNull(result);
        assertEquals(ChargingSession.ChargingSessionStatus.COMPLETED, result.getStatus());
        verify(chargingSessionRepository).save(any(ChargingSession.class));
    }

    @Test
    void whenErrorOccurs_thenErrorIsReported() {
        // Given
        String sessionId = "SESSION-001";
        String errorMessage = "Connection error";
        ChargingSession session = new ChargingSession();
        when(chargingSessionRepository.findById(Long.valueOf(sessionId))).thenReturn(java.util.Optional.of(session));
        when(chargingSessionRepository.save(any(ChargingSession.class))).thenReturn(session);

        // When
        ChargingSession result = chargingSessionService.reportError(sessionId, errorMessage);

        // Then
        assertNotNull(result);
        assertEquals(ChargingSession.ChargingSessionStatus.ERROR, result.getStatus());
        assertEquals(errorMessage, result.getErrorMessage());
        verify(chargingSessionRepository).save(any(ChargingSession.class));
    }
} 
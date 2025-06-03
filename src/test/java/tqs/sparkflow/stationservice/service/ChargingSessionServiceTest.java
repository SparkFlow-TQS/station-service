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
        Long sessionId = 1L;
        ChargingSession session = new ChargingSession();
        when(chargingSessionRepository.findById(sessionId)).thenReturn(java.util.Optional.of(session));
        when(chargingSessionRepository.save(any(ChargingSession.class))).thenReturn(session);

        // When
        ChargingSession result = chargingSessionService.startCharging(sessionId.toString());

        // Then
        assertNotNull(result);
        assertEquals(ChargingSession.ChargingSessionStatus.CHARGING, result.getStatus());
        verify(chargingSessionRepository).save(any(ChargingSession.class));
    }

    @Test
    void whenEndCharging_thenSessionIsCompleted() {
        // Given
        Long sessionId = 1L;
        ChargingSession session = new ChargingSession();
        when(chargingSessionRepository.findById(sessionId)).thenReturn(java.util.Optional.of(session));
        when(chargingSessionRepository.save(any(ChargingSession.class))).thenReturn(session);

        // When
        ChargingSession result = chargingSessionService.endCharging(sessionId.toString());

        // Then
        assertNotNull(result);
        assertEquals(ChargingSession.ChargingSessionStatus.COMPLETED, result.getStatus());
        verify(chargingSessionRepository).save(any(ChargingSession.class));
    }

    @Test
    void whenErrorOccurs_thenErrorIsReported() {
        // Given
        Long sessionId = 1L;
        String errorMessage = "Connection error";
        ChargingSession session = new ChargingSession();
        when(chargingSessionRepository.findById(sessionId)).thenReturn(java.util.Optional.of(session));
        when(chargingSessionRepository.save(any(ChargingSession.class))).thenReturn(session);

        // When
        ChargingSession result = chargingSessionService.reportError(sessionId.toString(), errorMessage);

        // Then
        assertNotNull(result);
        assertEquals(ChargingSession.ChargingSessionStatus.ERROR, result.getStatus());
        assertEquals(errorMessage, result.getErrorMessage());
        verify(chargingSessionRepository).save(any(ChargingSession.class));
    }
} 
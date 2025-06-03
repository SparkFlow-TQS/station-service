package tqs.sparkflow.stationservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tqs.sparkflow.stationservice.exception.ChargingSessionNotFoundException;
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

    @Test
    void whenStartCharging_withNonExistentSession_thenThrowException() {
        // Given
        String sessionId = "999";
        when(chargingSessionRepository.findById(Long.valueOf(sessionId))).thenReturn(java.util.Optional.empty());

        // When/Then
        assertThrows(ChargingSessionNotFoundException.class, () -> 
            chargingSessionService.startCharging(sessionId));
    }

    @Test
    void whenEndCharging_withNonExistentSession_thenThrowException() {
        // Given
        String sessionId = "999";
        when(chargingSessionRepository.findById(Long.valueOf(sessionId))).thenReturn(java.util.Optional.empty());

        // When/Then
        assertThrows(ChargingSessionNotFoundException.class, () -> 
            chargingSessionService.endCharging(sessionId));
    }

    @Test
    void whenReportError_withNonExistentSession_thenThrowException() {
        // Given
        String sessionId = "999";
        String errorMessage = "Connection error";
        when(chargingSessionRepository.findById(Long.valueOf(sessionId))).thenReturn(java.util.Optional.empty());

        // When/Then
        assertThrows(ChargingSessionNotFoundException.class, () -> 
            chargingSessionService.reportError(sessionId, errorMessage));
    }

    @Test
    void whenGetSessionStatus_withNonExistentSession_thenThrowException() {
        // Given
        String sessionId = "999";
        when(chargingSessionRepository.findById(Long.valueOf(sessionId))).thenReturn(java.util.Optional.empty());

        // When/Then
        assertThrows(ChargingSessionNotFoundException.class, () -> 
            chargingSessionService.getSessionStatus(sessionId));
    }

    @Test
    void whenUnlockStation_thenSessionHasCorrectInitialState() {
        // Given
        String stationId = "STATION-001";
        String userId = "USER-001";
        ChargingSession expectedSession = new ChargingSession();
        when(chargingSessionRepository.save(any(ChargingSession.class))).thenAnswer(invocation -> {
            ChargingSession savedSession = invocation.getArgument(0);
            assertEquals(stationId, savedSession.getStationId());
            assertEquals(userId, savedSession.getUserId());
            assertEquals(ChargingSession.ChargingSessionStatus.UNLOCKED, savedSession.getStatus());
            assertNull(savedSession.getStartTime());
            assertNull(savedSession.getEndTime());
            assertNull(savedSession.getErrorMessage());
            return savedSession;
        });

        // When
        ChargingSession result = chargingSessionService.unlockStation(stationId, userId);

        // Then
        assertNotNull(result);
        verify(chargingSessionRepository).save(any(ChargingSession.class));
    }

    @Test
    void whenStartCharging_thenStartTimeIsSet() {
        // Given
        Long sessionId = 1L;
        ChargingSession session = new ChargingSession();
        when(chargingSessionRepository.findById(sessionId)).thenReturn(java.util.Optional.of(session));
        when(chargingSessionRepository.save(any(ChargingSession.class))).thenReturn(session);

        // When
        ChargingSession result = chargingSessionService.startCharging(sessionId.toString());

        // Then
        assertNotNull(result);
        assertNotNull(result.getStartTime());
        assertEquals(ChargingSession.ChargingSessionStatus.CHARGING, result.getStatus());
    }

    @Test
    void whenEndCharging_thenEndTimeIsSet() {
        // Given
        Long sessionId = 1L;
        ChargingSession session = new ChargingSession();
        session.setStartTime(java.time.LocalDateTime.now());
        when(chargingSessionRepository.findById(sessionId)).thenReturn(java.util.Optional.of(session));
        when(chargingSessionRepository.save(any(ChargingSession.class))).thenReturn(session);

        // When
        ChargingSession result = chargingSessionService.endCharging(sessionId.toString());

        // Then
        assertNotNull(result);
        assertNotNull(result.getEndTime());
        assertEquals(ChargingSession.ChargingSessionStatus.COMPLETED, result.getStatus());
    }
} 
package tqs.sparkflow.stationservice.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class ChargingSessionTest {

    @Test
    void whenCreatingNewSession_thenInitialStateIsCorrect() {
        // Given
        String stationId = "STATION-001";
        String userId = "USER-001";

        // When
        ChargingSession session = new ChargingSession();
        session.setStationId(stationId);
        session.setUserId(userId);

        // Then
        assertEquals(stationId, session.getStationId());
        assertEquals(userId, session.getUserId());
        assertEquals(ChargingSession.ChargingSessionStatus.CREATED, session.getStatus());
        assertNull(session.getStartTime());
        assertNull(session.getEndTime());
        assertNull(session.getErrorMessage());
    }

    @Test
    void whenStartingCharging_thenStatusIsUpdated() {
        // Given
        ChargingSession session = new ChargingSession();
        session.setStationId("STATION-001");
        session.setUserId("USER-001");

        // When
        session.setStatus(ChargingSession.ChargingSessionStatus.CHARGING);
        session.setStartTime(LocalDateTime.now());

        // Then
        assertEquals(ChargingSession.ChargingSessionStatus.CHARGING, session.getStatus());
        assertNotNull(session.getStartTime());
    }

    @Test
    void whenEndingCharging_thenSessionIsCompleted() {
        // Given
        ChargingSession session = new ChargingSession();
        session.setStationId("STATION-001");
        session.setUserId("USER-001");
        session.setStatus(ChargingSession.ChargingSessionStatus.CHARGING);
        session.setStartTime(LocalDateTime.now());

        // When
        session.setStatus(ChargingSession.ChargingSessionStatus.COMPLETED);
        session.setEndTime(LocalDateTime.now());

        // Then
        assertEquals(ChargingSession.ChargingSessionStatus.COMPLETED, session.getStatus());
        assertNotNull(session.getEndTime());
    }

    @Test
    void whenReportingError_thenErrorStateIsSet() {
        // Given
        ChargingSession session = new ChargingSession();
        session.setStationId("STATION-001");
        session.setUserId("USER-001");
        String errorMessage = "Connection error";

        // When
        session.setStatus(ChargingSession.ChargingSessionStatus.ERROR);
        session.setErrorMessage(errorMessage);

        // Then
        assertEquals(ChargingSession.ChargingSessionStatus.ERROR, session.getStatus());
        assertEquals(errorMessage, session.getErrorMessage());
    }
} 
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
        assertFalse(session.isFinished());
        assertNull(session.getStartTime());
        assertNull(session.getEndTime());
    }

    @Test
    void whenCreatingSessionWithConstructor_thenStartTimeIsSet() {
        // Given
        String stationId = "STATION-001";
        String userId = "USER-001";

        // When
        ChargingSession session = new ChargingSession(stationId, userId);

        // Then
        assertEquals(stationId, session.getStationId());
        assertEquals(userId, session.getUserId());
        assertFalse(session.isFinished());
        assertNotNull(session.getStartTime());
        assertNull(session.getEndTime());
    }

    @Test
    void whenFinishingSession_thenFinishedFlagIsSet() {
        // Given
        ChargingSession session = new ChargingSession("STATION-001", "USER-001");

        // When
        session.setFinished(true);
        session.setEndTime(LocalDateTime.now());

        // Then
        assertTrue(session.isFinished());
        assertNotNull(session.getEndTime());
    }

    @Test
    void whenSettingSessionProperties_thenPropertiesAreCorrect() {
        // Given
        ChargingSession session = new ChargingSession();
        Long id = 1L;
        String stationId = "STATION-001";
        String userId = "USER-001";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(2);

        // When
        session.setId(id);
        session.setStationId(stationId);
        session.setUserId(userId);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setFinished(true);

        // Then
        assertEquals(id, session.getId());
        assertEquals(stationId, session.getStationId());
        assertEquals(userId, session.getUserId());
        assertEquals(startTime, session.getStartTime());
        assertEquals(endTime, session.getEndTime());
        assertTrue(session.isFinished());
    }
}
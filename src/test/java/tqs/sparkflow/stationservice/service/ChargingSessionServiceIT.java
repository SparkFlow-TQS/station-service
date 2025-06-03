package tqs.sparkflow.stationservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import tqs.sparkflow.stationservice.StationServiceApplication;
import tqs.sparkflow.stationservice.TestcontainersConfiguration;
import tqs.sparkflow.stationservice.config.TestConfig;
import tqs.sparkflow.stationservice.exception.ChargingSessionNotFoundException;
import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.repository.ChargingSessionRepository;

@SpringBootTest(
    classes = {StationServiceApplication.class, TestConfig.class, TestcontainersConfiguration.class},
    properties = {"spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles("test")
@Transactional
class ChargingSessionServiceIT {

    @Autowired
    private ChargingSessionService chargingSessionService;

    @Autowired
    private ChargingSessionRepository chargingSessionRepository;

    @BeforeEach
    void setUp() {
        chargingSessionRepository.deleteAll();
    }

    @Test
    void whenUnlockStation_thenSessionIsCreated() {
        // Given
        String stationId = "STATION-001";
        String userId = "USER-001";

        // When
        ChargingSession result = chargingSessionService.unlockStation(stationId, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStationId()).isEqualTo(stationId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getStatus()).isEqualTo(ChargingSession.ChargingSessionStatus.UNLOCKED);
        assertThat(result.getStartTime()).isNull();
        assertThat(result.getEndTime()).isNull();
        assertThat(result.getErrorMessage()).isNull();
    }

    @Test
    void whenStartCharging_thenSessionStatusIsUpdated() {
        // Given
        ChargingSession session = chargingSessionService.unlockStation("STATION-001", "USER-001");

        // When
        ChargingSession result = chargingSessionService.startCharging(session.getId().toString());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ChargingSession.ChargingSessionStatus.CHARGING);
        assertThat(result.getStartTime()).isNotNull();
    }

    @Test
    void whenEndCharging_thenSessionIsCompleted() {
        // Given
        ChargingSession session = chargingSessionService.unlockStation("STATION-001", "USER-001");
        session = chargingSessionService.startCharging(session.getId().toString());

        // When
        ChargingSession result = chargingSessionService.endCharging(session.getId().toString());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ChargingSession.ChargingSessionStatus.COMPLETED);
        assertThat(result.getEndTime()).isNotNull();
    }

    @Test
    void whenReportError_thenErrorIsRecorded() {
        // Given
        ChargingSession session = chargingSessionService.unlockStation("STATION-001", "USER-001");
        String errorMessage = "Connection error";

        // When
        ChargingSession result = chargingSessionService.reportError(session.getId().toString(), errorMessage);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ChargingSession.ChargingSessionStatus.ERROR);
        assertThat(result.getErrorMessage()).isEqualTo(errorMessage);
    }

    @Test
    void whenGetSessionStatus_thenReturnCurrentStatus() {
        // Given
        ChargingSession session = chargingSessionService.unlockStation("STATION-001", "USER-001");

        // When
        ChargingSession result = chargingSessionService.getSessionStatus(session.getId().toString());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ChargingSession.ChargingSessionStatus.UNLOCKED);
    }

    @Test
    void whenStartCharging_withNonExistentSession_thenThrowException() {
        // When/Then
        assertThatThrownBy(() -> chargingSessionService.startCharging("999"))
            .isInstanceOf(ChargingSessionNotFoundException.class)
            .hasMessageContaining("Session not found: 999");
    }

    @Test
    void whenEndCharging_withNonExistentSession_thenThrowException() {
        // When/Then
        assertThatThrownBy(() -> chargingSessionService.endCharging("999"))
            .isInstanceOf(ChargingSessionNotFoundException.class)
            .hasMessageContaining("Session not found: 999");
    }

    @Test
    void whenReportError_withNonExistentSession_thenThrowException() {
        // When/Then
        assertThatThrownBy(() -> chargingSessionService.reportError("999", "Error"))
            .isInstanceOf(ChargingSessionNotFoundException.class)
            .hasMessageContaining("Session not found: 999");
    }

    @Test
    void whenGetSessionStatus_withNonExistentSession_thenThrowException() {
        // When/Then
        assertThatThrownBy(() -> chargingSessionService.getSessionStatus("999"))
            .isInstanceOf(ChargingSessionNotFoundException.class)
            .hasMessageContaining("Session not found: 999");
    }

    /**
     * Tests the complete charging session flow from unlocking to completion.
     * Verifies that:
     * 1. A station can be unlocked and creates a session in UNLOCKED state
     * 2. The session can transition to CHARGING state with a start time
     * 3. The session can be completed with an end time
     * 4. All state transitions maintain the correct timestamps
     */
    @Test
    void whenCompleteChargingFlow_thenAllStatesAreCorrect() {
        // Given
        String stationId = "STATION-001";
        String userId = "USER-001";

        // When
        ChargingSession unlocked = chargingSessionService.unlockStation(stationId, userId);
        assertThat(unlocked.getStatus()).isEqualTo(ChargingSession.ChargingSessionStatus.UNLOCKED);
        
        ChargingSession charging = chargingSessionService.startCharging(unlocked.getId().toString());
        assertThat(charging.getStatus()).isEqualTo(ChargingSession.ChargingSessionStatus.CHARGING);
        
        ChargingSession completed = chargingSessionService.endCharging(charging.getId().toString());
        assertThat(completed.getStatus()).isEqualTo(ChargingSession.ChargingSessionStatus.COMPLETED);
        
        // Then
        assertThat(charging.getStartTime()).isNotNull();
        assertThat(completed.getEndTime()).isNotNull();
    }
} 
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
    void whenCreateSession_thenSessionIsCreated() {
        // Given
        String stationId = "STATION-001";
        String userId = "USER-001";

        // When
        ChargingSession result = chargingSessionService.createSession(stationId, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStationId()).isEqualTo(stationId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.isFinished()).isFalse();
        assertThat(result.getStartTime()).isNotNull();
        assertThat(result.getEndTime()).isNull();
    }

    @Test
    void whenEndSession_thenSessionIsCompleted() {
        // Given
        ChargingSession session = chargingSessionService.createSession("STATION-001", "USER-001");

        // When
        ChargingSession result = chargingSessionService.endSession(session.getId().toString());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isFinished()).isTrue();
        assertThat(result.getEndTime()).isNotNull();
    }

    @Test
    void whenGetSession_thenReturnSession() {
        // Given
        ChargingSession session = chargingSessionService.createSession("STATION-001", "USER-001");

        // When
        ChargingSession result = chargingSessionService.getSession(session.getId().toString());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStationId()).isEqualTo("STATION-001");
        assertThat(result.getUserId()).isEqualTo("USER-001");
    }

    @Test
    void whenEndSession_withNonExistentSession_thenThrowException() {
        // When/Then
        assertThatThrownBy(() -> chargingSessionService.endSession("999"))
            .isInstanceOf(ChargingSessionNotFoundException.class)
            .hasMessageContaining("Session not found: 999");
    }

    @Test
    void whenGetSession_withNonExistentSession_thenThrowException() {
        // When/Then
        assertThatThrownBy(() -> chargingSessionService.getSession("999"))
            .isInstanceOf(ChargingSessionNotFoundException.class)
            .hasMessageContaining("Session not found: 999");
    }

    /**
     * Tests the complete charging session flow from creation to completion.
     * Verifies that:
     * 1. A session can be created and starts immediately
     * 2. The session can be completed with an end time
     * 3. All state transitions maintain the correct timestamps
     */
    @Test
    void whenCompleteChargingFlow_thenAllStatesAreCorrect() {
        // Given
        String stationId = "STATION-001";
        String userId = "USER-001";

        // When
        ChargingSession created = chargingSessionService.createSession(stationId, userId);
        assertThat(created.isFinished()).isFalse();
        assertThat(created.getStartTime()).isNotNull();
        
        ChargingSession completed = chargingSessionService.endSession(created.getId().toString());
        assertThat(completed.isFinished()).isTrue();
        
        // Then
        assertThat(created.getStartTime()).isNotNull();
        assertThat(completed.getEndTime()).isNotNull();
    }
} 
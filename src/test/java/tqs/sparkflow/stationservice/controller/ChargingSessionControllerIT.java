package tqs.sparkflow.stationservice.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import tqs.sparkflow.stationservice.StationServiceApplication;
import tqs.sparkflow.stationservice.TestcontainersConfiguration;
import tqs.sparkflow.stationservice.config.TestConfig;
import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.repository.ChargingSessionRepository;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {StationServiceApplication.class, TestConfig.class, TestcontainersConfiguration.class},
    properties = {"spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles("test")
class ChargingSessionControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ChargingSessionRepository chargingSessionRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/charging-sessions";
        chargingSessionRepository.deleteAll();
    }

    @Test
    void whenUnlockStation_thenReturnSuccess() {
        // Given
        String stationId = "STATION-001";
        String userId = "USER-001";

        // When
        ResponseEntity<ChargingSession> response = restTemplate.postForEntity(
            baseUrl + "/unlock?stationId={stationId}&userId={userId}",
            null,
            ChargingSession.class,
            stationId,
            userId
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStationId()).isEqualTo(stationId);
        assertThat(response.getBody().getUserId()).isEqualTo(userId);
        assertThat(response.getBody().getStatus()).isEqualTo(ChargingSession.ChargingSessionStatus.UNLOCKED);
    }

    @Test
    void whenStartCharging_thenReturnSuccess() {
        // Given
        ChargingSession session = chargingSessionRepository.save(createTestSession("STATION-001", "USER-001"));

        // When
        ResponseEntity<ChargingSession> response = restTemplate.postForEntity(
            baseUrl + "/{sessionId}/start",
            null,
            ChargingSession.class,
            session.getId()
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(ChargingSession.ChargingSessionStatus.CHARGING);
        assertThat(response.getBody().getStartTime()).isNotNull();
    }

    @Test
    void whenEndCharging_thenReturnSuccess() {
        // Given
        ChargingSession session = chargingSessionRepository.save(createTestSession("STATION-001", "USER-001"));
        session.setStatus(ChargingSession.ChargingSessionStatus.CHARGING);
        session.setStartTime(java.time.LocalDateTime.now());
        session = chargingSessionRepository.save(session);

        // When
        ResponseEntity<ChargingSession> response = restTemplate.postForEntity(
            baseUrl + "/{sessionId}/end",
            null,
            ChargingSession.class,
            session.getId()
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(ChargingSession.ChargingSessionStatus.COMPLETED);
        assertThat(response.getBody().getEndTime()).isNotNull();
    }

    @Test
    void whenGetStatus_thenReturnCurrentStatus() {
        // Given
        ChargingSession session = chargingSessionRepository.save(createTestSession("STATION-001", "USER-001"));

        // When
        ResponseEntity<ChargingSession> response = restTemplate.getForEntity(
            baseUrl + "/{sessionId}/status",
            ChargingSession.class,
            session.getId()
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(ChargingSession.ChargingSessionStatus.UNLOCKED);
    }

    @Test
    void whenStartCharging_withNonExistentSession_thenReturnNotFound() {
        // When
        ResponseEntity<Void> response = restTemplate.postForEntity(
            baseUrl + "/{sessionId}/start",
            null,
            Void.class,
            "999"
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenEndCharging_withNonExistentSession_thenReturnNotFound() {
        // When
        ResponseEntity<Void> response = restTemplate.postForEntity(
            baseUrl + "/{sessionId}/end",
            null,
            Void.class,
            "999"
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenGetStatus_withNonExistentSession_thenReturnNotFound() {
        // When
        ResponseEntity<Void> response = restTemplate.getForEntity(
            baseUrl + "/{sessionId}/status",
            Void.class,
            "999"
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private ChargingSession createTestSession(String stationId, String userId) {
        ChargingSession session = new ChargingSession();
        session.setStationId(stationId);
        session.setUserId(userId);
        session.setStatus(ChargingSession.ChargingSessionStatus.UNLOCKED);
        return session;
    }
} 
package tqs.sparkflow.stationservice.controller;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.service.ChargingSessionService;

@ExtendWith(MockitoExtension.class)
class ChargingSessionControllerTest {

    @Mock
    private ChargingSessionService chargingSessionService;

    private ChargingSessionController chargingSessionController;
    private ChargingSession testSession;

    @BeforeEach
    void setUp() {
        chargingSessionController = new ChargingSessionController(chargingSessionService);
        testSession = new ChargingSession();
        testSession.setStationId("STATION-001");
        testSession.setUserId("USER-001");
    }

    @Test
    @XrayTest(key = "CHARGING-SESSION-1")
    @Requirement("CHARGING-SESSION-1")
    void whenStartSession_thenReturnSuccess() {
        // Given
        when(chargingSessionService.createSession(anyString(), anyString()))
                .thenReturn(testSession);

        // When
        var response = chargingSessionController.startSession("STATION-001", "USER-001");

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull().satisfies(body -> {
            assertThat(body.getStationId()).isEqualTo("STATION-001");
        });
    }

    @Test
    @XrayTest(key = "CHARGING-SESSION-2")
    @Requirement("CHARGING-SESSION-2")
    void whenEndSession_thenReturnSuccess() {
        // Given
        when(chargingSessionService.endSession(anyString())).thenReturn(testSession);

        // When
        var response = chargingSessionController.endSession("1");

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @XrayTest(key = "CHARGING-SESSION-3")
    @Requirement("CHARGING-SESSION-3")
    void whenGetSession_thenReturnSession() {
        // Given
        when(chargingSessionService.getSession(anyString())).thenReturn(testSession);

        // When
        var response = chargingSessionController.getSession("1");

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}

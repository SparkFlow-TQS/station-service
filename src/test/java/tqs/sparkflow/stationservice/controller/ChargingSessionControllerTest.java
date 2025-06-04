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
    void whenUnlockStation_thenReturnSuccess() {
        // Given
        when(chargingSessionService.unlockStation(anyString(), anyString())).thenReturn(testSession);

        // When
        var response = chargingSessionController.unlockStation("STATION-001", "USER-001");

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull().satisfies(body -> {
            assertThat(body.getStationId()).isEqualTo("STATION-001");
        });
    }

    @Test
    @XrayTest(key = "CHARGING-SESSION-2")
    @Requirement("CHARGING-SESSION-2")
    void whenStartCharging_thenReturnSuccess() {
        // Given
        when(chargingSessionService.startCharging(anyString())).thenReturn(testSession);

        // When
        var response = chargingSessionController.startCharging("1");

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @XrayTest(key = "CHARGING-SESSION-3")
    @Requirement("CHARGING-SESSION-3")
    void whenEndCharging_thenReturnSuccess() {
        // Given
        when(chargingSessionService.endCharging(anyString())).thenReturn(testSession);

        // When
        var response = chargingSessionController.endCharging("1");

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @XrayTest(key = "CHARGING-SESSION-4")
    @Requirement("CHARGING-SESSION-4")
    void whenGetStatus_thenReturnCurrentStatus() {
        // Given
        when(chargingSessionService.getSessionStatus(anyString())).thenReturn(testSession);

        // When
        var response = chargingSessionController.getSessionStatus("1");

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
} 
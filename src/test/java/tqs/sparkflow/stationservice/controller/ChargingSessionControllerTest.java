package tqs.sparkflow.stationservice.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import tqs.sparkflow.stationservice.config.ControllerTestConfig;
import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.service.ChargingSessionService;

@WebMvcTest(ChargingSessionController.class)
@Import(ControllerTestConfig.class)
@ActiveProfiles("controller-test")
class ChargingSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChargingSessionService chargingSessionService;

    private ChargingSession testSession;

    @BeforeEach
    void setUp() {
        testSession = new ChargingSession();
        testSession.setStationId("STATION-001");
        testSession.setUserId("USER-001");
    }

    @Test
    void whenUnlockStation_thenReturnSuccess() throws Exception {
        // Given
        when(chargingSessionService.unlockStation(anyString(), anyString())).thenReturn(testSession);

        // When/Then
        mockMvc.perform(post("/api/v1/charging-sessions/unlock")
                .param("stationId", "STATION-001")
                .param("userId", "USER-001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationId").value("STATION-001"));
    }

    @Test
    void whenStartCharging_thenReturnSuccess() throws Exception {
        // Given
        when(chargingSessionService.startCharging(anyString())).thenReturn(testSession);

        // When/Then
        mockMvc.perform(post("/api/v1/charging-sessions/{sessionId}/start", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenEndCharging_thenReturnSuccess() throws Exception {
        // Given
        when(chargingSessionService.endCharging(anyString())).thenReturn(testSession);

        // When/Then
        mockMvc.perform(post("/api/v1/charging-sessions/{sessionId}/end", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenGetStatus_thenReturnCurrentStatus() throws Exception {
        // Given
        when(chargingSessionService.getSessionStatus(anyString())).thenReturn(testSession);

        // When/Then
        mockMvc.perform(get("/api/v1/charging-sessions/{sessionId}/status", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
} 
package tqs.sparkflow.stationservice.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.service.ChargingSessionService;

@WebMvcTest(ChargingSessionController.class)
class ChargingSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChargingSessionService chargingSessionService;

    @Test
    void whenUnlockStation_thenReturnSuccess() throws Exception {
        // Given
        String stationId = "STATION-001";
        String userId = "USER-001";
        ChargingSession session = new ChargingSession();
        when(chargingSessionService.unlockStation(stationId, userId)).thenReturn(session);

        // When/Then
        mockMvc.perform(post("/api/v1/charging-sessions/unlock")
                .param("stationId", stationId)
                .param("userId", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationId").value(stationId));
    }

    @Test
    void whenStartCharging_thenReturnSuccess() throws Exception {
        // Given
        String sessionId = "SESSION-001";
        ChargingSession session = new ChargingSession();
        when(chargingSessionService.startCharging(sessionId)).thenReturn(session);

        // When/Then
        mockMvc.perform(post("/api/v1/charging-sessions/{sessionId}/start", sessionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenEndCharging_thenReturnSuccess() throws Exception {
        // Given
        String sessionId = "SESSION-001";
        ChargingSession session = new ChargingSession();
        when(chargingSessionService.endCharging(sessionId)).thenReturn(session);

        // When/Then
        mockMvc.perform(post("/api/v1/charging-sessions/{sessionId}/end", sessionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenGetStatus_thenReturnCurrentStatus() throws Exception {
        // Given
        String sessionId = "SESSION-001";
        ChargingSession session = new ChargingSession();
        when(chargingSessionService.getSessionStatus(sessionId)).thenReturn(session);

        // When/Then
        mockMvc.perform(get("/api/v1/charging-sessions/{sessionId}/status", sessionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
} 
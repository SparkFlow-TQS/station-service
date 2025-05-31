package tqs.sparkflow.stationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAndGetBooking() throws Exception {
        Booking booking = new Booking();
        booking.setStationId(1L);
        booking.setUserId(1L);
        booking.setStartTime(LocalDateTime.now());
        booking.setEndTime(LocalDateTime.now().plusHours(2));
        booking.setStatus(BookingStatus.ACTIVE);

        // Create booking
        ResultActions createResult = mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)));
        createResult.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        // Get all bookings for user
        mockMvc.perform(get("/bookings?userId=1"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingById_notFound() throws Exception {
        mockMvc.perform(get("/bookings/999").principal(() -> "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelBooking_notFound() throws Exception {
        mockMvc.perform(post("/bookings/999/cancel").with(csrf()))
                .andExpect(status().isNotFound());
    }
} 
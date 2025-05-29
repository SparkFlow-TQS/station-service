package tqs.sparkflow.stationservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import com.fasterxml.jackson.databind.ObjectMapper;

import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;
import tqs.sparkflow.stationservice.service.BookingService;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private Booking testBooking;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setStationId(1L);
        testBooking.setUserId(1L);
        testBooking.setStartTime(now);
        testBooking.setEndTime(now.plusHours(2));
        testBooking.setStatus(BookingStatus.ACTIVE);
    }

    @Test
    @WithMockUser
    void whenCreateBooking_thenReturnCreatedBooking() throws Exception {
        when(bookingService.createBooking(any(Booking.class))).thenReturn(testBooking);

        mockMvc.perform(post("/bookings")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBooking)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testBooking.getId()))
                .andExpect(jsonPath("$.stationId").value(testBooking.getStationId()))
                .andExpect(jsonPath("$.userId").value(testBooking.getUserId()))
                .andExpect(jsonPath("$.status").value(testBooking.getStatus().toString()));
    }

    @Test
    @WithMockUser
    void whenGetBookingById_thenReturnBooking() throws Exception {
        when(bookingService.getBookingById(1L)).thenReturn(Optional.of(testBooking));

        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId()))
                .andExpect(jsonPath("$.stationId").value(testBooking.getStationId()));
    }

    @Test
    @WithMockUser
    void whenGetBookingById_notFound_thenReturn404() throws Exception {
        when(bookingService.getBookingById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/bookings/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void whenGetAllBookings_thenReturnList() throws Exception {
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.getAllBookings()).thenReturn(bookings);

        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].stationId").value(testBooking.getStationId()));
    }

    @Test
    @WithMockUser
    void whenCancelBooking_thenReturnUpdatedBooking() throws Exception {
        testBooking.setStatus(BookingStatus.CANCELLED);
        when(bookingService.cancelBooking(1L)).thenReturn(testBooking);

        mockMvc.perform(put("/bookings/1/cancel")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(BookingStatus.CANCELLED.toString()));
    }

    @Test
    @WithMockUser
    void whenGetBookingsByStationId_thenReturnList() throws Exception {
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.getBookingsByStationId(1L)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/station/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].stationId").value(testBooking.getStationId()));
    }

    @Test
    @WithMockUser
    void whenGetBookingsByUserId_thenReturnList() throws Exception {
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.getBookingsByUserId(1L)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].userId").value(testBooking.getUserId()));
    }
} 
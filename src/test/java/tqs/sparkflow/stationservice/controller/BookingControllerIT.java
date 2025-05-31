package tqs.sparkflow.stationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.BookingRepository;
import tqs.sparkflow.stationservice.service.StationService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private StationService stationService;

    @MockBean
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        // Mock user service responses
        doReturn(new Object()).when(restTemplate).getForObject(anyString(), Object.class);
        doThrow(new RuntimeException("User not found")).when(restTemplate).getForObject("http://dummy-user-service-url/users/999", Object.class);
        doReturn(true).when(restTemplate).getForObject("http://dummy-user-service-url/users/1/has-role/ADMIN", Boolean.class);

        // Mock station service
        Station mockStation = new Station();
        mockStation.setId(1L);
        mockStation.setIsOperational(true);
        doReturn(mockStation).when(stationService).getStationById(1L);
        doThrow(new RuntimeException("Station not found")).when(stationService).getStationById(999L);

        // Mock booking repository
        doReturn(Collections.emptyList()).when(bookingRepository).findOverlappingBookings(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
        doReturn(Optional.empty()).when(bookingRepository).findById(999L);
        doAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(1L);
            return booking;
        }).when(bookingRepository).save(any(Booking.class));
        doReturn(Collections.singletonList(new Booking())).when(bookingRepository).findByUserId(anyLong());
        doReturn(Collections.singletonList(new Booking())).when(bookingRepository).findByStationId(anyLong());
        doReturn(Collections.singletonList(new Booking())).when(bookingRepository).findAll();
    }

    @Test
    void createAndGetBooking() throws Exception {
        // Create booking
        mockMvc.perform(post("/bookings")
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stationId\":1,\"userId\":1,\"startTime\":\"2025-05-31T21:51:15.819308398\",\"endTime\":\"2025-05-31T23:51:15.819325787\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        // Get booking
        mockMvc.perform(get("/bookings")
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("1"))
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createBooking_unauthenticated() throws Exception {
        // Create a valid booking request
        String validBookingJson = "{\"stationId\":1,\"userId\":1,\"startTime\":\"2025-05-31T21:51:15.819308398\",\"endTime\":\"2025-05-31T23:51:15.819325787\",\"status\":\"ACTIVE\"}";

        // Make the request without authentication
        mockMvc.perform(post("/bookings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(validBookingJson)
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("anonymous", "anonymous")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBookingById_notFound() throws Exception {
        // Mock repository to return empty for non-existent booking
        doReturn(Optional.empty()).when(bookingRepository).findById(999L);

        mockMvc.perform(get("/bookings/999")
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("1"))) // Use proper authentication
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelBooking_notFound() throws Exception {
        mockMvc.perform(post("/bookings/999/cancel").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBooking_validationError() throws Exception {
        Booking booking = new Booking(); // missing required fields
        mockMvc.perform(post("/bookings")
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAndGetAndCancelBooking_happyPath() throws Exception {
        Booking booking = new Booking();
        booking.setStationId(1L);
        booking.setUserId(1L);
        booking.setStartTime(LocalDateTime.now());
        booking.setEndTime(LocalDateTime.now().plusHours(2));
        booking.setStatus(BookingStatus.ACTIVE);

        // Mock repository to return the booking when finding by ID
        Booking savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setStationId(1L);
        savedBooking.setUserId(1L);
        savedBooking.setStartTime(booking.getStartTime());
        savedBooking.setEndTime(booking.getEndTime());
        savedBooking.setStatus(BookingStatus.ACTIVE);
        doReturn(Optional.of(savedBooking)).when(bookingRepository).findById(1L);

        // Create booking
        String response = mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking))
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("1"))) // Use proper authentication
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long bookingId = objectMapper.readTree(response).get("id").asLong();

        // Get by id
        mockMvc.perform(get("/bookings/" + bookingId)
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("1"))) // Use proper authentication
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));

        // Cancel booking
        mockMvc.perform(post("/bookings/" + bookingId + "/cancel")
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("1"))) // Use proper authentication
                .andExpect(status().isNoContent());
    }

    @Test
    void createOverlappingBooking_shouldFail() throws Exception {
        // Mock findOverlappingBookings to return a list with one booking
        doReturn(java.util.Collections.singletonList(new Booking())).when(bookingRepository)
            .findOverlappingBookings(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));

        Booking booking = new Booking();
        booking.setStationId(1L);
        booking.setUserId(1L);
        booking.setStartTime(LocalDateTime.now());
        booking.setEndTime(LocalDateTime.now().plusHours(2));
        booking.setStatus(BookingStatus.ACTIVE);
        mockMvc.perform(post("/bookings")
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_nonOperationalStation_shouldFail() throws Exception {
        // Mock station service to return non-operational station
        Station nonOperationalStation = new Station();
        nonOperationalStation.setId(999L);
        nonOperationalStation.setIsOperational(false);
        doReturn(nonOperationalStation).when(stationService).getStationById(999L);

        mockMvc.perform(post("/bookings")
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user("1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stationId\":999,\"userId\":1,\"startTime\":\"2025-05-31T21:51:15.819308398\",\"endTime\":\"2025-05-31T23:51:15.819325787\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRecurringBooking_happyPath() throws Exception {
        mockMvc.perform(post("/bookings/recurring")
                .param("userId", "1")
                .param("stationId", "1")
                .param("startTime", LocalDateTime.now().toString())
                .param("endTime", LocalDateTime.now().plusHours(2).toString())
                .param("recurringDays", "1,2,3")
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    void getBookingsByStationId_andByUserId() throws Exception {
        // Mock repository to return a list with one booking
        Booking mockBooking = new Booking();
        mockBooking.setId(1L);
        doReturn(java.util.Collections.singletonList(mockBooking)).when(bookingRepository).findByStationId(anyLong());
        doReturn(java.util.Collections.singletonList(mockBooking)).when(bookingRepository).findByUserId(anyLong());

        // Get by station
        mockMvc.perform(get("/bookings/station/1")
                .with(csrf())) // Add CSRF token
                .andExpect(status().isOk());

        // Get by user
        mockMvc.perform(get("/bookings/user/1")
                .with(csrf())) // Add CSRF token
                .andExpect(status().isOk());
    }
} 
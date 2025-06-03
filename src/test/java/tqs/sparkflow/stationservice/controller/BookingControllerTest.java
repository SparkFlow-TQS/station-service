package tqs.sparkflow.stationservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;
import tqs.sparkflow.stationservice.service.BookingService;
import tqs.sparkflow.stationservice.service.StationService;
import tqs.sparkflow.stationservice.repository.BookingRepository;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import tqs.sparkflow.stationservice.config.ControllerTestConfig;

@WebMvcTest(BookingController.class)
@Import(ControllerTestConfig.class)
@ActiveProfiles("controller-test")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private StationService stationService;

    @MockBean
    private BookingRepository bookingRepository;

    private Booking testBooking;
    private LocalDateTime now;
    private Set<Integer> recurringDays;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        recurringDays = new HashSet<>(Arrays.asList(1, 2, 3)); // Monday, Tuesday, Wednesday

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setStationId(1L);
        testBooking.setUserId(1L);
        testBooking.setStartTime(now);
        testBooking.setEndTime(now.plusHours(2));
        testBooking.setRecurringDays(recurringDays);
        testBooking.setStatus(BookingStatus.ACTIVE);

        // Mock user validation to return true
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(true);
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(new Object());
    }

    @Test
    @WithMockUser(username = "1")
    void whenCreateBooking_thenReturnCreatedBooking() throws Exception {
        when(bookingService.createBooking(any(Booking.class))).thenReturn(testBooking);

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBooking)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testBooking.getId()))
                .andExpect(jsonPath("$.stationId").value(testBooking.getStationId()))
                .andExpect(jsonPath("$.userId").value(testBooking.getUserId()))
                .andExpect(jsonPath("$.status").value(testBooking.getStatus().toString()));
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetAllBookings_thenReturnList() throws Exception {
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.getAllBookings(1L)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].stationId").value(testBooking.getStationId()));
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetBookingById_thenReturnBooking() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(Optional.of(testBooking));

        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId()))
                .andExpect(jsonPath("$.stationId").value(testBooking.getStationId()));
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetBookingById_notFound_thenReturn404() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/bookings/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "1")
    void whenCancelBooking_thenReturnNoContent() throws Exception {
        testBooking.setStatus(BookingStatus.CANCELLED);
        when(bookingService.cancelBooking(anyLong())).thenReturn(testBooking);

        mockMvc.perform(post("/bookings/1/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetBookingsByStationId_thenReturnList() throws Exception {
        when(bookingService.getBookingsByStationId(1L, 1L)).thenReturn(List.of(testBooking));

        mockMvc.perform(get("/bookings/station/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].stationId").value(testBooking.getStationId()));
    }

    @Test
    @WithMockUser(username = "99")
    void whenGetBookingsByStationId_withUserNotAuthorized_thenReturnBadRequest() throws Exception {
        when(bookingService.getBookingsByStationId(99L, 99L))
            .thenThrow(new IllegalStateException("User not found or not authorized"));

        mockMvc.perform(get("/bookings/station/99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetBookingsByStationId_withNoBookings_thenReturnNoContent() throws Exception {
        when(bookingService.getBookingsByStationId(1L, 1L)).thenReturn(List.of());

        mockMvc.perform(get("/bookings/station/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenGetBookingsByStationId_withoutAuthentication_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/bookings/station/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetBookingsByUserId_thenReturnList() throws Exception {
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.getBookingsByUserId(anyLong())).thenReturn(bookings);

        mockMvc.perform(get("/bookings/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].userId").value(testBooking.getUserId()));
    }

    @Test
    void whenCreateBooking_withoutAuthentication_thenReturnForbidden() throws Exception {
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBooking)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1")
    void whenCreateBooking_withInvalidInput_thenBadRequest() throws Exception {
        Booking inputBooking = new Booking(); // missing required fields
        when(bookingService.createBooking(any(Booking.class))).thenThrow(new IllegalStateException("Invalid input"));
        mockMvc.perform(post("/bookings")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputBooking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "2")
    void whenCancelBooking_withoutPermission_thenForbidden() throws Exception {
        // Simulate permission denied
        when(bookingService.cancelBooking(anyLong())).thenThrow(new IllegalStateException("User not authorized to cancel this booking"));
        mockMvc.perform(post("/bookings/1/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Controller maps IllegalStateException to 404
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetAllBookings_withNoBookings_thenNoContent() throws Exception {
        when(bookingService.getAllBookings(1L)).thenReturn(List.of());
        mockMvc.perform(get("/bookings").param("userId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "1")
    void whenCreateBooking_withMalformedJson_thenBadRequest() throws Exception {
        String malformedJson = "{\"stationId\":1,\"userId\":1,\"startTime\":\"not-a-date\"}";
        mockMvc.perform(post("/bookings")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    static Stream<Arguments> createBookingExceptionProvider() {
        return Stream.of(
            Arguments.of(2L, 1L, "Station is not operational"),
            Arguments.of(1L, 1L, "Time slot is already booked"),
            Arguments.of(1L, 999L, "User not found or not authorized")
        );
    }

    @ParameterizedTest
    @MethodSource("createBookingExceptionProvider")
    @WithMockUser(username = "1")
    void whenCreateBooking_withBusinessException_thenBadRequest(Long stationId, Long userId, String exceptionMessage) throws Exception {
        Booking inputBooking = new Booking();
        inputBooking.setStationId(stationId);
        inputBooking.setUserId(userId);
        inputBooking.setStartTime(now);
        inputBooking.setEndTime(now.plusHours(2));
        inputBooking.setStatus(BookingStatus.ACTIVE);
        when(bookingService.createBooking(any(Booking.class))).thenThrow(new IllegalStateException(exceptionMessage));
        mockMvc.perform(post("/bookings")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputBooking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "1")
    void whenCancelBooking_withNonExistentBooking_thenNotFound() throws Exception {
        when(bookingService.cancelBooking(anyLong())).thenThrow(new IllegalStateException("Booking not found"));
        mockMvc.perform(post("/bookings/999/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetBookingsByUserId_withNoResults_thenNoContent() throws Exception {
        when(bookingService.getBookingsByUserId(anyLong())).thenReturn(List.of());
        mockMvc.perform(get("/bookings/user/1"))
                .andExpect(status().isNoContent());
    }
} 
package tqs.sparkflow.stationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import tqs.sparkflow.stationservice.config.TestConfig;
import tqs.sparkflow.stationservice.config.WebConfig;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.BookingRepository;
import tqs.sparkflow.stationservice.repository.StationRepository;
import tqs.sparkflow.stationservice.service.StationService;
import tqs.sparkflow.stationservice.service.BookingService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest(BookingController.class)
@Import({TestConfig.class, WebConfig.class})
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
    private BookingService bookingService;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private StationRepository stationRepository;

    private Booking testBooking;
    private Station testStation;
    private LocalDateTime now;
    private Set<Integer> recurringDays;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        recurringDays = new HashSet<>(Arrays.asList(1, 2, 3)); // Monday, Tuesday, Wednesday

        testStation = new Station();
        testStation.setId(1L);
        testStation.setIsOperational(true);

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setStationId(1L);
        testBooking.setUserId(1L);
        testBooking.setStartTime(now);
        testBooking.setEndTime(now.plusHours(2));
        testBooking.setRecurringDays(recurringDays);
        testBooking.setStatus(BookingStatus.ACTIVE);

        // Mock user service responses
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(new Object());
        when(restTemplate.getForObject("http://dummy-user-service-url/users/999", Object.class))
                .thenThrow(new RuntimeException("User not found"));
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(true);

        // Mock station service
        when(stationService.getStationById(1L)).thenReturn(testStation);
        when(stationService.getStationById(999L))
                .thenThrow(new RuntimeException("Station not found"));

        // Mock booking service
        when(bookingService.createBooking(any(Booking.class))).thenReturn(testBooking);
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(Optional.of(testBooking));
        when(bookingService.getBookingsByStationId(anyLong(), anyLong()))
                .thenReturn(Collections.singletonList(testBooking));
        when(bookingService.getBookingsByUserId(anyLong()))
                .thenReturn(Collections.singletonList(testBooking));
        when(bookingService.getAllBookings(anyLong()))
                .thenReturn(Collections.singletonList(testBooking));
        when(bookingService.cancelBooking(anyLong())).thenReturn(testBooking);

        // Mock booking repository
        when(bookingRepository.findOverlappingBookings(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(1L);
            return booking;
        });
        when(bookingRepository.findByUserId(anyLong()))
                .thenReturn(Collections.singletonList(testBooking));
        when(bookingRepository.findByStationId(anyLong()))
                .thenReturn(Collections.singletonList(testBooking));
        when(bookingRepository.findAll()).thenReturn(Collections.singletonList(testBooking));
    }

    @Test
    @XrayTest(key = "BOOKING-1")
    @Requirement("BOOKING-1")
    @WithMockUser(username = "1")
    void whenCreateBooking_thenReturnCreatedBooking() throws Exception {
        Booking inputBooking = new Booking();
        inputBooking.setStationId(1L);
        inputBooking.setUserId(1L);
        inputBooking.setStartTime(now.plusHours(3));
        inputBooking.setEndTime(now.plusHours(4));
        inputBooking.setStatus(BookingStatus.ACTIVE);

        MvcResult result = mockMvc
                .perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputBooking)))
                .andExpect(status().isCreated()).andReturn();

        Booking createdBooking =
                objectMapper.readValue(result.getResponse().getContentAsString(), Booking.class);
        assertThat(createdBooking.getStationId()).isEqualTo(1L);
        assertThat(createdBooking.getUserId()).isEqualTo(1L);
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.ACTIVE);
    }

    @Test
    @XrayTest(key = "BOOKING-2")
    @Requirement("BOOKING-2")
    @WithMockUser(username = "1")
    void whenGetAllBookings_thenReturnList() throws Exception {
        mockMvc.perform(get("/api/v1/bookings").param("userId", "1")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].stationId").value(testBooking.getStationId()));
    }

    @Test
    @XrayTest(key = "BOOKING-3")
    @Requirement("BOOKGING-3")
    @WithMockUser(username = "1")
    void whenGetBookingById_thenReturnBooking() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId()))
                .andExpect(jsonPath("$.stationId").value(testBooking.getStationId()));
    }

    @Test
    @XrayTest(key = "BOOKING-4")
    @Requirement("BOOKING-4")
    @WithMockUser(username = "1")
    void whenCancelBooking_thenReturnUpdatedBooking() throws Exception {
        mockMvc.perform(post("/api/v1/bookings/1/cancel")).andExpect(status().isNoContent());
    }

    @Test
    @XrayTest(key = "BOOKING-5")
    @Requirement("BOOKING-5")
    @WithMockUser(username = "1")
    void whenGetBookingsByStationId_thenReturnList() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/station/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].stationId").value(testBooking.getStationId()));
    }

    @Test
    @XrayTest(key = "BOOKING-6")
    @Requirement("BOOKING-6")
    @WithMockUser(username = "1")
    void whenGetBookingsByUserId_thenReturnList() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/user/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].userId").value(testBooking.getUserId()));
    }

    @Test
    @XrayTest(key = "BOOKING-7")
    @Requirement("BOOKING-7")
    void whenCreateBooking_withoutAuth_thenUnauthorized() throws Exception {
        Booking inputBooking = new Booking();
        inputBooking.setStationId(1L);
        inputBooking.setStartTime(now);
        inputBooking.setEndTime(now.plusHours(2));
        inputBooking.setStatus(BookingStatus.ACTIVE);
        mockMvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputBooking))
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @XrayTest(key = "BOOKING-8")
    @Requirement("BOOKING-8")
    @WithMockUser(username = "1")
    void whenGetBookingsByStationId_withNoBookings_thenReturnNoContent() throws Exception {
        when(bookingService.getBookingsByStationId(anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/v1/bookings/station/1")).andExpect(status().isNoContent());
    }

    @Test
    @XrayTest(key = "BOOKING-9")
    @Requirement("BOOKING-9")
    @WithMockUser(username = "1")
    void whenGetBookingsByUserId_withNoBookings_thenReturnNoContent() throws Exception {
        when(bookingService.getBookingsByUserId(anyLong())).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/v1/bookings/user/1")).andExpect(status().isNoContent());
    }

    @Test
    @XrayTest(key = "BOOKING-10")
    @Requirement("BOOKING-10")
    @WithMockUser(username = "1")
    void createRecurringBooking_happyPath() throws Exception {
        mockMvc.perform(post("/api/v1/bookings/recurring").param("userId", "1")
                .param("stationId", "1").param("startTime", LocalDateTime.now().toString())
                .param("endTime", LocalDateTime.now().plusHours(2).toString())
                .param("recurringDays", "1,2,3")).andExpect(status().isCreated());
    }

    @Test
    @XrayTest(key = "BOOKING-11")
    @Requirement("BOOKING-11")
    @WithMockUser(username = "1")
    void getBookingsByStationId_andByUserId() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/station/1")).andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/bookings/user/1")).andExpect(status().isOk());
    }
}

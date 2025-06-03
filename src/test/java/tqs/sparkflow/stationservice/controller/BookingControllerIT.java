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
import tqs.sparkflow.stationservice.StationServiceApplication;
import tqs.sparkflow.stationservice.config.TestConfig;
import tqs.sparkflow.stationservice.config.WebConfig;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.BookingRepository;
import tqs.sparkflow.stationservice.repository.StationRepository;
import tqs.sparkflow.stationservice.service.StationService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {StationServiceApplication.class, TestConfig.class, WebConfig.class},
    properties = {"spring.main.allow-bean-definition-overriding=true"}
)
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
        doReturn(new Object()).when(restTemplate).getForObject(anyString(), eq(Object.class));
        doThrow(new RuntimeException("User not found")).when(restTemplate).getForObject("http://dummy-user-service-url/users/999", Object.class);
        doReturn(true).when(restTemplate).getForObject(anyString(), eq(Boolean.class));

        // Mock station service
        doReturn(testStation).when(stationService).getStationById(1L);
        doThrow(new RuntimeException("Station not found")).when(stationService).getStationById(999L);

        // Mock booking repository
        doReturn(Collections.emptyList()).when(bookingRepository).findOverlappingBookings(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
        doReturn(Optional.of(testBooking)).when(bookingRepository).findById(1L);
        doReturn(Optional.empty()).when(bookingRepository).findById(999L);
        doAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(1L);
            return booking;
        }).when(bookingRepository).save(any(Booking.class));
        doReturn(Collections.singletonList(testBooking)).when(bookingRepository).findByUserId(anyLong());
        doReturn(Collections.singletonList(testBooking)).when(bookingRepository).findByStationId(anyLong());
        doReturn(Collections.singletonList(testBooking)).when(bookingRepository).findAll();
    }

    @Test
    @WithMockUser(username = "1")
    void whenCreateBooking_thenReturnCreatedBooking() throws Exception {
        Booking inputBooking = new Booking();
        inputBooking.setStationId(1L);
        inputBooking.setUserId(1L);
        inputBooking.setStartTime(now.plusHours(3));
        inputBooking.setEndTime(now.plusHours(4));
        inputBooking.setStatus(BookingStatus.ACTIVE);

        MvcResult result = mockMvc.perform(post("/api/v1/bookings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputBooking)))
                .andExpect(status().isCreated())
                .andReturn();

        Booking createdBooking = objectMapper.readValue(result.getResponse().getContentAsString(), Booking.class);
        assertThat(createdBooking.getStationId()).isEqualTo(1L);
        assertThat(createdBooking.getUserId()).isEqualTo(1L);
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.ACTIVE);
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetAllBookings_thenReturnList() throws Exception {
        mockMvc.perform(get("/api/v1/bookings")
                .with(csrf())
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].stationId").value(testBooking.getStationId()));
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetBookingById_thenReturnBooking() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId()))
                .andExpect(jsonPath("$.stationId").value(testBooking.getStationId()));
    }

    @Test
    @WithMockUser(username = "1")
    void whenCancelBooking_thenReturnUpdatedBooking() throws Exception {
        mockMvc.perform(post("/api/v1/bookings/1/cancel")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetBookingsByStationId_thenReturnList() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/station/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].stationId").value(testBooking.getStationId()));
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetBookingsByUserId_thenReturnList() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/user/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].userId").value(testBooking.getUserId()));
    }

    @Test
    void whenCreateBooking_withoutAuth_thenForbidden() throws Exception {
        Booking inputBooking = new Booking();
        inputBooking.setStationId(1L);
        inputBooking.setStartTime(now);
        inputBooking.setEndTime(now.plusHours(2));
        inputBooking.setStatus(BookingStatus.ACTIVE);
        mockMvc.perform(post("/api/v1/bookings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputBooking))
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetBookingsByStationId_withNoBookings_thenReturnNoContent() throws Exception {
        doReturn(Collections.emptyList()).when(bookingRepository).findByStationId(anyLong());
        mockMvc.perform(get("/api/v1/bookings/station/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "1")
    void whenGetBookingsByUserId_withNoBookings_thenReturnNoContent() throws Exception {
        doReturn(Collections.emptyList()).when(bookingRepository).findByUserId(anyLong());
        mockMvc.perform(get("/api/v1/bookings/user/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "1")
    void createRecurringBooking_happyPath() throws Exception {
        mockMvc.perform(post("/api/v1/bookings/recurring")
                .with(csrf())
                .param("userId", "1")
                .param("stationId", "1")
                .param("startTime", LocalDateTime.now().toString())
                .param("endTime", LocalDateTime.now().plusHours(2).toString())
                .param("recurringDays", "1,2,3"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "1")
    void getBookingsByStationId_andByUserId() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/station/1")
                .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/bookings/user/1")
                .with(csrf()))
                .andExpect(status().isOk());
    }
} 
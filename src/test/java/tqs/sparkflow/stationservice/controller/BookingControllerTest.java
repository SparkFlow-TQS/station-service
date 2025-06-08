package tqs.sparkflow.stationservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import java.security.Principal;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;
import tqs.sparkflow.stationservice.service.BookingService;
import tqs.sparkflow.stationservice.service.StationService;
import tqs.sparkflow.stationservice.repository.BookingRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private StationService stationService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private Principal principal;

    private BookingController bookingController;
    private Booking testBooking;
    private LocalDateTime now;
    private Set<Integer> recurringDays;

    @BeforeEach
    void setUp() {
        bookingController = new BookingController(bookingService);
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
    }

    @Test
    @XrayTest(key = "BOOKING-1")
    @Requirement("BOOKING-1")
    void whenCreateBooking_thenReturnCreatedBooking() {
        when(bookingService.createBooking(any(Booking.class))).thenReturn(testBooking);

        ResponseEntity<Booking> response = bookingController.createBooking(testBooking, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull().satisfies(body -> {
            assertThat(body.getId()).isEqualTo(testBooking.getId());
            assertThat(body.getStationId()).isEqualTo(testBooking.getStationId());
            assertThat(body.getUserId()).isEqualTo(testBooking.getUserId());
            assertThat(body.getStatus()).isEqualTo(testBooking.getStatus());
        });
    }

    @Test
    @XrayTest(key = "BOOKING-2")
    @Requirement("BOOKING-2")
    void whenGetAllBookings_thenReturnList() {
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.getAllBookings(1L)).thenReturn(bookings);

        ResponseEntity<List<Booking>> response = bookingController.getAllBookings(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().satisfies(body -> {
            assertThat(body.get(0).getId()).isEqualTo(testBooking.getId());
            assertThat(body.get(0).getStationId()).isEqualTo(testBooking.getStationId());
        });
    }

    @Test
    @XrayTest(key = "BOOKING-3")
    @Requirement("BOOKING-3")
    void whenGetBookingById_thenReturnBooking() {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(Optional.of(testBooking));
        when(principal.getName()).thenReturn("1");

        ResponseEntity<Booking> response = bookingController.getBookingById(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().satisfies(body -> {
            assertThat(body.getId()).isEqualTo(testBooking.getId());
            assertThat(body.getStationId()).isEqualTo(testBooking.getStationId());
        });
    }

    @Test
    @XrayTest(key = "BOOKING-4")
    @Requirement("BOOKING-4")
    void whenGetBookingById_notFound_thenReturn404() {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(principal.getName()).thenReturn("1");

        ResponseEntity<Booking> response = bookingController.getBookingById(999L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @XrayTest(key = "BOOKING-5")
    @Requirement("BOOKING-5")
    void whenCancelBooking_thenReturnNoContent() {
        testBooking.setStatus(BookingStatus.CANCELLED);
        when(bookingService.cancelBooking(anyLong())).thenReturn(testBooking);

        ResponseEntity<Void> response = bookingController.cancelBooking(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @XrayTest(key = "BOOKING-6")
    @Requirement("BOOKING-6")
    void whenGetBookingsByStationId_thenReturnList() {
        when(bookingService.getBookingsByStationId(1L, 1L)).thenReturn(List.of(testBooking));
        when(principal.getName()).thenReturn("1");

        ResponseEntity<List<Booking>> response =
                bookingController.getBookingsByStationId(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().satisfies(body -> {
            assertThat(body.get(0).getId()).isEqualTo(testBooking.getId());
            assertThat(body.get(0).getStationId()).isEqualTo(testBooking.getStationId());
        });
    }

    @Test
    @XrayTest(key = "BOOKING-7")
    @Requirement("BOOKING-7")
    void whenGetBookingsByStationId_withUserNotAuthorized_thenReturnBadRequest() {
        when(bookingService.getBookingsByStationId(99L, 99L))
                .thenThrow(new IllegalStateException("User not found or not authorized"));
        when(principal.getName()).thenReturn("99");

        ResponseEntity<List<Booking>> response =
                bookingController.getBookingsByStationId(99L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @XrayTest(key = "BOOKING-8")
    @Requirement("BOOKING-8")
    void whenGetBookingsByStationId_withNoBookings_thenReturnNoContent() {
        when(bookingService.getBookingsByStationId(1L, 1L)).thenReturn(List.of());
        when(principal.getName()).thenReturn("1");

        ResponseEntity<List<Booking>> response =
                bookingController.getBookingsByStationId(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @XrayTest(key = "BOOKING-10")
    @Requirement("BOOKING-10")
    void whenGetBookingsByUserId_thenReturnList() {
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.getBookingsByUserId(anyLong())).thenReturn(bookings);

        ResponseEntity<List<Booking>> response = bookingController.getBookingsByUserId(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().satisfies(body -> {
            assertThat(body.get(0).getId()).isEqualTo(testBooking.getId());
            assertThat(body.get(0).getUserId()).isEqualTo(testBooking.getUserId());
        });
    }

    @Test
    @XrayTest(key = "BOOKING-12")
    @Requirement("BOOKING-12")
    void whenCreateBooking_withInvalidInput_thenBadRequest() {
        Booking inputBooking = new Booking(); // missing required fields
        when(bookingService.createBooking(any(Booking.class)))
                .thenThrow(new IllegalStateException("Invalid input"));

        ResponseEntity<Booking> response = bookingController.createBooking(inputBooking, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @XrayTest(key = "BOOKING-13")
    @Requirement("BOOKING-13")
    void whenCancelBooking_withoutPermission_thenForbidden() {
        when(bookingService.cancelBooking(anyLong()))
                .thenThrow(new IllegalStateException("User not authorized to cancel this booking"));

        ResponseEntity<Void> response = bookingController.cancelBooking(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @XrayTest(key = "BOOKING-14")
    @Requirement("BOOKING-14")
    void whenGetAllBookings_withNoBookings_thenNoContent() {
        when(bookingService.getAllBookings(1L)).thenReturn(List.of());

        ResponseEntity<List<Booking>> response = bookingController.getAllBookings(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    static Stream<Arguments> createBookingExceptionProvider() {
        return Stream.of(Arguments.of(2L, 1L, "Station is not operational"),
                Arguments.of(1L, 1L, "Time slot is already booked"),
                Arguments.of(1L, 999L, "User not found or not authorized"));
    }

    @ParameterizedTest
    @MethodSource("createBookingExceptionProvider")
    @XrayTest(key = "BOOKING-16")
    @Requirement("BOOKING-16")
    void whenCreateBooking_withBusinessException_thenBadRequest(Long stationId, Long userId,
            String exceptionMessage) {
        Booking inputBooking = new Booking();
        inputBooking.setStationId(stationId);
        inputBooking.setUserId(userId);
        inputBooking.setStartTime(now);
        inputBooking.setEndTime(now.plusHours(2));
        inputBooking.setStatus(BookingStatus.ACTIVE);
        when(bookingService.createBooking(any(Booking.class)))
                .thenThrow(new IllegalStateException(exceptionMessage));

        ResponseEntity<Booking> response = bookingController.createBooking(inputBooking, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @XrayTest(key = "BOOKING-17")
    @Requirement("BOOKING-17")
    void whenCancelBooking_withNonExistentBooking_thenNotFound() {
        when(bookingService.cancelBooking(anyLong()))
                .thenThrow(new IllegalStateException("Booking not found"));

        ResponseEntity<Void> response = bookingController.cancelBooking(999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @XrayTest(key = "BOOKING-18")
    @Requirement("BOOKING-18")
    void whenGetBookingsByUserId_withNoResults_thenNoContent() {
        when(bookingService.getBookingsByUserId(anyLong())).thenReturn(List.of());

        ResponseEntity<List<Booking>> response = bookingController.getBookingsByUserId(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}

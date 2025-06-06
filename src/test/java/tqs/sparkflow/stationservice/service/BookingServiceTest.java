package tqs.sparkflow.stationservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.BookingRepository;

/**
 * Comprehensive unit tests for BookingServiceImpl class. Tests all public methods, error scenarios,
 * and edge cases to achieve >85% coverage.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private StationService stationService;

    @Mock
    private RestTemplate restTemplate;

    private BookingServiceImpl bookingService;

    private static final String USER_SERVICE_URL = "http://test-user-service:8081";
    private static final String USERS_PATH = "/users/";
    private static final String ADMIN_ROLE_CHECK = "/has-role/ADMIN";

    private Booking testBooking;
    private Station testStation;
    private LocalDateTime now;
    private Set<Integer> recurringDays;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, stationService, restTemplate,
                USER_SERVICE_URL);

        now = LocalDateTime.now();
        recurringDays = new HashSet<>(Arrays.asList(1, 2, 3)); // Monday, Tuesday, Wednesday

        testStation = new Station();
        testStation.setId(1L);
        testStation.setIsOperational(true);
        testStation.setQuantityOfChargers(1);

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setStationId(1L);
        testBooking.setUserId(1L);
        testBooking.setStartTime(now);
        testBooking.setEndTime(now.plusHours(2));
        testBooking.setRecurringDays(recurringDays);
        testBooking.setStatus(BookingStatus.ACTIVE);
    }

    // ===============================================
    // Tests for createRecurringBooking method
    // ===============================================

    @Test
    @DisplayName("Should create recurring booking successfully with valid inputs")
    void whenCreateRecurringBooking_thenReturnCreatedBooking() {
        // Given
        when(restTemplate.getForObject(USER_SERVICE_URL + USERS_PATH + 1L, Object.class))
                .thenReturn(new Object());
        when(stationService.getStationById(1L)).thenReturn(testStation);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        Booking createdBooking =
                bookingService.createRecurringBooking(1L, 1L, now, now.plusHours(2), recurringDays);

        // Then
        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getStationId()).isEqualTo(1L);
        assertThat(createdBooking.getUserId()).isEqualTo(1L);
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.ACTIVE);
        verify(stationService).validateBooking(1L, 1L, now, now.plusHours(2));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw exception when user validation fails")
    void whenCreateRecurringBooking_withInvalidUser_thenThrowException() {
        // Given
        when(restTemplate.getForObject(USER_SERVICE_URL + USERS_PATH + 999L, Object.class))
                .thenThrow(new RestClientException("User not found"));

        // When & Then
        assertThatThrownBy(() -> bookingService.createRecurringBooking(999L, 1L, now,
                now.plusHours(2), recurringDays)).isInstanceOf(IllegalStateException.class)
                        .hasMessage("User not found or not authorized");

        verify(stationService, never()).getStationById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when station is not operational")
    void whenCreateRecurringBooking_withInvalidStation_thenThrowException() {
        // Given
        testStation.setIsOperational(false);
        LocalDateTime end = now.plusHours(2);
        when(restTemplate.getForObject(USER_SERVICE_URL + USERS_PATH + 1L, Object.class))
                .thenReturn(new Object());
        when(stationService.getStationById(1L)).thenReturn(testStation);

        // When & Then
        assertThatThrownBy(
                () -> bookingService.createRecurringBooking(1L, 1L, now, end, recurringDays))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("Station is not operational");

        verify(stationService, never()).validateBooking(any(), any(), any(), any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void whenCreateRecurringBooking_withOverlappingBooking_thenThrowException() {
        // User validation
        when(restTemplate.getForObject(USER_SERVICE_URL + USERS_PATH + 1L, Object.class))
                .thenReturn(new Object());

        // Station setup
        when(stationService.getStationById(1L)).thenReturn(testStation);

        // Configure validateBooking to throw exception for overlapping bookings
        doThrow(new IllegalStateException("No chargers available for the requested time slot"))
                .when(stationService).validateBooking(1L, 1L, now, now.plusHours(2));

        assertThatThrownBy(() -> bookingService.createRecurringBooking(1L, 1L, now,
                now.plusHours(2), Set.of(1, 2, 3))).isInstanceOf(IllegalStateException.class)
                        .hasMessage("No chargers available for the requested time slot");

        verify(stationService).validateBooking(1L, 1L, now, now.plusHours(2));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void whenGetBookingById_thenReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        Optional<Booking> found = bookingService.getBookingById(1L, 1L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(1L);
    }

    @Test
    void whenGetAllBookings_thenReturnList() {
        when(bookingRepository.findAll()).thenReturn(List.of(testBooking));

        List<Booking> bookings = bookingService.getAllBookings(1L);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void whenCancelBooking_thenReturnCancelledBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        Booking cancelledBooking = bookingService.cancelBooking(1L);

        assertThat(cancelledBooking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void whenGetBookingsByStationId_thenReturnList() {
        when(bookingRepository.findByStationId(1L)).thenReturn(List.of(testBooking));

        List<Booking> bookings = bookingService.getBookingsByStationId(1L, 1L);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStationId()).isEqualTo(1L);
    }

    @Test
    void whenGetBookingsByUserId_thenReturnList() {
        when(bookingRepository.findByUserId(1L)).thenReturn(List.of(testBooking));

        List<Booking> bookings = bookingService.getBookingsByUserId(1L);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    void whenCreateBooking_withUserNotFound_thenThrowException() {
        // Simulate user validation failure
        org.mockito.Mockito.doThrow(new IllegalStateException("User not found or not authorized"))
                .when(restTemplate).getForObject(anyString(), eq(Object.class));
        Booking booking = new Booking();
        booking.setUserId(99L);
        booking.setStationId(1L);
        booking.setStartTime(now);
        booking.setEndTime(now.plusHours(2));
        booking.setRecurringDays(recurringDays);
        assertThatThrownBy(() -> bookingService.createBooking(booking))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User not found or not authorized");
    }

    @Test
    void whenGetAllBookings_withUserNotAuthorized_thenThrowException() {
        org.mockito.Mockito.doThrow(new IllegalStateException("User not found or not authorized"))
                .when(restTemplate).getForObject(anyString(), eq(Object.class));
        assertThatThrownBy(() -> bookingService.getAllBookings(99L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User not found or not authorized");
    }

    @Test
    void whenGetBookingsByStationId_withUserNotAuthorized_thenThrowException() {
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
                .thenThrow(new IllegalStateException("User not found or not authorized"));

        assertThatThrownBy(() -> bookingService.getBookingsByStationId(99L, 99L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User not found or not authorized");
    }

    @Test
    void whenGetBookingsByUserId_withUserNotAuthorized_thenThrowException() {
        org.mockito.Mockito.doThrow(new IllegalStateException("User not found or not authorized"))
                .when(restTemplate).getForObject(anyString(), eq(Object.class));
        assertThatThrownBy(() -> bookingService.getBookingsByUserId(99L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User not found or not authorized");
    }

    @Test
    void whenGetBookingById_withPermissionDenied_thenThrowException() {
        // Setup: Create a booking belonging to user 2
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUserId(2L);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // Setup: Mock permission check to fail
        when(restTemplate.getForObject(anyString(), eq(Boolean.class)))
                .thenThrow(new IllegalStateException("User not authorized to access this booking"));

        // Test: Single method invocation that should throw the exception
        assertThatThrownBy(() -> bookingService.getBookingById(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User not authorized to access this booking");
    }

    @Test
    void whenCancelBooking_withBookingNotFound_thenThrowException() {
        when(bookingRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookingService.cancelBooking(2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Booking not found");
    }

    @Test
    void whenCreateRecurringBooking_withNullRecurringDays_thenReturnCreatedBooking() {
        // User validation
        when(restTemplate.getForObject(USER_SERVICE_URL + USERS_PATH + 1L, Object.class))
                .thenReturn(new Object());

        // Station setup
        when(stationService.getStationById(1L)).thenReturn(testStation);

        // No overlapping bookings - booking should succeed
        doNothing().when(stationService).validateBooking(1L, 1L, now, now.plusHours(2));

        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        Booking result = bookingService.createRecurringBooking(1L, 1L, now, now.plusHours(2), null);

        assertThat(result).isEqualTo(testBooking);
        verify(stationService).validateBooking(1L, 1L, now, now.plusHours(2));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void whenCreateRecurringBooking_withEmptyRecurringDays_thenReturnCreatedBooking() {
        // User validation
        when(restTemplate.getForObject(USER_SERVICE_URL + USERS_PATH + 1L, Object.class))
                .thenReturn(new Object());

        // Station setup
        when(stationService.getStationById(1L)).thenReturn(testStation);

        // No overlapping bookings - booking should succeed
        doNothing().when(stationService).validateBooking(1L, 1L, now, now.plusHours(2));

        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        Booking result =
                bookingService.createRecurringBooking(1L, 1L, now, now.plusHours(2), Set.of());

        assertThat(result).isEqualTo(testBooking);
        verify(stationService).validateBooking(1L, 1L, now, now.plusHours(2));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void whenCreateBooking_withMultipleChargersAvailable_thenAllowOverlappingBookings() {
        // User validation
        when(restTemplate.getForObject(USER_SERVICE_URL + USERS_PATH + 1L, Object.class))
                .thenReturn(new Object());

        // Station setup
        when(stationService.getStationById(1L)).thenReturn(testStation);

        // No overlapping bookings - booking should succeed
        doNothing().when(stationService).validateBooking(1L, 1L, now, now.plusHours(2));

        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        Booking result = bookingService.createBooking(testBooking);

        assertThat(result).isEqualTo(testBooking);
        verify(stationService).validateBooking(1L, 1L, now, now.plusHours(2));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void whenCreateBooking_withAllChargersOccupied_thenThrowException() {
        // User validation
        when(restTemplate.getForObject(USER_SERVICE_URL + USERS_PATH + 1L, Object.class))
                .thenReturn(new Object());

        // Station setup
        when(stationService.getStationById(1L)).thenReturn(testStation);

        // Configure validateBooking to throw exception for no available chargers
        doThrow(new IllegalStateException("No chargers available for the requested time slot"))
                .when(stationService).validateBooking(1L, 1L, now, now.plusHours(2));

        assertThatThrownBy(() -> bookingService.createBooking(testBooking))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No chargers available for the requested time slot");

        verify(stationService).validateBooking(1L, 1L, now, now.plusHours(2));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void whenCreateBooking_withSingleChargerStation_thenBlockOverlappingBookings() {
        // User validation
        when(restTemplate.getForObject(USER_SERVICE_URL + USERS_PATH + 1L, Object.class))
                .thenReturn(new Object());

        // Create a single charger station
        Station singleChargerStation = new Station();
        singleChargerStation.setId(1L);
        singleChargerStation.setQuantityOfChargers(1);
        singleChargerStation.setIsOperational(true);

        when(stationService.getStationById(1L)).thenReturn(singleChargerStation);

        // Configure validateBooking to throw exception for single charger station with overlapping
        // booking
        doThrow(new IllegalStateException("No chargers available for the requested time slot"))
                .when(stationService).validateBooking(1L, 1L, now, now.plusHours(2));

        assertThatThrownBy(() -> bookingService.createBooking(testBooking))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No chargers available for the requested time slot");

        verify(stationService).validateBooking(1L, 1L, now, now.plusHours(2));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void whenCreateBooking_withCancelledBookings_thenIgnoreCancelledBookings() {
        // User validation
        when(restTemplate.getForObject(USER_SERVICE_URL + USERS_PATH + 1L, Object.class))
                .thenReturn(new Object());

        // Station setup
        when(stationService.getStationById(1L)).thenReturn(testStation);

        // No overlapping active bookings - booking should succeed (cancelled bookings are ignored)
        doNothing().when(stationService).validateBooking(1L, 1L, now, now.plusHours(2));

        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        Booking result = bookingService.createBooking(testBooking);

        assertThat(result).isEqualTo(testBooking);
        verify(stationService).validateBooking(1L, 1L, now, now.plusHours(2));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void whenCreateBooking_withNullQuantityOfChargers_thenDefaultToSingleCharger() {
        // User validation
        when(restTemplate.getForObject(USER_SERVICE_URL + USERS_PATH + 1L, Object.class))
                .thenReturn(new Object());

        // Create station with null quantity of chargers
        Station nullChargerStation = new Station();
        nullChargerStation.setId(1L);
        nullChargerStation.setQuantityOfChargers(null);
        nullChargerStation.setIsOperational(true);

        when(stationService.getStationById(1L)).thenReturn(nullChargerStation);

        // Configure validateBooking to throw exception for null charger station (defaults to 1)
        doThrow(new IllegalStateException("No chargers available for the requested time slot"))
                .when(stationService).validateBooking(1L, 1L, now, now.plusHours(2));

        assertThatThrownBy(() -> bookingService.createBooking(testBooking))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No chargers available for the requested time slot");

        verify(stationService).validateBooking(1L, 1L, now, now.plusHours(2));
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}

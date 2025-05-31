package tqs.sparkflow.stationservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.BookingRepository;

/** Tests for the BookingService class. */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private StationService stationService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BookingServiceImpl bookingService;

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
    }

    @Test
    void whenCreateRecurringBooking_thenReturnCreatedBooking() {
        when(stationService.getStationById(1L)).thenReturn(testStation);
        when(bookingRepository.findOverlappingBookings(any(), any(), any())).thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        Booking createdBooking = bookingService.createRecurringBooking(1L, 1L, now, now.plusHours(2), recurringDays);

        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getStationId()).isEqualTo(1L);
        assertThat(createdBooking.getUserId()).isEqualTo(1L);
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.ACTIVE);
    }

    @Test
    void whenCreateRecurringBooking_withInvalidStation_thenThrowException() {
        testStation.setIsOperational(false);
        when(stationService.getStationById(1L)).thenReturn(testStation);

        assertThatThrownBy(() -> 
            bookingService.createRecurringBooking(1L, 1L, now, now.plusHours(2), recurringDays))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Station is not operational");
    }

    @Test
    void whenCreateRecurringBooking_withOverlappingBooking_thenThrowException() {
        when(stationService.getStationById(1L)).thenReturn(testStation);
        when(bookingRepository.findOverlappingBookings(any(), any(), any()))
            .thenReturn(List.of(testBooking));

        assertThatThrownBy(() -> 
            bookingService.createRecurringBooking(1L, 1L, now, now.plusHours(2), recurringDays))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Time slot is already booked");
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

        List<Booking> bookings = bookingService.getBookingsByStationId(1L);

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
        org.mockito.Mockito.doThrow(new IllegalStateException("User not found or not authorized"))
            .when(restTemplate).getForObject(anyString(), eq(Object.class));
        assertThatThrownBy(() -> bookingService.getBookingsByStationId(99L))
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
        when(stationService.getStationById(1L)).thenReturn(testStation);
        when(bookingRepository.findOverlappingBookings(any(), any(), any())).thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        Booking createdBooking = bookingService.createRecurringBooking(1L, 1L, now, now.plusHours(2), null);
        assertThat(createdBooking).isNotNull();
    }

    @Test
    void whenCreateRecurringBooking_withEmptyRecurringDays_thenReturnCreatedBooking() {
        when(stationService.getStationById(1L)).thenReturn(testStation);
        when(bookingRepository.findOverlappingBookings(any(), any(), any())).thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        Booking createdBooking = bookingService.createRecurringBooking(1L, 1L, now, now.plusHours(2), new java.util.HashSet<>());
        assertThat(createdBooking).isNotNull();
    }
} 
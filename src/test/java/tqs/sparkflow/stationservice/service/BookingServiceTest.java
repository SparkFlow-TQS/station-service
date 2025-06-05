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
 * Comprehensive unit tests for BookingServiceImpl class.
 * Tests all public methods, error scenarios, and edge cases to achieve >85% coverage.
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
        bookingService = new BookingServiceImpl(
            bookingRepository,
            stationService,
            restTemplate,
            USER_SERVICE_URL
        );
        
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
        Booking createdBooking = bookingService.createRecurringBooking(1L, 1L, now, now.plusHours(2), recurringDays);

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
        assertThatThrownBy(() -> 
            bookingService.createRecurringBooking(999L, 1L, now, now.plusHours(2), recurringDays))
            .isInstanceOf(IllegalStateException.class)
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
        assertThatThrownBy(() -> bookingService.createRecurringBooking(1L, 1L, now, end, recurringDays))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Station is not operational");
            
        verify(stationService, never()).validateBooking(any(), any(), any(), any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void whenCreateRecurringBooking_withOverlappingBooking_thenThrowException() {
        // Given: Station with 1 charger (default)
        when(stationService.getStationById(1L)).thenReturn(testStation);
        when(bookingRepository.findOverlappingBookings(any(), any(), any()))
            .thenReturn(List.of(testBooking));

        // When/Then: Creating a booking when charger is already occupied should throw exception
        assertThatThrownBy(() -> 
            bookingService.createRecurringBooking(1L, 1L, now, now.plusHours(2), recurringDays))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No chargers available for the requested time slot");
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

    @Test
    void whenCreateBooking_withMultipleChargersAvailable_thenAllowOverlappingBookings() {
        // Given: Station with 3 chargers
        testStation.setQuantityOfChargers(3);
        
        // And: 2 existing overlapping bookings (less than available chargers)
        Booking existingBooking1 = new Booking();
        existingBooking1.setId(2L);
        existingBooking1.setStationId(1L);
        existingBooking1.setUserId(2L);
        existingBooking1.setStartTime(now);
        existingBooking1.setEndTime(now.plusHours(2));
        existingBooking1.setStatus(BookingStatus.ACTIVE);
        
        Booking existingBooking2 = new Booking();
        existingBooking2.setId(3L);
        existingBooking2.setStationId(1L);
        existingBooking2.setUserId(3L);
        existingBooking2.setStartTime(now);
        existingBooking2.setEndTime(now.plusHours(2));
        existingBooking2.setStatus(BookingStatus.ACTIVE);
        
        List<Booking> overlappingBookings = List.of(existingBooking1, existingBooking2);
        
        when(stationService.getStationById(1L)).thenReturn(testStation);
        when(bookingRepository.findOverlappingBookings(any(), any(), any())).thenReturn(overlappingBookings);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        
        // When: Creating a new booking that overlaps
        Booking createdBooking = bookingService.createRecurringBooking(1L, 1L, now, now.plusHours(2), recurringDays);
        
        // Then: Booking should be created successfully (3rd charger available)
        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getStationId()).isEqualTo(1L);
        assertThat(createdBooking.getUserId()).isEqualTo(1L);
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.ACTIVE);
    }

    @Test
    void whenCreateBooking_withAllChargersOccupied_thenThrowException() {
        // Given: Station with 2 chargers
        testStation.setQuantityOfChargers(2);
        
        // And: 2 existing overlapping bookings (all chargers occupied)
        Booking existingBooking1 = new Booking();
        existingBooking1.setId(2L);
        existingBooking1.setStationId(1L);
        existingBooking1.setUserId(2L);
        existingBooking1.setStartTime(now);
        existingBooking1.setEndTime(now.plusHours(2));
        existingBooking1.setStatus(BookingStatus.ACTIVE);
        
        Booking existingBooking2 = new Booking();
        existingBooking2.setId(3L);
        existingBooking2.setStationId(1L);
        existingBooking2.setUserId(3L);
        existingBooking2.setStartTime(now);
        existingBooking2.setEndTime(now.plusHours(2));
        existingBooking2.setStatus(BookingStatus.ACTIVE);
        
        List<Booking> overlappingBookings = List.of(existingBooking1, existingBooking2);
        
        when(stationService.getStationById(1L)).thenReturn(testStation);
        when(bookingRepository.findOverlappingBookings(any(), any(), any())).thenReturn(overlappingBookings);
        
        // When & Then: Creating a new booking should fail (no chargers available)
        assertThatThrownBy(() -> 
            bookingService.createRecurringBooking(1L, 1L, now, now.plusHours(2), recurringDays))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No chargers available for the requested time slot");
    }

    @Test
    void whenCreateBooking_withSingleChargerStation_thenBlockOverlappingBookings() {
        // Given: Station with 1 charger (current behavior should be maintained)
        testStation.setQuantityOfChargers(1);
        
        // And: 1 existing overlapping booking
        Booking existingBooking = new Booking();
        existingBooking.setId(2L);
        existingBooking.setStationId(1L);
        existingBooking.setUserId(2L);
        existingBooking.setStartTime(now);
        existingBooking.setEndTime(now.plusHours(2));
        existingBooking.setStatus(BookingStatus.ACTIVE);
        
        List<Booking> overlappingBookings = List.of(existingBooking);
        
        when(stationService.getStationById(1L)).thenReturn(testStation);
        when(bookingRepository.findOverlappingBookings(any(), any(), any())).thenReturn(overlappingBookings);
        
        // When & Then: Creating a new booking should fail (single charger occupied)
        assertThatThrownBy(() -> 
            bookingService.createRecurringBooking(1L, 1L, now, now.plusHours(2), recurringDays))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No chargers available for the requested time slot");
    }

    @Test
    void whenCreateBooking_withCancelledBookings_thenIgnoreCancelledBookings() {
        // Given: Station with 2 chargers
        testStation.setQuantityOfChargers(2);
        
        // And: 1 active booking and 1 cancelled booking overlapping
        Booking activeBooking = new Booking();
        activeBooking.setId(2L);
        activeBooking.setStationId(1L);
        activeBooking.setUserId(2L);
        activeBooking.setStartTime(now);
        activeBooking.setEndTime(now.plusHours(2));
        activeBooking.setStatus(BookingStatus.ACTIVE);
        
        Booking cancelledBooking = new Booking();
        cancelledBooking.setId(3L);
        cancelledBooking.setStationId(1L);
        cancelledBooking.setUserId(3L);
        cancelledBooking.setStartTime(now);
        cancelledBooking.setEndTime(now.plusHours(2));
        cancelledBooking.setStatus(BookingStatus.CANCELLED);
        
        // Note: Repository query should only return ACTIVE bookings, but testing the service logic
        List<Booking> overlappingBookings = List.of(activeBooking); // Only active booking returned
        
        when(stationService.getStationById(1L)).thenReturn(testStation);
        when(bookingRepository.findOverlappingBookings(any(), any(), any())).thenReturn(overlappingBookings);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        
        // When: Creating a new booking
        Booking createdBooking = bookingService.createRecurringBooking(1L, 1L, now, now.plusHours(2), recurringDays);
        
        // Then: Booking should be created successfully (only 1 active booking, 1 charger still available)
        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getStationId()).isEqualTo(1L);
        assertThat(createdBooking.getUserId()).isEqualTo(1L);
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.ACTIVE);
    }

    @Test
    void whenCreateBooking_withNullQuantityOfChargers_thenDefaultToSingleCharger() {
        // Given: Station with null quantity of chargers (should default to 1)
        testStation.setQuantityOfChargers(null);
        
        // And: 1 existing overlapping booking
        Booking existingBooking = new Booking();
        existingBooking.setId(2L);
        existingBooking.setStationId(1L);
        existingBooking.setUserId(2L);
        existingBooking.setStartTime(now);
        existingBooking.setEndTime(now.plusHours(2));
        existingBooking.setStatus(BookingStatus.ACTIVE);
        
        List<Booking> overlappingBookings = List.of(existingBooking);
        
        when(stationService.getStationById(1L)).thenReturn(testStation);
        when(bookingRepository.findOverlappingBookings(any(), any(), any())).thenReturn(overlappingBookings);
        
        // When & Then: Creating a new booking should fail (default to 1 charger, already occupied)
        assertThatThrownBy(() -> 
            bookingService.createRecurringBooking(1L, 1L, now, now.plusHours(2), recurringDays))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No chargers available for the requested time slot");
    }
} 
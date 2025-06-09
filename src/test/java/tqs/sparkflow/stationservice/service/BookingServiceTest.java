package tqs.sparkflow.stationservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import tqs.sparkflow.stationservice.repository.StationRepository;

/**
 * Comprehensive unit tests for BookingServiceImpl class. Tests all public methods, error scenarios,
 * and edge cases to achieve >85% coverage.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

        @Mock
        private BookingRepository bookingRepository;

        @Mock
        private StationRepository stationRepository;

        @Mock
        private RestTemplate restTemplate;

        private BookingServiceImpl bookingService;

        private static final String USER_SERVICE_URL = "http://test-user-service:8081";

        private Booking testBooking;
        private Station testStation;
        private LocalDateTime now;
        private Set<Integer> recurringDays;

        @BeforeEach
        void setUp() {
                bookingService = new BookingServiceImpl(bookingRepository, stationRepository,
                                restTemplate, USER_SERVICE_URL);

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
                when(stationRepository.findById(1L)).thenReturn(Optional.of(testStation));
                when(bookingRepository.findOverlappingBookings(any(), any(), any()))
                                .thenReturn(List.of());
                when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

                // When
                Booking createdBooking = bookingService.createRecurringBooking(1L, 1L, now,
                                now.plusHours(2), recurringDays);

                // Then
                assertThat(createdBooking).isNotNull();
                assertThat(createdBooking.getStationId()).isEqualTo(1L);
                assertThat(createdBooking.getUserId()).isEqualTo(1L);
                assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.ACTIVE);
                verify(bookingRepository).save(any(Booking.class));
        }

        @Test
        @DisplayName("Should throw exception when station is not operational")
        void whenCreateRecurringBooking_withInvalidStation_thenThrowException() {
                // Given
                testStation.setIsOperational(false);
                when(stationRepository.findById(1L)).thenReturn(Optional.of(testStation));
                LocalDateTime endTime = now.plusHours(2);

                // When & Then
                assertThatThrownBy(() -> bookingService.createRecurringBooking(1L, 1L, now, endTime,
                                recurringDays)).isInstanceOf(IllegalStateException.class)
                                                .hasMessageContaining("Station is not operational");

                verify(bookingRepository, never()).save(any());
        }

        @Test
        void whenCreateRecurringBooking_withOverlappingBooking_thenThrowException() {
                // Given
                when(stationRepository.findById(1L)).thenReturn(Optional.of(testStation));
                when(bookingRepository.findOverlappingBookings(any(), any(), any()))
                                .thenReturn(List.of(testBooking));
                LocalDateTime endTime = now.plusHours(2);

                // When & Then
                assertThatThrownBy(() -> bookingService.createRecurringBooking(1L, 1L, now, endTime,
                                recurringDays)).isInstanceOf(IllegalStateException.class)
                                                .hasMessage("There are overlapping bookings for this time slot");

                verify(bookingRepository, never()).save(any());
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
                // Given
                when(restTemplate.getForObject(anyString(), eq(Object.class)))
                                .thenThrow(new RestClientException("User not found"));
                LocalDateTime endTime = now.plusHours(2);

                // When & Then
                assertThatThrownBy(() -> bookingService.createRecurringBooking(99L, 1L, now,
                                endTime, recurringDays)).isInstanceOf(IllegalStateException.class)
                                                .hasMessageContaining(
                                                                "User not found or not authorized");
        }

        @Test
        void whenGetAllBookings_withUserNotAuthorized_thenThrowException() {
                // Given
                when(restTemplate.getForObject(anyString(), eq(Object.class)))
                                .thenThrow(new RestClientException("User not found"));

                // When & Then
                assertThatThrownBy(() -> bookingService.getAllBookings(99L))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("User not found or not authorized");
        }

        @Test
        void whenGetBookingsByStationId_withUserNotAuthorized_thenThrowException() {
                // Given
                when(restTemplate.getForObject(anyString(), eq(Object.class)))
                                .thenThrow(new RestClientException("User not found"));

                // When & Then
                assertThatThrownBy(() -> bookingService.getBookingsByStationId(99L, 99L))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("User not found or not authorized");
        }

        @Test
        void whenGetBookingsByUserId_withUserNotAuthorized_thenThrowException() {
                // Given
                when(restTemplate.getForObject(anyString(), eq(Object.class)))
                                .thenThrow(new RestClientException("User not found"));

                // When & Then
                assertThatThrownBy(() -> bookingService.getBookingsByUserId(99L))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("User not found or not authorized");
        }

        @Test
        void whenGetBookingById_withPermissionDenied_thenThrowException() {
                // Given
                Booking booking = new Booking();
                booking.setId(1L);
                booking.setUserId(2L);
                when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
                when(restTemplate.getForObject(anyString(), eq(Object.class)))
                                .thenThrow(new RestClientException(
                                                "User not authorized to access this booking"));

                // When & Then
                assertThatThrownBy(() -> bookingService.getBookingById(1L, 1L))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessage("User not authorized to access this booking");
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
                // Given
                when(stationRepository.findById(1L)).thenReturn(Optional.of(testStation));
                when(bookingRepository.findOverlappingBookings(any(), any(), any()))
                                .thenReturn(List.of());
                when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

                // When
                Booking result = bookingService.createRecurringBooking(1L, 1L, now,
                                now.plusHours(2), null);

                // Then
                assertThat(result).isEqualTo(testBooking);
                verify(bookingRepository).save(any(Booking.class));
        }

        @Test
        void whenCreateRecurringBooking_withEmptyRecurringDays_thenReturnCreatedBooking() {
                // Given
                when(stationRepository.findById(1L)).thenReturn(Optional.of(testStation));
                when(bookingRepository.findOverlappingBookings(any(), any(), any()))
                                .thenReturn(List.of());
                when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

                // When
                Booking result = bookingService.createRecurringBooking(1L, 1L, now,
                                now.plusHours(2), Set.of());

                // Then
                assertThat(result).isEqualTo(testBooking);
                verify(bookingRepository).save(any(Booking.class));
        }

        @Test
        void whenCreateBooking_withMultipleChargersAvailable_thenAllowOverlappingBookings() {
                // Given
                testStation.setQuantityOfChargers(2);
                when(stationRepository.findById(1L)).thenReturn(Optional.of(testStation));
                when(bookingRepository.findOverlappingBookings(any(), any(), any()))
                                .thenReturn(List.of());
                when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

                // When
                Booking result = bookingService.createRecurringBooking(1L, 1L, now,
                                now.plusHours(2), recurringDays);

                // Then
                assertThat(result).isEqualTo(testBooking);
                verify(bookingRepository).save(any(Booking.class));
        }

        @Test
        void whenCreateBooking_withAllChargersOccupied_thenThrowException() {
                // Given
                testStation.setQuantityOfChargers(2);
                when(stationRepository.findById(1L)).thenReturn(Optional.of(testStation));
                LocalDateTime endTime = now.plusHours(2);

                // Two overlapping bookings - all chargers occupied
                List<Booking> overlappingBookings = List.of(testBooking, testBooking);
                when(bookingRepository.findOverlappingBookings(any(), any(), any()))
                                .thenReturn(overlappingBookings);

                // When & Then
                assertThatThrownBy(() -> bookingService.createRecurringBooking(1L, 1L, now, endTime,
                                recurringDays)).isInstanceOf(IllegalStateException.class)
                                                .hasMessage("There are overlapping bookings for this time slot");

                verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        void whenCreateBooking_withSingleChargerStation_thenBlockOverlappingBookings() {
                // Given
                testStation.setQuantityOfChargers(1);
                when(stationRepository.findById(1L)).thenReturn(Optional.of(testStation));
                LocalDateTime endTime = now.plusHours(2);

                // One overlapping booking - charger occupied
                when(bookingRepository.findOverlappingBookings(any(), any(), any()))
                                .thenReturn(List.of(testBooking));

                // When & Then
                assertThatThrownBy(() -> bookingService.createRecurringBooking(1L, 1L, now, endTime,
                                recurringDays)).isInstanceOf(IllegalStateException.class)
                                                .hasMessage("There are overlapping bookings for this time slot");

                verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        void whenCreateBooking_withCancelledBookings_thenThrowException() {
                // Given
                when(stationRepository.findById(1L)).thenReturn(Optional.of(testStation));
                LocalDateTime endTime = now.plusHours(2);

                // Create a cancelled booking
                Booking cancelledBooking = new Booking();
                cancelledBooking.setId(2L);
                cancelledBooking.setStationId(1L);
                cancelledBooking.setUserId(1L);
                cancelledBooking.setStartTime(now);
                cancelledBooking.setEndTime(endTime);
                cancelledBooking.setStatus(BookingStatus.CANCELLED);

                // Mock user validation
                when(restTemplate.getForObject(anyString(), eq(Object.class)))
                                .thenReturn(new Object());

                // Mock findOverlappingBookings to return the cancelled booking
                when(bookingRepository.findOverlappingBookings(any(), any(), any()))
                                .thenReturn(List.of(cancelledBooking));

                // When & Then
                assertThatThrownBy(() -> bookingService.createRecurringBooking(1L, 1L, now, endTime,
                                recurringDays)).isInstanceOf(IllegalStateException.class)
                                                .hasMessage("There are overlapping bookings for this time slot");

                verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        void whenCreateBooking_withNullQuantityOfChargers_thenDefaultToSingleCharger() {
                // Given
                testStation.setQuantityOfChargers(null);
                when(stationRepository.findById(1L)).thenReturn(Optional.of(testStation));
                LocalDateTime endTime = now.plusHours(2);

                // One overlapping booking - charger occupied
                when(bookingRepository.findOverlappingBookings(any(), any(), any()))
                                .thenReturn(List.of(testBooking));

                // When & Then
                assertThatThrownBy(() -> bookingService.createRecurringBooking(1L, 1L, now, endTime,
                                recurringDays)).isInstanceOf(IllegalStateException.class)
                                                .hasMessage("There are overlapping bookings for this time slot");

                verify(bookingRepository, never()).save(any(Booking.class));
        }
}

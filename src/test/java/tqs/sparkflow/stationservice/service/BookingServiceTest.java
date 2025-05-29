package tqs.sparkflow.stationservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.Station;
import tqs.sparkflow.stationservice.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/** Tests for the BookingService class. */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void whenCreatingRecurringBooking_thenBookingIsCreated() {
        // Arrange
        String userId = "user123";
        Long stationId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2024, 3, 18, 8, 0); // Monday 8:00 AM
        LocalDateTime endTime = LocalDateTime.of(2024, 3, 18, 9, 0);   // Monday 9:00 AM
        Set<Integer> recurringDays = Set.of(1, 2, 3, 4, 5); // Monday to Friday

        Station station = new Station();
        station.setId(stationId);
        station.setIsOperational(true);

        when(stationService.getStationById(stationId)).thenReturn(station);
        when(bookingRepository.findOverlappingBookings(stationId, startTime, endTime))
            .thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Booking booking = bookingService.createRecurringBooking(
            userId, stationId, startTime, endTime, recurringDays);

        // Assert
        assertThat(booking).isNotNull();
        assertThat(booking.getUserId()).isEqualTo(userId);
        assertThat(booking.getStationId()).isEqualTo(stationId);
        assertThat(booking.getStartTime()).isEqualTo(startTime);
        assertThat(booking.getEndTime()).isEqualTo(endTime);
        assertThat(booking.getRecurringDays()).isEqualTo(recurringDays);
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.ACTIVE);
    }

    @Test
    void whenCreatingRecurringBookingWithNonOperationalStation_thenThrowException() {
        // Arrange
        String userId = "user123";
        Long stationId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2024, 3, 18, 8, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 3, 18, 9, 0);
        Set<Integer> recurringDays = Set.of(1, 2, 3, 4, 5);

        Station station = new Station();
        station.setId(stationId);
        station.setIsOperational(false);

        when(stationService.getStationById(stationId)).thenReturn(station);

        // Act & Assert
        assertThatThrownBy(() -> bookingService.createRecurringBooking(
            userId, stationId, startTime, endTime, recurringDays))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Station is not operational");
    }

    @Test
    void whenCreatingRecurringBookingWithOverlappingBooking_thenThrowException() {
        // Arrange
        String userId = "user123";
        Long stationId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2024, 3, 18, 8, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 3, 18, 9, 0);
        Set<Integer> recurringDays = Set.of(1, 2, 3, 4, 5);

        Station station = new Station();
        station.setId(stationId);
        station.setIsOperational(true);

        Booking existingBooking = new Booking();
        existingBooking.setId(2L);
        existingBooking.setStationId(stationId);
        existingBooking.setStartTime(startTime);
        existingBooking.setEndTime(endTime);

        when(stationService.getStationById(stationId)).thenReturn(station);
        when(bookingRepository.findOverlappingBookings(stationId, startTime, endTime))
            .thenReturn(List.of(existingBooking));

        // Act & Assert
        assertThatThrownBy(() -> bookingService.createRecurringBooking(
            userId, stationId, startTime, endTime, recurringDays))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Time slot is already booked");
    }

    @Test
    void whenCreatingRecurringBookingWithInvalidTimeRange_thenThrowException() {
        // Arrange
        String userId = "user123";
        Long stationId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2024, 3, 18, 9, 0); // Later time
        LocalDateTime endTime = LocalDateTime.of(2024, 3, 18, 8, 0);   // Earlier time
        Set<Integer> recurringDays = Set.of(1, 2, 3, 4, 5);

        // Act & Assert
        assertThatThrownBy(() -> bookingService.createRecurringBooking(
            userId, stationId, startTime, endTime, recurringDays))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Start time must be before end time");
    }

    @Test
    void whenCreatingRecurringBookingWithEmptyRecurringDays_thenThrowException() {
        // Arrange
        String userId = "user123";
        Long stationId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2024, 3, 18, 8, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 3, 18, 9, 0);
        Set<Integer> recurringDays = Set.of();

        // Act & Assert
        assertThatThrownBy(() -> bookingService.createRecurringBooking(
            userId, stationId, startTime, endTime, recurringDays))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Recurring days cannot be null or empty");
    }
} 
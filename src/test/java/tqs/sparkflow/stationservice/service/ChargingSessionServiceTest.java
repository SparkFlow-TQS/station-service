package tqs.sparkflow.stationservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tqs.sparkflow.stationservice.exception.ChargingSessionNotFoundException;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.model.BookingStatus;
import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.repository.BookingRepository;
import tqs.sparkflow.stationservice.repository.ChargingSessionRepository;

@ExtendWith(MockitoExtension.class)
class ChargingSessionServiceTest {

        @Mock
        private ChargingSessionRepository chargingSessionRepository;

        @Mock
        private BookingRepository bookingRepository;

        @Mock
        private StationService stationService;

        private ChargingSessionService chargingSessionService;

        @BeforeEach
        void setUp() {
                chargingSessionService = new ChargingSessionService(chargingSessionRepository,
                                bookingRepository, stationService);
        }

        @Test
        void whenCreateSessionAndFreeChargersExists_thenSessionIsCreatedAndStarted() {
                // Given
                String stationId = "1";
                String userId = "1";
                ChargingSession session = new ChargingSession(stationId, userId);
                when(chargingSessionRepository.save(any(ChargingSession.class)))
                                .thenReturn(session);
                when(bookingRepository.findActiveBookingsForStationAtTime(eq(1L),
                                any(LocalDateTime.class))).thenReturn(Arrays.asList());
                doNothing().when(stationService).validateSessionStart(1L, 1L);

                // When
                ChargingSession result = chargingSessionService.createSession(stationId, userId);

                // Then
                assertNotNull(result);
                assertFalse(result.isFinished());
                assertNotNull(result.getStartTime());
                verify(chargingSessionRepository).save(any(ChargingSession.class));
                verify(stationService).validateSessionStart(1L, 1L);
        }

        @Test
        void whenCreateSessionAndNoFreeChargersExists_thenSessionIsNotCreated() {
                // Given
                String stationId = "1";
                String userId = "123";
                doThrow(new IllegalStateException(
                                "Cannot start session: no booking or free chargers available"))
                                                .when(stationService)
                                                .validateSessionStart(1L, 123L);

                // When/Then
                IllegalStateException exception = assertThrows(IllegalStateException.class,
                                () -> chargingSessionService.createSession(stationId, userId));

                assertEquals("Cannot start session: no booking or free chargers available",
                                exception.getMessage());
                verify(stationService).validateSessionStart(1L, 123L);
                verify(chargingSessionRepository, never()).save(any(ChargingSession.class));
        }

        @Test
        void whenCreateSession_thenValidationIsCalledWithCorrectParameters() {
                // Given
                String stationId = "123";
                String userId = "456";
                ChargingSession session = new ChargingSession(stationId, userId);
                when(chargingSessionRepository.save(any(ChargingSession.class)))
                                .thenReturn(session);
                when(bookingRepository.findActiveBookingsForStationAtTime(eq(123L),
                                any(LocalDateTime.class))).thenReturn(Arrays.asList());
                doNothing().when(stationService).validateSessionStart(123L, 456L);

                // When
                chargingSessionService.createSession(stationId, userId);

                // Then
                verify(stationService).validateSessionStart(123L, 456L);
        }

        @Test
        void whenCreateSession_withInvalidUser_thenThrowException() {
                // Given
                String stationId = "1";
                String userId = "1";
                doThrow(new IllegalStateException(
                                "Cannot start session: no booking or free chargers available"))
                                                .when(stationService).validateSessionStart(1L, 1L);

                // When/Then
                IllegalStateException exception = assertThrows(IllegalStateException.class,
                                () -> chargingSessionService.createSession(stationId, userId));

                assertEquals("Cannot start session: no booking or free chargers available",
                                exception.getMessage());
                verify(stationService).validateSessionStart(1L, 1L);
                verify(chargingSessionRepository, never()).save(any(ChargingSession.class));
        }

        @Test
        void whenCreateSession_thenSessionHasCorrectInitialState() {
                // Given
                String stationId = "1";
                String userId = "1";
                ArgumentCaptor<ChargingSession> sessionCaptor =
                                ArgumentCaptor.forClass(ChargingSession.class);
                when(chargingSessionRepository.save(sessionCaptor.capture()))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(bookingRepository.findActiveBookingsForStationAtTime(eq(1L),
                                any(LocalDateTime.class))).thenReturn(Arrays.asList());
                doNothing().when(stationService).validateSessionStart(1L, 1L);

                // When
                ChargingSession result = chargingSessionService.createSession(stationId, userId);

                // Then
                ChargingSession capturedSession = sessionCaptor.getValue();
                assertEquals(stationId, capturedSession.getStationId());
                assertEquals(userId, capturedSession.getUserId());
                assertFalse(capturedSession.isFinished());
                assertNotNull(capturedSession.getStartTime());
                assertNull(capturedSession.getEndTime());

                assertNotNull(result);
                verify(chargingSessionRepository).save(any(ChargingSession.class));
                verify(stationService).validateSessionStart(1L, 1L);
        }

        @Test
        void whenCreateSession_thenClosesUserBooking() {
                // Given
                String stationId = "1";
                String userId = "123";

                ChargingSession session = new ChargingSession(stationId, userId);

                Booking userBooking = new Booking();
                userBooking.setId(1L);
                userBooking.setStationId(Long.valueOf(stationId));
                userBooking.setUserId(Long.valueOf(userId));
                userBooking.setStatus(BookingStatus.ACTIVE);
                userBooking.setStartTime(LocalDateTime.now().minusMinutes(30));
                userBooking.setEndTime(LocalDateTime.now().plusMinutes(30));

                when(chargingSessionRepository.save(any(ChargingSession.class)))
                                .thenReturn(session);
                when(bookingRepository.findActiveBookingsForStationAtTime(eq(1L),
                                any(LocalDateTime.class))).thenReturn(Arrays.asList(userBooking));
                when(bookingRepository.save(any(Booking.class))).thenReturn(userBooking);
                doNothing().when(stationService).validateSessionStart(1L, 123L);

                // When
                ChargingSession result = chargingSessionService.createSession(stationId, userId);

                // Then
                assertNotNull(result);
                assertFalse(result.isFinished());
                verify(bookingRepository).findActiveBookingsForStationAtTime(eq(1L),
                                any(LocalDateTime.class));

                ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
                verify(bookingRepository).save(bookingCaptor.capture());

                Booking savedBooking = bookingCaptor.getValue();
                assertEquals(BookingStatus.COMPLETED, savedBooking.getStatus());
                assertEquals(userBooking.getId(), savedBooking.getId());

                verify(stationService).validateSessionStart(1L, 123L);
        }

        @Test
        void whenCreateSession_withMultipleBookings_thenClosesOnlyUserBooking() {
                // Given
                String stationId = "1";
                String userId = "123";

                ChargingSession session = new ChargingSession(stationId, userId);

                Booking userBooking = new Booking();
                userBooking.setId(1L);
                userBooking.setStationId(1L);
                userBooking.setUserId(123L);
                userBooking.setStatus(BookingStatus.ACTIVE);

                Booking otherUserBooking = new Booking();
                otherUserBooking.setId(2L);
                otherUserBooking.setStationId(1L);
                otherUserBooking.setUserId(456L);
                otherUserBooking.setStatus(BookingStatus.ACTIVE);

                when(chargingSessionRepository.save(any(ChargingSession.class)))
                                .thenReturn(session);
                when(bookingRepository.findActiveBookingsForStationAtTime(eq(1L),
                                any(LocalDateTime.class))).thenReturn(
                                                Arrays.asList(userBooking, otherUserBooking));
                when(bookingRepository.save(any(Booking.class))).thenReturn(userBooking);
                doNothing().when(stationService).validateSessionStart(1L, 123L);

                // When
                chargingSessionService.createSession(stationId, userId);

                // Then
                verify(bookingRepository, times(1)).save(any(Booking.class));

                ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
                verify(bookingRepository).save(bookingCaptor.capture());

                Booking savedBooking = bookingCaptor.getValue();
                assertEquals(BookingStatus.COMPLETED, savedBooking.getStatus());
                assertEquals(123L, savedBooking.getUserId());
        }

        @Test
        void whenCreateSession_withNoActiveBookings_thenNoBookingIsClosed() {
                // Given
                String stationId = "1";
                String userId = "123";
                ChargingSession session = new ChargingSession(stationId, userId);

                when(chargingSessionRepository.save(any(ChargingSession.class)))
                                .thenReturn(session);
                when(bookingRepository.findActiveBookingsForStationAtTime(eq(1L),
                                any(LocalDateTime.class))).thenReturn(Arrays.asList());
                doNothing().when(stationService).validateSessionStart(1L, 123L);

                // When
                chargingSessionService.createSession(stationId, userId);

                // Then
                verify(bookingRepository).findActiveBookingsForStationAtTime(eq(1L),
                                any(LocalDateTime.class));
                verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        void whenEndSession_thenSessionIsCompleted() {
                // Given
                Long sessionId = 1L;
                ChargingSession session = new ChargingSession();
                session.setId(sessionId);
                session.setStartTime(LocalDateTime.now().minusHours(1));
                when(chargingSessionRepository.findById(sessionId))
                                .thenReturn(Optional.of(session));
                when(chargingSessionRepository.save(any(ChargingSession.class)))
                                .thenReturn(session);

                // When
                ChargingSession result = chargingSessionService.endSession(sessionId.toString());

                // Then
                assertNotNull(result);
                assertTrue(result.isFinished());
                assertNotNull(result.getEndTime());
                verify(chargingSessionRepository).save(any(ChargingSession.class));
        }

        @Test
        void whenEndSession_thenEndTimeIsSet() {
                // Given
                Long sessionId = 1L;
                ChargingSession session = new ChargingSession();
                session.setStartTime(LocalDateTime.now());
                LocalDateTime beforeEnd = LocalDateTime.now();

                when(chargingSessionRepository.findById(sessionId))
                                .thenReturn(Optional.of(session));
                when(chargingSessionRepository.save(any(ChargingSession.class)))
                                .thenReturn(session);

                // When
                ChargingSession result = chargingSessionService.endSession(sessionId.toString());

                // Then
                assertNotNull(result);
                assertNotNull(result.getEndTime());
                assertTrue(result.isFinished());
                assertTrue(result.getEndTime().isAfter(beforeEnd)
                                || result.getEndTime().isEqual(beforeEnd));
                assertTrue(result.getEndTime().isAfter(result.getStartTime()));
        }

        @Test
        void whenEndSession_withNonExistentSession_thenThrowException() {
                // Given
                String sessionId = "999";
                when(chargingSessionRepository.findById(Long.valueOf(sessionId)))
                                .thenReturn(Optional.empty());

                // When/Then
                ChargingSessionNotFoundException exception =
                                assertThrows(ChargingSessionNotFoundException.class,
                                                () -> chargingSessionService.endSession(sessionId));

                assertEquals("Session not found: " + sessionId, exception.getMessage());
                verify(chargingSessionRepository, never()).save(any(ChargingSession.class));
        }

        @Test
        void whenEndSession_withAlreadyFinishedSession_thenStillCompletes() {
                // Given
                Long sessionId = 1L;
                ChargingSession session = new ChargingSession();
                session.setId(sessionId);
                session.setFinished(true);
                session.setEndTime(LocalDateTime.now().minusMinutes(30));

                when(chargingSessionRepository.findById(sessionId))
                                .thenReturn(Optional.of(session));
                when(chargingSessionRepository.save(any(ChargingSession.class)))
                                .thenReturn(session);

                // When
                ChargingSession result = chargingSessionService.endSession(sessionId.toString());

                // Then
                assertNotNull(result);
                assertTrue(result.isFinished());
                assertNotNull(result.getEndTime());
                verify(chargingSessionRepository).save(any(ChargingSession.class));
        }

        @Test
        void whenGetSession_thenReturnSession() {
                // Given
                String sessionId = "1";
                ChargingSession session = new ChargingSession();
                session.setId(1L);
                session.setStationId("1");
                session.setUserId("123");
                when(chargingSessionRepository.findById(1L)).thenReturn(Optional.of(session));

                // When
                ChargingSession result = chargingSessionService.getSession(sessionId);

                // Then
                assertNotNull(result);
                assertEquals(session, result);
                assertEquals(1L, result.getId());
                assertEquals("1", result.getStationId());
                assertEquals("123", result.getUserId());
        }

        @Test
        void whenGetSession_withNonExistentSession_thenThrowException() {
                // Given
                String sessionId = "999";
                when(chargingSessionRepository.findById(Long.valueOf(sessionId)))
                                .thenReturn(Optional.empty());

                // When/Then
                ChargingSessionNotFoundException exception =
                                assertThrows(ChargingSessionNotFoundException.class,
                                                () -> chargingSessionService.getSession(sessionId));

                assertEquals("Session not found: " + sessionId, exception.getMessage());
        }

        @Test
        void whenGetSession_withInvalidSessionIdFormat_thenThrowNumberFormatException() {
                // Given
                String invalidSessionId = "invalid";

                // When/Then
                assertThrows(NumberFormatException.class,
                                () -> chargingSessionService.getSession(invalidSessionId));
        }

        @Test
        void whenCreateSession_withInvalidStationIdFormat_thenThrowNumberFormatException() {
                // Given
                String invalidStationId = "invalid";
                String userId = "1";

                // When/Then
                assertThrows(NumberFormatException.class, () -> chargingSessionService
                                .createSession(invalidStationId, userId));
        }

        @Test
        void whenCreateSession_withInvalidUserIdFormat_thenThrowNumberFormatException() {
                // Given
                String stationId = "1";
                String invalidUserId = "invalid";

                // When/Then
                assertThrows(NumberFormatException.class, () -> chargingSessionService
                                .createSession(stationId, invalidUserId));
        }

        @Test
        void whenEndSession_withInvalidSessionIdFormat_thenThrowNumberFormatException() {
                // Given
                String invalidSessionId = "invalid";

                // When/Then
                assertThrows(NumberFormatException.class,
                                () -> chargingSessionService.endSession(invalidSessionId));
        }

        @Test
        void whenCreateSession_thenBookingRepositoryIsCalledWithCorrectParameters() {
                // Given
                String stationId = "42";
                String userId = "123";
                ChargingSession session = new ChargingSession(stationId, userId);

                when(chargingSessionRepository.save(any(ChargingSession.class)))
                                .thenReturn(session);
                when(bookingRepository.findActiveBookingsForStationAtTime(eq(42L),
                                any(LocalDateTime.class))).thenReturn(Arrays.asList());
                doNothing().when(stationService).validateSessionStart(42L, 123L);

                // When
                chargingSessionService.createSession(stationId, userId);

                // Then
                ArgumentCaptor<Long> stationIdCaptor = ArgumentCaptor.forClass(Long.class);
                ArgumentCaptor<LocalDateTime> timeCaptor =
                                ArgumentCaptor.forClass(LocalDateTime.class);
                verify(bookingRepository).findActiveBookingsForStationAtTime(
                                stationIdCaptor.capture(), timeCaptor.capture());

                assertEquals(42L, stationIdCaptor.getValue());
                assertNotNull(timeCaptor.getValue());
                assertTrue(timeCaptor.getValue().isBefore(LocalDateTime.now().plusSeconds(1)));
                assertTrue(timeCaptor.getValue().isAfter(LocalDateTime.now().minusSeconds(1)));
        }
}

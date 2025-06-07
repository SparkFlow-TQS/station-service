package tqs.sparkflow.stationservice.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import tqs.sparkflow.stationservice.dto.CurrentMonthStatisticsDTO;
import tqs.sparkflow.stationservice.dto.MonthlyStatisticsDTO;
import tqs.sparkflow.stationservice.dto.WeeklyStatisticsDTO;
import tqs.sparkflow.stationservice.dto.CostTrendDTO;
import tqs.sparkflow.stationservice.dto.PeriodDetailsDTO;
import tqs.sparkflow.stationservice.dto.RecentSessionDTO;
import tqs.sparkflow.stationservice.dto.ReservationDTO;
import tqs.sparkflow.stationservice.model.ChargingSession;
import tqs.sparkflow.stationservice.model.Booking;
import tqs.sparkflow.stationservice.repository.ChargingSessionRepository;
import tqs.sparkflow.stationservice.repository.BookingRepository;

/**
 * Implementation of StatisticsService for calculating statistics based on finished charging sessions.
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final ChargingSessionRepository chargingSessionRepository;
    private final BookingRepository bookingRepository;

    // Constants for calculations
    private static final double COST_PER_MINUTE = 0.25; // €0.25 per minute
    private static final double KWH_PER_MINUTE = 0.5; // 0.5 kWh per minute (30kW charging rate)
    private static final double CO2_SAVED_PER_KWH = 0.4; // 0.4 kg CO2 saved per kWh

    public StatisticsServiceImpl(ChargingSessionRepository chargingSessionRepository,
                                BookingRepository bookingRepository) {
        this.chargingSessionRepository = chargingSessionRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public CurrentMonthStatisticsDTO getCurrentMonthStatistics(Long userId) {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);

        List<ChargingSession> sessions = getFinishedSessionsForUserInPeriod(userId, startOfMonth, endOfMonth);

        double totalCost = 0.0;
        double totalKwh = 0.0;
        int totalSessions = sessions.size();

        for (ChargingSession session : sessions) {
            long durationMinutes = calculateDurationMinutes(session);
            totalCost += calculateEstimatedCost(durationMinutes);
            totalKwh += estimateKwhConsumed(durationMinutes);
        }

        double co2Saved = calculateCo2Saved(totalKwh);
        double avgCostPerSession = totalSessions > 0 ? totalCost / totalSessions : 0.0;

        return new CurrentMonthStatisticsDTO(totalCost, totalKwh, totalSessions, co2Saved, avgCostPerSession);
    }

    @Override
    public MonthlyStatisticsDTO getMonthlyStatistics(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<ChargingSession> sessions = getFinishedSessionsForUserInPeriod(userId, startOfMonth, endOfMonth);
        List<Booking> bookings = getBookingsForUserInPeriod(userId, startOfMonth, endOfMonth);

        double totalCost = 0.0;
        double totalKwh = 0.0;
        long totalDurationMinutes = 0;

        for (ChargingSession session : sessions) {
            long durationMinutes = calculateDurationMinutes(session);
            totalCost += calculateEstimatedCost(durationMinutes);
            totalKwh += estimateKwhConsumed(durationMinutes);
            totalDurationMinutes += durationMinutes;
        }

        List<ReservationDTO> reservationDTOs = bookings.stream()
                .map(this::convertToReservationDTO)
                .collect(Collectors.toList());

        String monthName = yearMonth.format(DateTimeFormatter.ofPattern("MMM"));
        String fullMonth = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        double durationHours = totalDurationMinutes / 60.0;

        return new MonthlyStatisticsDTO(monthName, fullMonth, totalCost, sessions.size(),
                durationHours, totalKwh, reservationDTOs);
    }

    @Override
    public WeeklyStatisticsDTO getWeeklyStatistics(Long userId, LocalDate startDate) {
        LocalDateTime startOfWeek = startDate.atStartOfDay();
        LocalDateTime endOfWeek = startDate.plusDays(6).atTime(23, 59, 59);

        List<ChargingSession> sessions = getFinishedSessionsForUserInPeriod(userId, startOfWeek, endOfWeek);
        List<Booking> bookings = getBookingsForUserInPeriod(userId, startOfWeek, endOfWeek);

        double totalCost = 0.0;
        for (ChargingSession session : sessions) {
            long durationMinutes = calculateDurationMinutes(session);
            totalCost += calculateEstimatedCost(durationMinutes);
        }

        List<ReservationDTO> reservationDTOs = bookings.stream()
                .map(this::convertToReservationDTO)
                .collect(Collectors.toList());

        String dateRange = startDate.format(DateTimeFormatter.ofPattern("dd/MM")) + " - " +
                startDate.plusDays(6).format(DateTimeFormatter.ofPattern("dd/MM"));

        String weekIdentifier = "Week" + (startDate.getDayOfYear() / 7 + 1);

        return new WeeklyStatisticsDTO(weekIdentifier, sessions.size(), totalCost, dateRange, reservationDTOs);
    }

    @Override
    public List<CostTrendDTO> getCostTrend(Long userId, int months) {
        List<CostTrendDTO> trends = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (int i = months - 1; i >= 0; i--) {
            LocalDate monthDate = currentDate.minusMonths(i);
            YearMonth yearMonth = YearMonth.from(monthDate);
            LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

            List<ChargingSession> sessions = getFinishedSessionsForUserInPeriod(userId, startOfMonth, endOfMonth);

            double totalCost = 0.0;
            for (ChargingSession session : sessions) {
                long durationMinutes = calculateDurationMinutes(session);
                totalCost += calculateEstimatedCost(durationMinutes);
            }

            String monthName = yearMonth.format(DateTimeFormatter.ofPattern("MMM"));
            trends.add(new CostTrendDTO(monthName, totalCost, sessions.size()));
        }

        return trends;
    }

    @Override
    public PeriodDetailsDTO getPeriodDetails(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<ChargingSession> sessions = getFinishedSessionsForUserInPeriod(userId, startDate, endDate);
        List<Booking> bookings = getBookingsForUserInPeriod(userId, startDate, endDate);

        double totalCost = 0.0;
        for (ChargingSession session : sessions) {
            long durationMinutes = calculateDurationMinutes(session);
            totalCost += calculateEstimatedCost(durationMinutes);
        }

        double avgCostPerSession = sessions.size() > 0 ? totalCost / sessions.size() : 0.0;

        List<ReservationDTO> reservationDTOs = bookings.stream()
                .map(this::convertToReservationDTO)
                .collect(Collectors.toList());

        return new PeriodDetailsDTO(bookings.size(), totalCost, avgCostPerSession, reservationDTOs);
    }

    @Override
    public List<RecentSessionDTO> getRecentSessions(Long userId, int limit) {
        List<ChargingSession> sessions = chargingSessionRepository.findRecentSessionsByUser(String.valueOf(userId));
        
        return sessions.stream()
                .limit(limit)
                .map(this::convertToRecentSessionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Double calculateEstimatedCost(long durationMinutes) {
        return durationMinutes * COST_PER_MINUTE;
    }

    @Override
    public Double calculateCo2Saved(Double kwh) {
        return kwh != null ? kwh * CO2_SAVED_PER_KWH : 0.0;
    }

    @Override
    public Double estimateKwhConsumed(long durationMinutes) {
        return durationMinutes * KWH_PER_MINUTE;
    }

    /**
     * Get finished charging sessions for a user within a specific time period.
     */
    private List<ChargingSession> getFinishedSessionsForUserInPeriod(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return chargingSessionRepository.findFinishedSessionsByUserInPeriod(String.valueOf(userId), startDate, endDate);
    }

    /**
     * Get bookings for a user within a specific time period.
     */
    private List<Booking> getBookingsForUserInPeriod(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findBookingsByUserInPeriod(String.valueOf(userId), startDate, endDate);
    }

    /**
     * Calculate duration in minutes between start and end time of a charging session.
     */
    private long calculateDurationMinutes(ChargingSession session) {
        if (session.getStartTime() != null && session.getEndTime() != null) {
            return Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();
        }
        return 0;
    }

    /**
     * Convert ChargingSession to RecentSessionDTO.
     */
    private RecentSessionDTO convertToRecentSessionDTO(ChargingSession session) {
        long durationMinutes = Duration.between(session.getStartTime(), 
                session.getEndTime() != null ? session.getEndTime() : LocalDateTime.now()).toMinutes();
        Double estimatedCost = calculateEstimatedCost(durationMinutes);

        return new RecentSessionDTO(
            session.getId(),
            Long.valueOf(session.getStationId()),
            Long.valueOf(session.getUserId()),
            session.getStartTime(),
            session.getEndTime(),
            session.isFinished(),
            Long.valueOf(durationMinutes),
            estimatedCost
        );
    }

    /**
     * Convert Booking to ReservationDTO.
     */
    private ReservationDTO convertToReservationDTO(Booking booking) {
        // Calculate estimated cost based on booking duration
        long durationMinutes = Duration.between(booking.getStartTime(), booking.getEndTime()).toMinutes();
        Double estimatedCost = calculateEstimatedCost(durationMinutes);

        return new ReservationDTO(
                booking.getId(),
                booking.getStationId(),
                booking.getUserId(),
                booking.getStartTime(),
                booking.getEndTime(),
                estimatedCost,
                booking.getStatus().toString()
        );
    }
} 
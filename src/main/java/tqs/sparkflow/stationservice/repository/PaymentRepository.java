package tqs.sparkflow.stationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tqs.sparkflow.stationservice.model.Payment;
import tqs.sparkflow.stationservice.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by Stripe payment intent ID
     */
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    /**
     * Find all payments for a specific booking
     */
    List<Payment> findByBookingIdOrderByCreatedAtDesc(Long bookingId);

    /**
     * Find payments by status
     */
    List<Payment> findByStatusOrderByCreatedAtDesc(PaymentStatus status);

    /**
     * Find payments within a date range
     */
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);

    /**
     * Find successful payments for a booking
     */
    List<Payment> findByBookingIdAndStatusOrderByCreatedAtDesc(Long bookingId, PaymentStatus status);

    /**
     * Check if a booking has a successful payment
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Payment p " +
           "WHERE p.bookingId = :bookingId AND p.status = :status")
    boolean existsByBookingIdAndStatus(@Param("bookingId") Long bookingId, 
                                      @Param("status") PaymentStatus status);

    /**
     * Get total amount paid for a booking
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.bookingId = :bookingId AND p.status = 'SUCCEEDED'")
    Double getTotalPaidAmountForBooking(@Param("bookingId") Long bookingId);

    /**
     * Find pending payments older than specified time
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :cutoffTime")
    List<Payment> findPendingPaymentsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);
}
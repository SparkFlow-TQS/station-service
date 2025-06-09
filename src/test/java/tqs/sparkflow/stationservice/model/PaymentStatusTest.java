package tqs.sparkflow.stationservice.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentStatusTest {

    @Test
    @DisplayName("Should have all expected payment statuses")
    void shouldHaveAllExpectedPaymentStatuses() {
        // Given
        PaymentStatus[] expectedStatuses = {
            PaymentStatus.PENDING,
            PaymentStatus.PROCESSING,
            PaymentStatus.SUCCEEDED,
            PaymentStatus.FAILED,
            PaymentStatus.CANCELED,
            PaymentStatus.REFUNDED,
            PaymentStatus.REQUIRES_ACTION
        };

        // When
        PaymentStatus[] actualStatuses = PaymentStatus.values();

        // Then
        assertThat(actualStatuses).containsExactlyInAnyOrder(expectedStatuses);
        assertThat(actualStatuses).hasSize(7);
    }

    @Test
    @DisplayName("Should have correct string values")
    void shouldHaveCorrectStringValues() {
        // Then
        assertThat(PaymentStatus.PENDING.toString()).isEqualTo("PENDING");
        assertThat(PaymentStatus.PROCESSING.toString()).isEqualTo("PROCESSING");
        assertThat(PaymentStatus.SUCCEEDED.toString()).isEqualTo("SUCCEEDED");
        assertThat(PaymentStatus.FAILED.toString()).isEqualTo("FAILED");
        assertThat(PaymentStatus.CANCELED.toString()).isEqualTo("CANCELED");
        assertThat(PaymentStatus.REFUNDED.toString()).isEqualTo("REFUNDED");
        assertThat(PaymentStatus.REQUIRES_ACTION.toString()).isEqualTo("REQUIRES_ACTION");
    }

    @Test
    @DisplayName("Should handle valueOf correctly")
    void shouldHandleValueOfCorrectly() {
        // Test each enum value
        assertThat(PaymentStatus.valueOf("PENDING")).isEqualTo(PaymentStatus.PENDING);
        assertThat(PaymentStatus.valueOf("PROCESSING")).isEqualTo(PaymentStatus.PROCESSING);
        assertThat(PaymentStatus.valueOf("SUCCEEDED")).isEqualTo(PaymentStatus.SUCCEEDED);
        assertThat(PaymentStatus.valueOf("FAILED")).isEqualTo(PaymentStatus.FAILED);
        assertThat(PaymentStatus.valueOf("CANCELED")).isEqualTo(PaymentStatus.CANCELED);
        assertThat(PaymentStatus.valueOf("REFUNDED")).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(PaymentStatus.valueOf("REQUIRES_ACTION")).isEqualTo(PaymentStatus.REQUIRES_ACTION);
    }

    @Test
    @DisplayName("Should be used in switch statements")
    void shouldBeUsedInSwitchStatements() {
        // Test that each status can be used in switch statements
        for (PaymentStatus status : PaymentStatus.values()) {
            String description = getStatusDescription(status);
            assertThat(description).isNotNull();
            assertThat(description).isNotEmpty();
        }
    }

    private String getStatusDescription(PaymentStatus status) {
        return switch (status) {
            case PENDING -> "Payment is pending";
            case PROCESSING -> "Payment is being processed";
            case SUCCEEDED -> "Payment has succeeded";
            case FAILED -> "Payment has failed";
            case CANCELED -> "Payment was canceled";
            case REFUNDED -> "Payment was refunded";
            case REQUIRES_ACTION -> "Payment requires additional action";
        };
    }

    @Test
    @DisplayName("Should maintain order consistency")
    void shouldMaintainOrderConsistency() {
        // Given
        PaymentStatus[] statuses = PaymentStatus.values();

        // Then
        assertThat(statuses[0]).isEqualTo(PaymentStatus.PENDING);
        assertThat(statuses[1]).isEqualTo(PaymentStatus.PROCESSING);
        assertThat(statuses[2]).isEqualTo(PaymentStatus.SUCCEEDED);
        assertThat(statuses[3]).isEqualTo(PaymentStatus.FAILED);
        assertThat(statuses[4]).isEqualTo(PaymentStatus.CANCELED);
        assertThat(statuses[5]).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(statuses[6]).isEqualTo(PaymentStatus.REQUIRES_ACTION);
    }

    @Test
    @DisplayName("Should have unique ordinal values")
    void shouldHaveUniqueOrdinalValues() {
        // Given
        PaymentStatus[] statuses = PaymentStatus.values();

        // Then
        for (int i = 0; i < statuses.length; i++) {
            assertThat(statuses[i].ordinal()).isEqualTo(i);
        }
    }

    @Test
    @DisplayName("Should handle comparison operations")
    void shouldHandleComparisonOperations() {
        // Test enum comparison
        assertThat(PaymentStatus.PENDING.compareTo(PaymentStatus.PROCESSING)).isLessThan(0);
        assertThat(PaymentStatus.SUCCEEDED.compareTo(PaymentStatus.PENDING)).isGreaterThan(0);
        assertThat(PaymentStatus.FAILED.compareTo(PaymentStatus.FAILED)).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle equality correctly")
    void shouldHandleEqualityCorrectly() {
        // Test enum equality
        assertThat(PaymentStatus.PENDING).isEqualTo(PaymentStatus.PENDING);
        assertThat(PaymentStatus.PENDING).isNotEqualTo(PaymentStatus.SUCCEEDED);
        
        // Test with valueOf
        assertThat(PaymentStatus.valueOf("PENDING")).isEqualTo(PaymentStatus.PENDING);
        assertThat(PaymentStatus.valueOf("SUCCEEDED")).isNotEqualTo(PaymentStatus.PENDING);
    }
}
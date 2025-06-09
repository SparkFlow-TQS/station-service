package tqs.sparkflow.stationservice.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status of a booking")
public enum BookingStatus {
    @Schema(description = "The booking is active and valid")
    ACTIVE,

    @Schema(description = "The booking has been cancelled")
    CANCELLED,

    @Schema(description = "The booking has been completed")
    COMPLETED
}

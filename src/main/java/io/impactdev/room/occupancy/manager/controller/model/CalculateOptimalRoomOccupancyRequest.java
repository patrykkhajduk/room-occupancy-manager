package io.impactdev.room.occupancy.manager.controller.model;

import io.impactdev.room.occupancy.manager.domain.model.RoomOccupancyInputData;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CalculateOptimalRoomOccupancyRequest(
        @NotNull(message = "Premium rooms count cannot be null")
        @Min(value = 0, message = "Premium rooms count cannot be lower than {value}")
        @Max(value = 1000, message = "Premium rooms count cannot be greater than {value}")
        Integer premiumRoomsCount,
        @NotNull(message = "Economy rooms count cannot be null")
        @Min(value = 0, message = "Economy rooms count cannot be lower than {value}")
        @Max(value = 1000, message = "Economy rooms count cannot be greater than {value}")
        Integer economyRoomsCount,
        @NotEmpty(message = "Guests prices list cannot be null or empty")
        @Size(max = 1000, message = "Guests prices list cannot have more than {max} elements")
        List<@Positive(message = "Guest price must be positive number")
        @Digits(integer = 6, fraction = 2, message = "Guest price cannot be greater than 999999.99 or have more than {fraction} fractions")
                BigDecimal> guestPrices) {

    public RoomOccupancyInputData toRoomOccupancyInputData() {
        return new RoomOccupancyInputData(premiumRoomsCount, economyRoomsCount, guestPrices);
    }
}

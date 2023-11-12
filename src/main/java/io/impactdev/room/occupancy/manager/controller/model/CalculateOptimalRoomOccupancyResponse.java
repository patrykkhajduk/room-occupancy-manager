package io.impactdev.room.occupancy.manager.controller.model;

import io.impactdev.room.occupancy.manager.domain.model.RoomOccupancyOptimisationResult;

import java.math.BigDecimal;

public record CalculateOptimalRoomOccupancyResponse(Integer usedPremiumRoomsCount,
                                                    Integer usedEconomyRoomsCount,
                                                    BigDecimal premiumRoomsProfits,
                                                    BigDecimal economyRoomsProfits) {

    public static CalculateOptimalRoomOccupancyResponse from(RoomOccupancyOptimisationResult result) {
        return new CalculateOptimalRoomOccupancyResponse(
                result.usedPremiumRoomsCount(),
                result.usedEconomyRoomsCount(),
                result.premiumRoomsProfits(),
                result.economyRoomsProfits());
    }
}

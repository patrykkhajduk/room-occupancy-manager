package io.impactdev.room.occupancy.manager.domain.model;

import java.math.BigDecimal;

public record RoomOccupancyOptimisationResult(Integer usedPremiumRoomsCount,
                                              Integer usedEconomyRoomsCount,
                                              BigDecimal premiumRoomsProfits,
                                              BigDecimal economyRoomsProfits) {
}

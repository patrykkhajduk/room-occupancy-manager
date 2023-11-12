package io.impactdev.room.occupancy.manager.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record RoomOccupancyInputData(int premiumRoomsCount,
                                     int economyRoomsCount,
                                     List<BigDecimal> guestPrices) {

}

package io.impactdev.room.occupancy.manager.domain;

import io.impactdev.room.occupancy.manager.domain.model.RoomOccupancyInputData;
import io.impactdev.room.occupancy.manager.domain.model.RoomOccupancyOptimisationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
class ProfitsMaximisingRoomOccupancyOptimizer implements RoomOccupancyOptimizer {

    private static final BigDecimal PREMIUM_GUEST_ROOM_PRICE_LIMIT = new BigDecimal("100.00");

    @Override
    public RoomOccupancyOptimisationResult calculateOptimalRoomOccupancy(RoomOccupancyInputData data) {
        log.info("Started calculating profits maximising room occupancy");
        List<BigDecimal> sortedGuestsPrices = data.guestPrices()
                .stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        Integer remainingEconomyGuestsCount = null;
        int usedPremiumRoomsCount = 0;
        int usedEconomyRoomsCount = 0;
        BigDecimal premiumRoomsProfits = new BigDecimal("0.00");
        BigDecimal economyRoomsProfits = new BigDecimal("0.00");
        for (int guestIndex = 0; guestIndex < sortedGuestsPrices.size(); guestIndex++) {
            BigDecimal guestPrice = sortedGuestsPrices.get(guestIndex);
            if (isGuestQualifyingForPremiumRoom(data, usedPremiumRoomsCount, guestPrice)) {
                log.info("Putting guest with price: {} in premium room", guestPrice);
                usedPremiumRoomsCount++;
                premiumRoomsProfits = premiumRoomsProfits.add(guestPrice);
            } else if (!guestCanAffordPremiumRoomPrice(guestPrice)) {
                if (remainingEconomyGuestsCount == null) {
                    remainingEconomyGuestsCount = sortedGuestsPrices.size() - guestIndex;
                }
                if (isEconomyGuestQualifyingForPremiumUpgrade(data, usedPremiumRoomsCount, remainingEconomyGuestsCount)) {
                    log.info("Putting guest with price: {} in premium room", guestPrice);
                    usedPremiumRoomsCount++;
                    remainingEconomyGuestsCount--;
                    premiumRoomsProfits = premiumRoomsProfits.add(guestPrice);
                } else if (usedEconomyRoomsCount < data.economyRoomsCount()) {
                    log.info("Putting guest with price: {} in economy room", guestPrice);
                    usedEconomyRoomsCount++;
                    remainingEconomyGuestsCount--;
                    economyRoomsProfits = economyRoomsProfits.add(guestPrice);
                }
            }
        }
        log.info("Completed calculating profits maximising room occupancy");
        return new RoomOccupancyOptimisationResult(
                usedPremiumRoomsCount, usedEconomyRoomsCount, premiumRoomsProfits, economyRoomsProfits);
    }

    private boolean isGuestQualifyingForPremiumRoom(RoomOccupancyInputData data,
                                                    int usedPremiumRoomsCount,
                                                    BigDecimal guestPrice) {
        return arePremiumRoomsAvailable(data, usedPremiumRoomsCount) && guestCanAffordPremiumRoomPrice(guestPrice);
    }

    private boolean arePremiumRoomsAvailable(RoomOccupancyInputData data, int usedPremiumRoomsCount) {
        return usedPremiumRoomsCount < data.premiumRoomsCount();
    }

    private boolean guestCanAffordPremiumRoomPrice(BigDecimal guestPrice) {
        return guestPrice.compareTo(PREMIUM_GUEST_ROOM_PRICE_LIMIT) >= 0;
    }

    private boolean isEconomyGuestQualifyingForPremiumUpgrade(RoomOccupancyInputData data,
                                                              int usedPremiumRoomsCount,
                                                              Integer remainingEconomyGuestsCount) {
        return arePremiumRoomsAvailable(data, usedPremiumRoomsCount)
                && areMoreEconomyGuestThanRoomsAvailable(data, remainingEconomyGuestsCount);
    }

    private boolean areMoreEconomyGuestThanRoomsAvailable(RoomOccupancyInputData data, Integer remainingEconomyGuestsCount) {
        return remainingEconomyGuestsCount > data.economyRoomsCount();
    }
}

package io.impactdev.room.occupancy.manager.domain;

import io.impactdev.room.occupancy.manager.domain.model.RoomOccupancyInputData;
import io.impactdev.room.occupancy.manager.domain.model.RoomOccupancyOptimisationResult;

public interface RoomOccupancyOptimizer {

    RoomOccupancyOptimisationResult calculateOptimalRoomOccupancy(RoomOccupancyInputData data);
}

package io.impactdev.room.occupancy.manager.controller;

import io.impactdev.room.occupancy.manager.controller.model.CalculateOptimalRoomOccupancyRequest;
import io.impactdev.room.occupancy.manager.controller.model.CalculateOptimalRoomOccupancyResponse;
import io.impactdev.room.occupancy.manager.domain.RoomOccupancyOptimizer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@Validated
public class RoomController {

    private final RoomOccupancyOptimizer roomOccupancyOptimizer;

    @PostMapping("/calculate-optimal-occupancy")
    public CalculateOptimalRoomOccupancyResponse calculateOptimalRoomOccupancy(
            @RequestBody @Valid CalculateOptimalRoomOccupancyRequest request) {
        return CalculateOptimalRoomOccupancyResponse.from(
                roomOccupancyOptimizer.calculateOptimalRoomOccupancy(request.toRoomOccupancyInputData()));
    }
}

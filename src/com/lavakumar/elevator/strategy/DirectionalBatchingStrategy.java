package com.lavakumar.elevator.strategy;

import com.lavakumar.elevator.model.Direction;
import com.lavakumar.elevator.model.Elevator;
import com.lavakumar.elevator.model.OutsideRequest;

import java.util.List;

public class DirectionalBatchingStrategy implements ElevatorAssignmentStrategy {
    @Override
    public Elevator assign(List<Elevator> elevators, OutsideRequest request) {
        // 1. Prefer elevators already moving in the same direction toward the floor
        for (Elevator e : elevators) {
            if (e.getDirection() == request.getDirection()) {
                int curr = e.getCurrentFloor();
                if ((request.getDirection() == Direction.UP && curr <= request.getFloor()) ||
                        (request.getDirection() == Direction.DOWN && curr >= request.getFloor())) {
                    return e; // batch it!
                }
            }
        }

        // 2. Fall back to nearest idle
        for (Elevator e : elevators) {
            if (e.isIdle()) {
                return e;
            }
        }

        return null;
    }
}


package com.lavakumar.elevator.strategy;

import com.lavakumar.elevator.model.Direction;
import com.lavakumar.elevator.model.Elevator;
import com.lavakumar.elevator.model.OutsideRequest;

import java.util.List;

public class NearestElevatorStrategy implements ElevatorAssignmentStrategy {

    @Override
    public Elevator assign(List<Elevator> elevators, OutsideRequest request) {
        Elevator best = null;
        int minDistance = Integer.MAX_VALUE;
        int target = request.getFloor();
        Direction dir = request.getDirection();

        for (Elevator e : elevators) {
            int curr = e.getCurrentFloor();
            if (e.isIdle() || (e.getDirection() == dir &&
                    ((dir == Direction.UP && curr <= target) ||
                            (dir == Direction.DOWN && curr >= target)))) {
                int dist = Math.abs(curr - target);
                if (dist < minDistance) {
                    minDistance = dist;
                    best = e;
                }
            }
        }
        return best;
    }
}

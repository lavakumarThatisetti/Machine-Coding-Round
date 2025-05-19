package com.lavakumar.elevator.service;

import com.lavakumar.elevator.model.Direction;
import com.lavakumar.elevator.model.Elevator;

import java.util.List;

public class RequestScheduler {

    public Elevator assignElevator(List<Elevator> elevators, int requestedFloor, Direction requestedDirection) {
        Elevator bestElevator = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            Direction dir = elevator.getDirection();
            int currentFloor = elevator.getCurrentFloor();

            if (elevator.isIdle()) {
                int distance = Math.abs(currentFloor - requestedFloor);
                if (distance < minDistance) {
                    minDistance = distance;
                    bestElevator = elevator;
                }
            } else if (dir == requestedDirection) {
                if ((dir == Direction.UP && currentFloor <= requestedFloor) ||
                        (dir == Direction.DOWN && currentFloor >= requestedFloor)) {
                    int distance = Math.abs(currentFloor - requestedFloor);
                    if (distance < minDistance) {
                        minDistance = distance;
                        bestElevator = elevator;
                    }
                }
            }
        }

        // fallback: choose any idle elevator if nothing else matches
        if (bestElevator == null) {
            for (Elevator elevator : elevators) {
                if (elevator.isIdle()) {
                    return elevator;
                }
            }
        }

        return bestElevator;
    }
}


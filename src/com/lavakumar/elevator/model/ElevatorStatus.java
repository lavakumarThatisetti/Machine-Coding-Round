package com.lavakumar.elevator.model;

import java.util.Set;

public class ElevatorStatus {
    private final int id;
    private final int currentFloor;
    private final Direction direction;
    private final ElevatorDoorState doorState;
    private final Set<Integer> pendingRequests;

    public ElevatorStatus(int id, int currentFloor, Direction direction,
                          ElevatorDoorState doorState, Set<Integer> pendingRequests) {
        this.id = id;
        this.currentFloor = currentFloor;
        this.direction = direction;
        this.doorState = doorState;
        this.pendingRequests = pendingRequests;
    }

    public int getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public ElevatorDoorState getDoorState() {
        return doorState;
    }

    public Set<Integer> getPendingRequests() {
        return pendingRequests;
    }

    @Override
    public String toString() {
        return "ElevatorStatus{" +
                "id=" + id +
                ", currentFloor=" + currentFloor +
                ", direction=" + direction +
                ", doorState=" + doorState +
                ", pendingRequests=" + pendingRequests +
                '}';
    }
}

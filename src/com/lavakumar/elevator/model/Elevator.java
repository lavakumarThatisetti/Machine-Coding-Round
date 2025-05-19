package com.lavakumar.elevator.model;

import java.util.Set;
import java.util.TreeSet;

public class Elevator {
    private final int id;
    private int currentFloor;
    private Direction direction;
    private ElevatorDoorState doorState;
    private TreeSet<Integer> internalRequests; // Sorted to process in order
    private final int minFloor;
    private final int maxFloor;

    public Elevator(int id, int minFloor, int maxFloor) {
        this.id = id;
        this.currentFloor = 0;
        this.direction = Direction.IDLE;
        this.doorState = ElevatorDoorState.CLOSED;
        this.internalRequests = new TreeSet<>();
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
    }

    public void addInternalRequest(int floor) {
        addInternalRequest(floor, true);
    }

    public void addInternalRequest(int floor, boolean isInternal) {
        if (floor >= minFloor && floor <= maxFloor) {
            internalRequests.add(floor);

            if (direction == Direction.IDLE) {
                if (floor > currentFloor) {
                    direction = Direction.UP;
                } else if (floor < currentFloor) {
                    direction = Direction.DOWN;
                }
            }

            if (isInternal) {
                System.out.println("📥 Elevator " + id + " received INSIDE request to floor " + floor);
            } else {
                System.out.println("📤 Elevator " + id + " received EXTERNAL assignment to floor " + floor);
            }
        } else {
            System.out.println("⚠️ Invalid floor " + floor + " ignored by Elevator " + id);
        }
    }

    public void move() {
        if (internalRequests.isEmpty()) {
            direction = Direction.IDLE;
            return;
        }

        int targetFloor = direction == Direction.DOWN ? internalRequests.first() : internalRequests.last();

        if (currentFloor < targetFloor) {
            direction = Direction.UP;
            currentFloor++;
        } else if (currentFloor > targetFloor) {
            direction = Direction.DOWN;
            currentFloor--;
        }

        // Arrived at a destination
        if (internalRequests.contains(currentFloor)) {
            openDoor();
            internalRequests.remove(currentFloor);
        }
    }

    public void openDoor() {
        doorState = ElevatorDoorState.OPEN;
        System.out.println("Elevator " + id + " opened door at floor " + currentFloor);
    }

    public void closeDoor() {
        doorState = ElevatorDoorState.CLOSED;
    }

    public boolean isIdle() {
        return direction == Direction.IDLE && internalRequests.isEmpty();
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getId() {
        return id;
    }

    public Set<Integer> getPendingRequests() {
        return new TreeSet<>(internalRequests);
    }

    public ElevatorStatus getStatus() {
        return new ElevatorStatus(id, currentFloor, direction, doorState, getPendingRequests());
    }

    public void resetToIdle() {
        this.internalRequests.clear();
        this.doorState = ElevatorDoorState.CLOSED;
        this.direction = Direction.IDLE;
    }
}

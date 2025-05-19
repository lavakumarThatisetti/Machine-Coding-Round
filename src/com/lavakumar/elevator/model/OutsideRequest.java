package com.lavakumar.elevator.model;

public class OutsideRequest extends ElevatorRequest {
    private final Direction direction;

    public OutsideRequest(int floor, Direction direction) {
        super(floor);
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "OutsideRequest at floor " + floor + " to go " + direction;
    }
}

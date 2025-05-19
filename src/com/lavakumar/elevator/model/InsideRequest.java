package com.lavakumar.elevator.model;

public class InsideRequest extends ElevatorRequest {
    public InsideRequest(int floor) {
        super(floor);
    }

    @Override
    public String toString() {
        return "InsideRequest to floor " + floor;
    }
}


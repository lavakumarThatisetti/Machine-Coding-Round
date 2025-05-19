package com.lavakumar.elevator.strategy;

import com.lavakumar.elevator.model.Elevator;
import com.lavakumar.elevator.model.OutsideRequest;

import java.util.List;

public interface ElevatorAssignmentStrategy {
    Elevator assign(List<Elevator> elevators, OutsideRequest request);
}

package com.lavakumar.elevator;

import com.lavakumar.elevator.model.Elevator;
import com.lavakumar.elevator.model.ElevatorRequest;
import com.lavakumar.elevator.model.OutsideRequest;
import com.lavakumar.elevator.service.RequestScheduler;
import com.lavakumar.elevator.strategy.ElevatorAssignmentStrategy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ElevatorSystemController {
    private final List<Elevator> elevators;
    private final RequestScheduler scheduler;

    private final ElevatorAssignmentStrategy assignmentStrategy;
    private final int minFloor;
    private final int maxFloor;

    // Global external request queue (optional if you want to persist unassigned)
    private final Queue<OutsideRequest> pendingExternalRequests = new LinkedList<>();


    public ElevatorSystemController(int numberOfElevators, int minFloor, int maxFloor, ElevatorAssignmentStrategy assignmentStrategy) {
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.assignmentStrategy = assignmentStrategy;
        this.scheduler = new RequestScheduler();
        this.elevators = new ArrayList<>();
        for (int i = 0; i < numberOfElevators; i++) {
            elevators.add(new Elevator(i, minFloor, maxFloor));
        }
    }

    // Simulates a user pressing a button on a floor

    // Simulates one time step: all elevators move
    public void handleExternalRequest(OutsideRequest request) {
        Elevator assignedElevator = assignmentStrategy.assign(elevators, request);
        // Elevator assignedElevator = scheduler.assignElevator(elevators, request.getFloor(), request.getDirection());
        if (assignedElevator != null) {
            assignedElevator.addInternalRequest(request.getFloor(), false);
            System.out.println("✅ Assigned Elevator " + assignedElevator.getId() + " to request: " + request);
        } else {
            System.out.println("⏳ All elevators busy. Queuing request: " + request);
            pendingExternalRequests.offer(request);
        }
    }

    public void stepSimulation() {
        // Retry queued external requests
        if (!pendingExternalRequests.isEmpty()) {
            Queue<OutsideRequest> retryQueue = new LinkedList<>();
            while (!pendingExternalRequests.isEmpty()) {
                OutsideRequest req = pendingExternalRequests.poll();
                Elevator assigned = scheduler.assignElevator(elevators, req.getFloor(), req.getDirection());
                if (assigned != null) {
                    assigned.addInternalRequest(req.getFloor());
                    System.out.println("🔁 Retried: Assigned Elevator " + assigned.getId() + " to request: " + req);
                } else {
                    retryQueue.offer(req); // still not ready
                }
            }
            pendingExternalRequests.addAll(retryQueue);
        }

        // Move all elevators
        for (Elevator elevator : elevators) {
            elevator.move();
        }
    }

    public void printSystemStatus() {
        System.out.println("🛗 Elevator Status:");
        for (Elevator elevator : elevators) {
            System.out.println("  Elevator " + elevator.getId() + ": " + elevator.getStatus());
        }
    }

    public List<Elevator> getElevators() {
        return elevators;
    }
}


package com.lavakumar.elevator;

import com.lavakumar.elevator.model.Direction;
import com.lavakumar.elevator.model.Elevator;
import com.lavakumar.elevator.model.OutsideRequest;
import com.lavakumar.elevator.strategy.DirectionalBatchingStrategy;
import com.lavakumar.elevator.strategy.ElevatorAssignmentStrategy;
import com.lavakumar.elevator.strategy.NearestElevatorStrategy;

public class Driver2 {
    public static void main(String[] args) throws InterruptedException {
        // Switch between NearestElevatorStrategy and DirectionalBatchingStrategy here
        ElevatorAssignmentStrategy strategy = new DirectionalBatchingStrategy();
        // ElevatorAssignmentStrategy strategy = new NearestElevatorStrategy();

        ElevatorSystemController controller = new ElevatorSystemController(3, 0, 10, strategy);
        System.out.println("📊 Using strategy: " + strategy.getClass().getSimpleName());

        // STEP 1: Manually set elevator initial positions (0, 3, 6)
        // We simulate this by adding a dummy request and moving each elevator once
        controller.getElevators().get(0).addInternalRequest(0);
        controller.getElevators().get(0).move(); // Floor 0

        controller.getElevators().get(1).addInternalRequest(3);
        while (controller.getElevators().get(1).getCurrentFloor() < 3) {
            controller.getElevators().get(1).move(); // Floor 3
        }

        controller.getElevators().get(2).addInternalRequest(6);
        while (controller.getElevators().get(2).getCurrentFloor() < 6) {
            controller.getElevators().get(2).move(); // Floor 6
        }

        // Clear their internal state so they are idle again
        controller.getElevators().forEach(Elevator::resetToIdle);

        // STEP 2: Fire external requests
        controller.handleExternalRequest(new OutsideRequest(2, Direction.UP));   // Expect: Elevator 0
        controller.handleExternalRequest(new OutsideRequest(4, Direction.UP));   // Expect: Elevator 1 OR Elevator 0 (if batching)
        controller.handleExternalRequest(new OutsideRequest(5, Direction.UP));   // Expect: Elevator 2 OR Elevator 0 (if batching)
        controller.handleExternalRequest(new OutsideRequest(7, Direction.DOWN)); // Expect: Elevator 2

        // STEP 3: Run simulation
        for (int tick = 1; tick <= 10; tick++) {
            System.out.println("\n==============================");
            System.out.println("       ⏱️ Tick " + tick);
            System.out.println("==============================");
            controller.stepSimulation();
            controller.printSystemStatus();
            Thread.sleep(1000);
        }
    }
}

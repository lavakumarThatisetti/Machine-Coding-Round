package com.lavakumar.elevator;

import com.lavakumar.elevator.model.Direction;
import com.lavakumar.elevator.model.OutsideRequest;
import com.lavakumar.elevator.strategy.DirectionalBatchingStrategy;
import com.lavakumar.elevator.strategy.NearestElevatorStrategy;

public class Driver {
    public static void main(String[] args) throws InterruptedException {
        ElevatorSystemController controller = new ElevatorSystemController(2, 0, 10, new NearestElevatorStrategy()); // fewer elevators = easier to simulate busy state

        // Step 1: Initial load - make all elevators busy
        controller.handleExternalRequest(new OutsideRequest(1, Direction.UP));  // elevator needs to go all the way up
        controller.handleExternalRequest(new OutsideRequest(2, Direction.UP));
        controller.handleExternalRequest(new OutsideRequest(3, Direction.UP));

        // Step 2: Add internal destinations to keep them busy
        controller.getElevators().get(0).addInternalRequest(0);
        controller.getElevators().get(1).addInternalRequest(1);

        // Step 3: Submit a new request that will be forced into waiting queue
        controller.handleExternalRequest(new OutsideRequest(2, Direction.UP)); // all elevators already busy

        // Simulation loop to demonstrate queueing and retry
        for (int tick = 1; tick <= 20; tick++) {
            System.out.println("\n===== Tick " + tick + " =====");
            controller.stepSimulation();
            controller.printSystemStatus();

            // Step 4 (Optional): Inject a delayed request mid-simulation
            if (tick == 5) {
                System.out.println("Injecting new request at tick 5 (floor 3)");
                controller.handleExternalRequest(new OutsideRequest(3, Direction.UP));
            }

            Thread.sleep(1000); // pause for clarity
        }
    }
}

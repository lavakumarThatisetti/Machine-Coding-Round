package com.lavakumar.elevator;

import com.lavakumar.elevator.model.Direction;
import com.lavakumar.elevator.model.Door;
import com.lavakumar.elevator.model.ElevatorCar;
import com.lavakumar.elevator.model.Floor;
import com.lavakumar.elevator.model.buttonpanel.InsideElevatorButtonPanel;
import com.lavakumar.elevator.repository.ElevatorSystemRepository;
import com.lavakumar.elevator.service.Dispatcher;

public class Driver {
    public static void main(String[] args) {
        ElevatorSystemRepository elevatorSystemRepository = new ElevatorSystemRepository();
        Dispatcher dispatcher = new Dispatcher(elevatorSystemRepository);

        ElevatorCar elevator1 = new ElevatorCar(
                1, new Floor(0),new Door(false), new InsideElevatorButtonPanel(dispatcher)
        );
        ElevatorCar elevator2 = new ElevatorCar(
                2, new Floor(0),new Door(false), new InsideElevatorButtonPanel(dispatcher)
        );
        ElevatorCar elevator3 = new ElevatorCar(
                3, new Floor(1),new Door(false), new InsideElevatorButtonPanel(dispatcher)
        );
        ElevatorCar elevator4 = new ElevatorCar(
                4, new Floor(2),new Door(false), new InsideElevatorButtonPanel(dispatcher)
        );

        elevatorSystemRepository.addElevator(elevator1);
        elevatorSystemRepository.addElevator(elevator2);
        elevatorSystemRepository.addElevator(elevator3);
        elevatorSystemRepository.addElevator(elevator4);

        // Outside Request
        Request upRequest1 = new OutsideRequest(4 , Direction.UP);
        Request upRequest2 = new OutsideRequest( 3 , Direction.UP);
        Request downRequest1 = new OutsideRequest( 0 , Direction.DOWN);
        Request downRequest2 = new OutsideRequest( 0 , Direction.DOWN);


        Floor floor0 = new Floor(0,dispatcher);
        Floor floor1 = new Floor(1,dispatcher);
        Floor floor3 = new Floor(3,dispatcher);
        Floor floor4 = new Floor(4,dispatcher);

        floor0.callElevator(upRequest1);
        floor1.callElevator(upRequest2);
        floor3.callElevator(downRequest1);
        floor4.callElevator(downRequest2);

        // Inside Request
        InsideRequest upRequest3 = new InsideRequest( 4 , Direction.UP);
        InsideRequest upRequest4 = new InsideRequest( 4 , Direction.UP);
        InsideRequest downRequest3 = new InsideRequest( 0, Direction.DOWN);
        InsideRequest downRequest4 = new InsideRequest( 0, Direction.DOWN);

        dispatcher.processElevatorRequest(upRequest3);
        dispatcher.processElevatorRequest(upRequest4);
        dispatcher.processElevatorRequest(downRequest3);
        dispatcher.processElevatorRequest(downRequest4);
    }
}

package com.lavakumar.elevator.model;

import com.lavakumar.elevator.Request;
import com.lavakumar.elevator.model.buttonpanel.ButtonPanel;

public class ElevatorCar {
    private final int elevatorId;
    Floor floor;
    Direction direction;
    ButtonPanel buttonPanel;
    Door door;
    int noOfPersons;

    public ElevatorCar(int elevatorId, Floor floor, Door door, ButtonPanel buttonPanel) {
        this.elevatorId = elevatorId;
        this.floor = floor;
        this.direction = Direction.IDLE;
        this.buttonPanel = buttonPanel;
        this.door = door;
        this.noOfPersons = 0;
    }

    public int getElevatorId() {
        return elevatorId;
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public ButtonPanel getButtonPanel() {
        return buttonPanel;
    }

    public void setButtonPanel(ButtonPanel buttonPanel) {
        this.buttonPanel = buttonPanel;
    }

    public Door getDoor() {
        return door;
    }

    public void setDoor(Door door) {
        this.door = door;
    }

    public int getNoOfPersons() {
        return noOfPersons;
    }

    public void setNoOfPersons(int noOfPersons) {
        this.noOfPersons = noOfPersons;
    }


    public void call(Request request){
//        if(request.getCurrentFloor() == floor.getFloorId()){
//            door.open();
//        }else{
//            moveToFloor(request.getDesiredFloor());
//        }
    }
    private void moveToFloor(int destinationFloor) {

    }
}

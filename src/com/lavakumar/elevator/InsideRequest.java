package com.lavakumar.elevator;

import com.lavakumar.elevator.model.Direction;

public class InsideRequest extends Request{

    Direction direction;
    public InsideRequest(int destinationFloor, Direction direction){
        super(destinationFloor);
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}

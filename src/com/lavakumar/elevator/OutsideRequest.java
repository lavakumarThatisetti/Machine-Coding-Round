package com.lavakumar.elevator;

import com.lavakumar.elevator.model.Direction;

public class OutsideRequest extends Request {

    Direction direction;

    public OutsideRequest(int floor, Direction direction){
        super(floor);
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

}

package com.lavakumar.elevator;

import com.lavakumar.elevator.model.Direction;
import com.lavakumar.elevator.model.RequestType;
import com.lavakumar.elevator.model.buttonpanel.ButtonPanel;

public class Request implements Comparable<Request> {

    RequestType type;
    int floor;

    public Request(int floor){
        this.floor = floor;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    @Override
    public int compareTo(Request o) {
        return this.floor - o.floor;
    }
}

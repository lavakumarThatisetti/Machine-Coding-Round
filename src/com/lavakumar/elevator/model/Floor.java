package com.lavakumar.elevator.model;

import com.lavakumar.elevator.Request;
import com.lavakumar.elevator.model.buttonpanel.ButtonPanel;
import com.lavakumar.elevator.model.buttonpanel.OutsideElevatorButtonPanel;
import com.lavakumar.elevator.service.Dispatcher;

public class Floor {
    int floorId;
    ButtonPanel buttonPanel;

    public Floor(int floorId){
        this.floorId =  floorId;
    }
    public Floor(int floorId, Dispatcher dispatcher){
        this.floorId =  floorId;
        this.buttonPanel = new OutsideElevatorButtonPanel(dispatcher);
    }

    public int getFloorId() {
        return floorId;
    }

    public void callElevator(Request request){
        buttonPanel.sendInstructionToDispatcher(request);
    }
}

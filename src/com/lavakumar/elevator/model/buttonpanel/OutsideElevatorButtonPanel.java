package com.lavakumar.elevator.model.buttonpanel;

import com.lavakumar.elevator.OutsideRequest;
import com.lavakumar.elevator.Request;
import com.lavakumar.elevator.model.Direction;
import com.lavakumar.elevator.service.Dispatcher;


public class OutsideElevatorButtonPanel implements ButtonPanel {
    Dispatcher dispatcher;

    public OutsideElevatorButtonPanel(Dispatcher dispatcher){
        this.dispatcher = dispatcher;
    }
    @Override
    public boolean sendInstructionToDispatcher(Request request) {
        dispatcher.processFloorRequest((OutsideRequest) request);
        return true;
    }
}

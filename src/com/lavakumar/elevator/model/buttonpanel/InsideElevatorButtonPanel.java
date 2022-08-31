package com.lavakumar.elevator.model.buttonpanel;

import com.lavakumar.elevator.InsideRequest;
import com.lavakumar.elevator.Request;
import com.lavakumar.elevator.model.Direction;
import com.lavakumar.elevator.model.buttonpanel.ButtonPanel;
import com.lavakumar.elevator.service.Dispatcher;

public class InsideElevatorButtonPanel implements ButtonPanel {
    Dispatcher dispatcher;
    int floor;


    public InsideElevatorButtonPanel(Dispatcher dispatcher){
        this.dispatcher = dispatcher;
    }
    @Override
    public boolean sendInstructionToDispatcher(Request request) {
        dispatcher.processElevatorRequest((InsideRequest) request);
        return true;
    }
}

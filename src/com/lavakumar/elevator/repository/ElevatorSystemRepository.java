package com.lavakumar.elevator.repository;

import com.lavakumar.elevator.model.Direction;
import com.lavakumar.elevator.model.ElevatorCar;
import com.lavakumar.elevator.model.Floor;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ElevatorSystemRepository {
    HashMap<Integer, ElevatorCar> elevatorCarMap;
    HashMap<Integer, Floor> elevatorFloorMap;

    public ElevatorCar addElevator(ElevatorCar elevatorCar){
        if(elevatorCarMap.get(elevatorCar.getElevatorId())!=null)
            elevatorCarMap.put(elevatorCar.getElevatorId(), elevatorCar);
        return elevatorCar;
    }

    public List<ElevatorCar> getAllIdleElevatorsAtFloor(int floorId){
        return elevatorCarMap.values().stream().filter(
                elevatorCar -> elevatorCar.getFloor().getFloorId()==floorId && elevatorCar.getDirection() == Direction.IDLE
                ).collect(Collectors.toList());
    }



}

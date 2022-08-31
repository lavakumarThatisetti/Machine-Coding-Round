package com.lavakumar.elevator.service;

import com.lavakumar.elevator.InsideRequest;
import com.lavakumar.elevator.OutsideRequest;
import com.lavakumar.elevator.Request;
import com.lavakumar.elevator.model.Direction;
import com.lavakumar.elevator.model.ElevatorCar;
import com.lavakumar.elevator.repository.ElevatorSystemRepository;

import java.util.List;
import java.util.PriorityQueue;

// Should get appropriate Elevator
public class Dispatcher {
    // Need to get all Elevators based on that direction
    PriorityQueue<Request> upQueue;
    PriorityQueue<Request> downQueue;
    ElevatorSystemRepository elevatorSystemRepository;

    public Dispatcher(ElevatorSystemRepository elevatorSystemRepository){
        this.elevatorSystemRepository = elevatorSystemRepository;
        // use default, which is a min heap
        upQueue = new PriorityQueue<>();
        // use a max heap
        downQueue =  new PriorityQueue<>((a, b) -> b.getFloor() - a.getFloor());
    }


    public void processElevatorRequest(InsideRequest request){
        if(request.getDirection() == Direction.UP){
            sendUpRequest(request);
        }else{
           // sendDownRequest(request);
        }
    }

    public void processFloorRequest(OutsideRequest request){
        if(request.getDirection() == Direction.UP){
            sendUpRequest(request);
        }else{
            //sendDownRequest(request);
        }
    }

    public void sendUpRequest(Request upRequest) {
//
//        List<ElevatorCar> allIdleElevatorsAtFloor = elevatorSystemRepository.getAllIdleElevatorsAtFloor(upRequest.getCurrentFloor());
//        if(allIdleElevatorsAtFloor.size()>0){
//            ElevatorCar elevatorCar = allIdleElevatorsAtFloor.get(0);
//            elevatorCar.call(upRequest);
//        } else {
//            upQueue.offer(upRequest);
//        }
//
//
//
//
//        if (upRequest.getLocation() == Location.OUTSIDE_ELEVATOR) {
//            // Go pick up the requester who is outside the elevator
//            upQueue.offer(new Request(upRequest.getCurrentFloor(),
//                    upRequest.getCurrentFloor(),
//                    Direction.UP,
//                    Location.OUTSIDE_ELEVATOR));
//
//            System.out.println("Append up request going to floor " + upRequest.getCurrentFloor() + ".");
//        }
//
//        // Go to the desired floor
//        upQueue.offer(upRequest);
//
//        System.out.println("Append up request going to floor " + upRequest.getDesiredFloor() + ".");
    }

//    public void sendDownRequest(Request downRequest) {
//        // Similar to the sendUpRequest logic
//        if (downRequest.getLocation() == Location.OUTSIDE_ELEVATOR) {
//            downQueue.offer(new Request(downRequest.getCurrentFloor(),
//                    downRequest.getCurrentFloor(),
//                    Direction.DOWN,
//                    Location.OUTSIDE_ELEVATOR));
//
//            System.out.println("Append down request going to floor " + downRequest.getCurrentFloor() + ".");
//        }
//
//        // Go to the desired floor
//        downQueue.offer(downRequest);
//
//        System.out.println("Append down request going to floor " + downRequest.getDesiredFloor() + ".");
//    }
//
//    public void run() {
//        while (!upQueue.isEmpty() || !downQueue.isEmpty()) {
//            processRequests();
//        }
//
//        System.out.println("Finished all requests.");
//        this.direction = Direction.IDLE;
//    }
//
//    private void processRequests() {
//        if (this.direction == Direction.UP || this.direction == Direction.IDLE) {
//            processUpRequest();
//            processDownRequest();
//        } else {
//            processDownRequest();
//            processUpRequest();
//        }
//    }
//
//    private void processUpRequest() {
//        while (!upQueue.isEmpty()) {
//            Request upRequest = upQueue.poll();
//            // Communicate with hardware
//            this.currentFloor = upRequest.getDesiredFloor();
//            System.out.println("Processing up requests. Elevator stopped at floor " + this.currentFloor + ".");
//        }
//        if (!downQueue.isEmpty()) {
//            this.direction = Direction.DOWN;
//        } else {
//            this.direction = Direction.IDLE;
//        }
//    }
//
//    private void processDownRequest() {
//        while (!downQueue.isEmpty()) {
//            Request downRequest = downQueue.poll();
//            // Communicate with hardware
//            this.currentFloor = downRequest.getDesiredFloor();
//            System.out.println("Processing down requests. Elevator stopped at floor " + this.currentFloor + ".");
//        }
//        if (!upQueue.isEmpty()) {
//            this.direction = Direction.UP;
//        } else {
//            this.direction = Direction.IDLE;
//        }
//    }



    /*
    private TreeSet<Integer> up = new TreeSet<>(); // floors above currentFloor
private TreeSet<Integer> down = new TreeSet<>(); // floors below currentFloor
private int currentFloor = 0;
private Enum direction = direction.UP;

public void addFloor(int f) {
    if(f < currentFloor) {
        down.add(f);
    } else if(f > currentFloor) {
        up.add(f);
    }
    // else f == currentFloor, so don't add the floor to either queue
}

public int nextFloor() {
    if(direction == direction.DOWN) {
        return down.pollLast(); // highest floor in down, or null if empty
    } else {
        return up.pollFirst(); // lowest floor in up, or null if empty
    }
}
     */
}

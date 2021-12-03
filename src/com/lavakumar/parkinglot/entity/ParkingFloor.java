package com.lavakumar.parkinglot.entity;

import java.util.List;

public class ParkingFloor {
    List<ParkingSlot> parkingSlots = null;

    public ParkingFloor(List<ParkingSlot> parkingSlots){
        this.parkingSlots = parkingSlots;

    }
    public List<ParkingSlot> getParkingSlots() {
        return parkingSlots;
    }

    public void setParkingSlots(List<ParkingSlot> parkingSlots) {
        this.parkingSlots = parkingSlots;
    }
}

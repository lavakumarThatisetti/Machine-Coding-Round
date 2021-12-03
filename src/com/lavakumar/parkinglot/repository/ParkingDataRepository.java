package com.lavakumar.parkinglot.repository;

import com.lavakumar.parkinglot.entity.ParkingFloor;
import com.lavakumar.parkinglot.entity.ParkingSlot;
import com.lavakumar.parkinglot.entity.VehicleType;

import java.util.ArrayList;
import java.util.List;

public class ParkingDataRepository {

    List<ParkingSlot> parkingSlotData;
    List<ParkingFloor> parkingFloors;

    public List<ParkingFloor> initializeData(int noOfFloors,int noOfSlots) {
        parkingFloors = new  ArrayList<>(noOfFloors);
        for(int i=0;i<noOfFloors;i++){
            initializeSlots(noOfSlots);
            List<ParkingSlot> parkingSlots = getAllParkingSlotData(i);
            parkingFloors.add(new ParkingFloor(parkingSlots));
        }
        return parkingFloors;
    }

    public void initializeSlots(int noOfSlots) {
        parkingSlotData = new  ArrayList<>(noOfSlots);
        if(noOfSlots>=1)
            parkingSlotData.add(getTruckData());
        if(noOfSlots>=3)
            for(int i=1;i<3;i++)
                parkingSlotData.add(getBikeData(i));
        if(noOfSlots>3)
            for(int i=3;i<noOfSlots;i++){
                parkingSlotData.add(getCarData(i));
            }
    }

    public List<ParkingSlot> getAllParkingSlotData(int floorId){
        parkingSlotData.forEach(parkingSlot -> parkingSlot.setFloorId(floorId));
        return parkingSlotData;
    }

    private ParkingSlot getTruckData(){
        return new ParkingSlot(VehicleType.TRUCK,true, 0);
    }
    private ParkingSlot getBikeData(int slotId){
        return new ParkingSlot(VehicleType.BIKE,true,slotId);
    }
    private ParkingSlot getCarData(int slotId){
        return new ParkingSlot(VehicleType.CAR,true,slotId);
    }
}

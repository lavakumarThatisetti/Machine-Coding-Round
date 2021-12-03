package com.lavakumar.parkinglot.entity;

public class Vehicle {
    VehicleType vehicleType;
    ParkingSlot parkingSlot;
    String ticketId;
    String color;
    String vehicleRegisterNo;

    public Vehicle(VehicleType vehicleType, ParkingSlot parkingSlot, String color, String vehicleRegisterNo) {
        this.vehicleType = vehicleType;
        this.parkingSlot = parkingSlot;
        this.color = color;
        this.vehicleRegisterNo = vehicleRegisterNo;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public ParkingSlot getParkingSlot() {
        return parkingSlot;
    }

    public void setParkingSlot(ParkingSlot parkingSlot) {
        this.parkingSlot = parkingSlot;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getVehicleRegisterNo() {
        return vehicleRegisterNo;
    }

    public void setVehicleRegisterNo(String vehicleRegisterNo) {
        this.vehicleRegisterNo = vehicleRegisterNo;
    }
}

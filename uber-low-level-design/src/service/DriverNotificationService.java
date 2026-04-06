package service;

import entity.Driver;
import entity.RideBooking;

import java.util.List;

public interface DriverNotificationService {
    void notifyDrivers(RideBooking booking, List<Driver> candidateDrivers);
}

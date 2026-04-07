package service;

import model.Driver;
import model.RideBooking;

import java.util.List;

public interface DriverNotificationService {
    void notifyDrivers(RideBooking booking, List<Driver> candidateDrivers);
}

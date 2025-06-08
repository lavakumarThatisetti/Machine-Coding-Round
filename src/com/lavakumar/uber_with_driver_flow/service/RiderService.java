package com.lavakumar.uber_with_driver_flow.service;

import com.lavakumar.uber_with_driver_flow.models.Rider;

import java.util.HashMap;
import java.util.Map;

public class RiderService {
    private Map<String, Rider> riders = new HashMap<>();

    public Rider registerRider(String id, String name) {
        Rider rider = new Rider(id, name);
        riders.put(id, rider);
        return rider;
    }

    public Rider getRider(String id) {
        return riders.get(id);
    }
}

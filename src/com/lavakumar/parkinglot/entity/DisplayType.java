package com.lavakumar.parkinglot.entity;

import java.util.HashMap;

public enum DisplayType {
    FREE_COUNT("free_count"),
    FREE_SLOTS("free_slots"),
    OCCUPIED_SLOTS("occupied_slots");

    private final String display;

    DisplayType(String s) {
        display = s;
    }

    public String toString() {
        return this.display;
    }

    private static final HashMap<String, DisplayType> map = new HashMap<>(values().length, 1);

    static {
        for (DisplayType c : values()) map.put(c.display, c);
    }

    public static DisplayType of(String name){
        return map.get(name);
    }
}

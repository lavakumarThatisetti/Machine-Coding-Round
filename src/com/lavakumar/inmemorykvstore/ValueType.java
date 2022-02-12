package com.lavakumar.inmemorykvstore;

import java.util.HashMap;

public enum ValueType {
    MAP("Map"),
    OBJECT("Object");

    private final String valueType;

    ValueType(String s) {
        valueType = s;
    }

    public String toString() {
        return this.valueType;
    }

    private static final HashMap<String, ValueType> map = new HashMap<>(values().length, 1);

    static {
        for (ValueType c : values()) map.put(c.valueType, c);
    }

    public static ValueType of(String name){
        return map.get(name);
    }
}

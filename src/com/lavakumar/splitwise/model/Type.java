package com.lavakumar.splitwise.model;

import java.util.HashMap;

public enum Type {
    EXPENSE ("EXPENSE"),
    SHOW ("SHOW"),
    QUIT ("QUIT");

    private final String name;

    Type(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }

    private static final HashMap<String, Type> map = new HashMap<>(values().length, 1);

    static {
        for (Type c : values()) map.put(c.name, c);
    }

    public static Type of(String name){
        return map.get(name);
    }

}

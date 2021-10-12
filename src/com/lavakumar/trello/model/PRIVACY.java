package com.lavakumar.trello.model;

import java.util.HashMap;

public enum PRIVACY {
    PUBLIC("PUBLIC"),
    PRIVATE ("PRIVATE");

    private final String privacy;

    PRIVACY(String s) {
        privacy = s;
    }

    public String toString() {
        return this.privacy;
    }

    private static final HashMap<String, PRIVACY> map = new HashMap<>(values().length, 1);

    static {
        for (PRIVACY c : values()) map.put(c.privacy, c);
    }

    public static PRIVACY of(String name){
        return map.get(name);
    }

}

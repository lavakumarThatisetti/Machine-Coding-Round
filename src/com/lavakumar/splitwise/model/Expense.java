package com.lavakumar.splitwise.model;

import java.util.HashMap;

public enum Expense {
    EQUAL("EQUAL"),
    EXACT ("EXACT"),
    PERCENT("PERCENT");

    private final String expesne;

    Expense(String s) {
        expesne = s;
    }

    public String toString() {
        return this.expesne;
    }

    private static final HashMap<String, Expense> map = new HashMap<>(values().length, 1);

    static {
        for (Expense c : values()) map.put(c.expesne, c);
    }

    public static Expense of(String name){
        return map.get(name);
    }

}

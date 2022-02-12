package com.lavakumar.bowlingalley.strategy;

public class SparseStrategy implements Strategy{
    public static final Integer SPARE_BONUS = 5;
    @Override
    public int bonus() {
        return SPARE_BONUS;
    }
}

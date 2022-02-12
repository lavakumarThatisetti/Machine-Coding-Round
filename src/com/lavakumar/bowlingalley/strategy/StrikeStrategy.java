package com.lavakumar.bowlingalley.strategy;

public class StrikeStrategy implements Strategy{
    public static final Integer STRIKE_BONUS = 10;
    @Override
    public int bonus() {
        return STRIKE_BONUS;
    }
}

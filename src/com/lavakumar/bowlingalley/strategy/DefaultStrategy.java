package com.lavakumar.bowlingalley.strategy;

public class DefaultStrategy implements Strategy{
    public static final Integer DEFAULT_BONUS = 1;
    @Override
    public int bonus() {
        return DEFAULT_BONUS;
    }
}

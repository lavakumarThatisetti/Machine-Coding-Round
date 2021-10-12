package com.lavakumar.snakeandladder.model;

import java.util.Random;

public class Dice {
    private int numberOfDice;
    private static int MIN = 1;
    Random random;
    public Dice(int numberOfDice){
        random = new Random();
        this.numberOfDice = numberOfDice;
    }

    public int getNumberOfDice() {
        return random.nextInt((this.numberOfDice - MIN) + 1) + 1;
    }
}

package com.lavakumar.snakeandladder.model;

import java.util.HashMap;

public class Entities {
    static HashMap<Integer,Integer> snakes;
    static HashMap<Integer,Integer> ladders;
    static HashMap<Integer,String> players;
    static Entities instance = null;

    public Entities(){
        snakes = new HashMap<>();
        ladders = new HashMap<>();
        players = new HashMap<>();
    }

    public void setSnake(int startPosition, int endPosition) {
        snakes.put(startPosition,endPosition);
    }

    public HashMap<Integer, Integer> getSnakes() {
        return snakes;
    }

    public void setLadder(int startPosition, int endPosition) {
        ladders.put(startPosition,endPosition);
    }

    public HashMap<Integer, Integer> getLadders() {
        return ladders;
    }

    public void setPlayer(int index,String playerName) {
        players.put(index,playerName);
    }

    public HashMap<Integer, String> getPlayers() {
        return players;
    }

    public static Entities getInstance(){
        if( instance == null){
           instance = new Entities();
        }
        return instance;
    }
}

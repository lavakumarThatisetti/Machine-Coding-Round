package com.lavakumar.snakeandladderoptimal.model;

import java.util.HashMap;
import java.util.List;

public class Game {
    HashMap<Integer,Integer> ladders;
    HashMap<Integer,Integer> snakes;
    List<Player> players;

    public Game(HashMap<Integer, Integer> ladders, HashMap<Integer, Integer> snakes, List<Player> players){
        this.ladders = ladders;
        this.snakes = snakes;
        this.players = players;
    }

    public HashMap<Integer, Integer> getLadders() {
        return ladders;
    }

    public HashMap<Integer, Integer> getSnakes() {
        return snakes;
    }

    public List<Player> getPlayers() {
        return players;
    }

}

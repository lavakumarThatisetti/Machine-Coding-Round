package com.lavakumar.snakeandladderoptimal;


import com.lavakumar.snakeandladderoptimal.model.Game;
import com.lavakumar.snakeandladderoptimal.model.Player;
import com.lavakumar.snakeandladderoptimal.service.SnakeLadderService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SnakeLadder {
    public static void main(String[] args) {
        HashMap<Integer,Integer> ladders = new HashMap<>();
        ladders.put(1,7);
        ladders.put(5,20);
        ladders.put(30,50);
        ladders.put(60,80);
        HashMap<Integer,Integer> snakes = new HashMap<>();
        snakes.put(10,1);
        snakes.put(25,3);
        snakes.put(34,7);
        snakes.put(78,2);
        Player player = new Player(1, "player");
        List<Player> players = Collections.singletonList(player);
        Game game = new Game(ladders,snakes,players);
        SnakeLadderService snakeLadderService = new SnakeLadderService(game);
        System.out.println(snakeLadderService.findOptimalPath());
    }
}


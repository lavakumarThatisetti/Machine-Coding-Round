package com.lavakumar.snakeandladderoptimal.service;

import com.lavakumar.snakeandladderoptimal.model.Game;
import com.lavakumar.snakeandladderoptimal.model.Player;

import java.util.LinkedList;
import java.util.Queue;

public class SnakeLadderService {
    Game game;
    Queue<Player> players;
    boolean[] visited = new boolean[100];
    public SnakeLadderService(Game game){
        this.game = game;
        players = new LinkedList<>();
        players.addAll(game.getPlayers());
    }


    public int findOptimalPath(){
        int dice;
        while (!players.isEmpty()) {
            Player currentPlayer = players.poll();
            for(dice=1;dice<=6;dice++) {
                int newPosition = currentPlayer.getPlayerPosition() + dice;
                if (!isVisited(newPosition)) {
                    Player player =  currentPlayer.clone();
                    // Check Ladder
                    // Check Snakes
                    if(game.getLadders().get(newPosition)!=null)
                        newPosition = game.getLadders().get(newPosition);
                    else if(game.getSnakes().get(newPosition)!=null)
                        newPosition = game.getSnakes().get(newPosition);

                    player.setPlayerPosition(newPosition);
                    visited[newPosition-1] = true;
                    player.setTotalMoves(player.getTotalMoves() + 1);
                    if (isPlayerWin(player)) {
                        System.out.println(player.getPlayerName()+" Wins!!");
                        return player.getTotalMoves();
                    } else {
                        players.add(player);
                    }
                }
            }
        }
        return  0;
    }

    private boolean isVisited(int dice){
        return visited[dice-1];
    }
    private boolean isPlayerWin(Player player){
        return player.getPlayerPosition() >=100;
    }

}


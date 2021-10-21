package com.lavakumar.snakeandladder.service;

import com.lavakumar.snakeandladder.model.Dice;
import com.lavakumar.snakeandladder.model.Entities;
import com.lavakumar.snakeandladder.model.PairPosition;

import java.util.HashMap;


public class PlaySnakeAndLadder {
    HashMap<String, PairPosition> playerHistory;
    HashMap<String, Integer> playerLatestPosition;
    Entities entities;
    Dice dice;
    public PlaySnakeAndLadder(int N){
        playerHistory = new HashMap<>();
        playerLatestPosition = new HashMap<>();
        entities = Entities.getInstance();
        dice = new Dice(N);

    }
    public String PlayGame() {
        initilizePlayersStartValue();
        int i=-1;
        do {
            i++; // i th Players playing
            if(i >= entities.getPlayers().size()){
                i=0;
            }
            StringBuilder str = new StringBuilder(); // To print output
            String playeName = entities.getPlayers().get(i);
            str.append(playeName);
            int diceNumber = dice.getNumberOfDice();
            int endPosition  = playerLatestPosition.get(playeName) + diceNumber;
            String sl="";
            if(checkFordiceNumberGreaterThan100(endPosition)) {
                str.append(" rolled a ").append(diceNumber);
                str.append(" and moved from ").append(playerLatestPosition.get(playeName));
                if(entities.getSnakes().get(endPosition)!=null){
                    // Captures snake
                    sl = " after Snake dinner" ;
                    playerLatestPosition.put(playeName,entities.getSnakes().get(endPosition));
                }else{
                    if(entities.getLadders().get(endPosition)!=null){
                        // up ladder
                        sl = " after Ladder ride ";
                        playerLatestPosition.put(playeName,entities.getLadders().get(endPosition));
                    }else{
                        playerLatestPosition.put(playeName,endPosition);
                    }
                }
                str.append(" to ").append(playerLatestPosition.get(playeName));
                str.append(sl);
            }
            System.out.println(str);
        } while (!isPlayerWon(entities.getPlayers().get(i)));

        return entities.getPlayers().get(i);
    }
    private boolean isPlayerWon(String player){
        return playerLatestPosition.get(player) == 100;
    }
    private boolean checkFordiceNumberGreaterThan100(int endPostion){
        return endPostion<=100;
    }

    private void initilizePlayersStartValue(){
        for(int i=0;i<entities.getPlayers().size();i++){
            playerLatestPosition.put(entities.getPlayers().get(i),0);
        }
    }
}

package com.lavakumar.bowlingalley;

import com.lavakumar.bowlingalley.model.Player;

import java.util.List;

public class BowlingGameService {

    List<Player> players;
    Player winnerPlayer;
    public static final int MAX_ROLLS = 21;

    public BowlingGameService(List<Player> players) {
        this.players = players;
    }

    public void startGame() {
        int maxScore = 0;
        for(Player player: players){
            int standingPins = 10;
            for (int index = 0; index < MAX_ROLLS; index++) {
                int numPinDown = takeShot(standingPins);
                standingPins -= numPinDown;
                // Every Frame
                if(index%2 == 0) {
                      player.getScoreBoard().roll(numPinDown);
                } else {
                      if(standingPins == 0){
                          // Frame Shift
                          index++;
                      }
                      player.getScoreBoard().roll(numPinDown);
                      standingPins  = refillThePins();
                }
            }
            int finalScore = player.getScoreBoard().score();
            if (finalScore > maxScore) {
                maxScore = finalScore;
                winnerPlayer = player;
            }
        }
    }

    private int refillThePins() {
        return 10;
    }

    public String getWinner() {
        winnerPlayer.setWin(true);
        return winnerPlayer.toString();
    }

    private int takeShot(int standingPins) {
        return (int) (Math.random() * (standingPins + 1));
    }
}

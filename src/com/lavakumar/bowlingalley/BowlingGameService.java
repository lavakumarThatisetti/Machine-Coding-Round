package com.lavakumar.bowlingalley;

import com.lavakumar.bowlingalley.model.Player;
import com.lavakumar.bowlingalley.constants.AppConstants;

import java.util.List;

public class BowlingGameService {

    List<Player> players;
    Player winnerPlayer;

    public BowlingGameService(List<Player> players) {
        this.players = players;
    }

    public void startGame() {
        int maxScore = 0;
        for(Player player: players){
            int standingPins = AppConstants.TOTAL_PINS;
            for (int index = 0; index < AppConstants.MAX_ROLLS; index++) {
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
        return AppConstants.TOTAL_PINS;
    }

    public String getWinner() {
        winnerPlayer.setWin(true);
        return winnerPlayer.toString();
    }

    private int takeShot(int standingPins) {
        return (int) (Math.random() * (standingPins + 1));
    }
}

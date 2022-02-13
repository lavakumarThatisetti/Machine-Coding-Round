package com.lavakumar.bowlingalley.scoreboard;

import com.lavakumar.bowlingalley.factory.BonusFactory;
import com.lavakumar.bowlingalley.model.Bonus;
import com.lavakumar.bowlingalley.constants.AppConstants;

public class ScoreBoardImpl implements ScoreBoard {

    private final int[] rolls;
    private Integer currentRoll = 0;

    public ScoreBoardImpl(){
        rolls = new int[AppConstants.MAX_ROLLS];
    }

    @Override
    public void roll(Integer noOfPins){
        if (currentRoll == AppConstants.MAX_ROLLS - 1 && (rolls[currentRoll - 1] + rolls[currentRoll - 2] >= 10)) {
            return;
        }
        rolls[currentRoll++] = noOfPins;
    }

    @Override
    public Integer score(){
        int totalScore = 0;
        int set = 0;
        for (int i = 0; i < AppConstants.TOTAL_SETS; i++) {
            if (isStrike(set)) {
                totalScore  += AppConstants.TOTAL_PINS + BonusFactory.getStrategy(Bonus.STRIKE).bonus();
                set += 2;
            } else if (isSpare(set)) {
                totalScore  += AppConstants.TOTAL_PINS + BonusFactory.getStrategy(Bonus.SPARE).bonus();
                set += 2;
            } else {
                totalScore  += rolls[set] + rolls[set + 1];
                set += 2;
            }
        }
        return totalScore + rolls[set];
    }

    private boolean isStrike(int set) {
        return rolls[set] == 10;
    }

    private boolean isSpare(int set) {
        return rolls[set] + rolls[set + 1] == 10;
    }
}

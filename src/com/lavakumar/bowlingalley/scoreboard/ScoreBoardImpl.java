package com.lavakumar.bowlingalley.scoreboard;

import com.lavakumar.bowlingalley.factory.BonusFactory;
import com.lavakumar.bowlingalley.model.Bonus;

public class ScoreBoardImpl implements ScoreBoard {

    private final int[] rolls;
    private Integer currentRoll = 0;
    public static final int MAX_ROLLS = 21;
    public ScoreBoardImpl(){
        rolls = new int[MAX_ROLLS];
    }

    @Override
    public void roll(Integer noOfPins){
        if (currentRoll == MAX_ROLLS - 1 && (rolls[currentRoll - 1] + rolls[currentRoll - 2] > 9)) {
            return;
        }
        rolls[currentRoll++] = noOfPins;
    }

    public Integer score(){
        int totalScore = 0;
        int frame = 0;
        for (int i = 0; i < 10; i++) {
            if (isStrike(frame)) {
                totalScore  += 10 + BonusFactory.getStrategy(Bonus.STRIKE).bonus();
                frame += 2;
            } else if (isSpare(frame)) {
                totalScore  += 10 + BonusFactory.getStrategy(Bonus.SPARE).bonus();
                frame += 2;
            } else {
                totalScore  += rolls[frame] + rolls[frame + 1];
                frame += 2;
            }
        }
        return totalScore + rolls[frame];
    }

    private boolean isStrike(int frame) {
        return rolls[frame] == 10;
    }

    private boolean isSpare(int frame) {
        return rolls[frame] + rolls[frame + 1] == 10;
    }
}

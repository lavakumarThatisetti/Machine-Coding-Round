package com.lavakumar.bowlingalley.factory;

import com.lavakumar.bowlingalley.model.Bonus;
import com.lavakumar.bowlingalley.strategy.DefaultStrategy;
import com.lavakumar.bowlingalley.strategy.SparseStrategy;
import com.lavakumar.bowlingalley.strategy.Strategy;
import com.lavakumar.bowlingalley.strategy.StrikeStrategy;

public class BonusFactory {
    public static Strategy getStrategy(Bonus bonus){
        if(bonus.equals(Bonus.SPARE)){
            return new SparseStrategy();
        } else if(bonus.equals(Bonus.STRIKE)){
            return new StrikeStrategy();
        }else {
            return new DefaultStrategy();
        }
    }
}

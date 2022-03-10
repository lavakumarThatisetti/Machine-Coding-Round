package com.lavakumar.splitwise.model.split;

import com.lavakumar.splitwise.model.User;
import com.lavakumar.splitwise.model.split.Split;

public class PercentSplit extends Split {
    double percent;
    public PercentSplit(User user, double percent) {
        super(user);
        this.percent = percent;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}
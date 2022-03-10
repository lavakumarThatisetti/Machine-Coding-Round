package com.lavakumar.splitwise.model.split;

import com.lavakumar.splitwise.model.User;
import com.lavakumar.splitwise.model.split.Split;

public class ExactSplit extends Split {

    public ExactSplit(User user, double amount) {
        super(user);
        this.amount = amount;
    }
}
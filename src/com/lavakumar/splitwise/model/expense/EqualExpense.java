package com.lavakumar.splitwise.model.expense;

import com.lavakumar.splitwise.model.User;
import com.lavakumar.splitwise.model.split.EqualSplit;
import com.lavakumar.splitwise.model.split.Split;

import java.util.List;

public class EqualExpense extends Expense {

    public EqualExpense(double amount, User expensePaidBy, List<Split> splits, ExpenseData expenseData) {
        super(amount, expensePaidBy, splits, expenseData);
    }

    @Override
    public boolean validate() {
        for(Split split: getSplits()){
            if(!(split instanceof EqualSplit)) return false;
        }
        return true;
    }


}

package com.lavakumar.splitwise;

import com.lavakumar.splitwise.model.ExpenseType;
import com.lavakumar.splitwise.model.Type;
import com.lavakumar.splitwise.model.User;
import com.lavakumar.splitwise.model.expense.ExpenseData;
import com.lavakumar.splitwise.model.split.EqualSplit;
import com.lavakumar.splitwise.model.split.ExactSplit;
import com.lavakumar.splitwise.model.split.PercentSplit;
import com.lavakumar.splitwise.model.split.Split;
import com.lavakumar.splitwise.repository.ExpenseRepository;
import com.lavakumar.splitwise.service.ExpenseService;
import com.lavakumar.splitwise.service.SplitWiseService;
import com.lavakumar.splitwise.service.UserService;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Input Sample Users
        User user1 = new User(1, "u1","u1@gmail.com","9890098900");
        User user2 = new User(2, "u2","u2@gmail.com","9999999999");
        User user3 = new User(3, "u3","u3@gmail.com","9898989899");
        User user4 = new User(4, "u4","u4@gmail.com","8976478292");

        // Adding Expenses
        ExpenseRepository expenseRepository = new ExpenseRepository();
        UserService userService = new UserService(expenseRepository);
        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);
        userService.addUser(user4);
        SplitWiseService service = new SplitWiseService(expenseRepository);

        while (true) {
            Scanner scan = new Scanner(System.in);
            String[] commands = scan.nextLine().split(" ");
            Type type = Type.of(commands[0]);
            switch (type){
                case EXPENSE:
                    String userName = commands[1];
                    double amountSpend = Double.parseDouble(commands[2]);
                    int totalMembers = Integer.parseInt(commands[3]);
                    List<Split> splits = new ArrayList<>();
                    int expenseIndex = 3 + totalMembers + 1;
                    ExpenseType expense = ExpenseType.of(commands[expenseIndex]);
                    switch (expense){
                        case EQUAL:
                            for (int i = 0; i < totalMembers; i++) {
                                splits.add(new EqualSplit(userService.getUser(commands[4+i])));
                            }
                            service.addExpense(
                                    ExpenseType.EQUAL, amountSpend, userName, splits, new ExpenseData("GoaFlight")
                            );
                            break;
                        case EXACT:
                            for (int i = 0; i < totalMembers; i++) {
                                splits.add(
                                        new ExactSplit(
                                                userService.getUser(commands[4+i]),
                                                Double.parseDouble(commands[expenseIndex+i+1]))
                                );
                            }
                            service.addExpense(
                                    ExpenseType.EXACT, amountSpend, userName, splits, new ExpenseData("CabTickets")
                            );

                            break;
                        case PERCENT:
                            for (int i = 0; i < totalMembers; i++) {
                                splits.add(
                                        new PercentSplit(
                                                userService.getUser(commands[4+i]),
                                                Double.parseDouble(commands[expenseIndex+i+1]))
                                );
                            }
                            service.addExpense(
                                    ExpenseType.PERCENT, amountSpend, userName, splits, new ExpenseData("Dinner")
                            );
                            break;
                    }
                    break;
                case SHOW:
                    if(commands.length == 1)
                        service.showBalances();
                    else
                        service.showBalance(commands[1]);
                    break;
                case QUIT:
                     System.out.println("Quiting...");
                     return;
                default:
                    System.out.println("No Expected Argument Found");
                    break;
            }

        }
    }
}

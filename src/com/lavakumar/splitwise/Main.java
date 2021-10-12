package com.lavakumar.splitwise;

import com.lavakumar.splitwise.model.Expense;
import com.lavakumar.splitwise.model.Type;
import com.lavakumar.splitwise.model.User;
import com.lavakumar.splitwise.service.ShareExpenses;
import com.lavakumar.splitwise.service.UserService;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Input Sample Users
        User user1 = new User(1, "u1","u1@gmail.com","9890098900");
        User user2 = new User(2, "u2","u2@gmail.com","9999999999");
        User user3 = new User(3, "u3","u3@gmail.com","9898989899");
        User user4 = new User(4, "u4","u4@gmail.com","8976478292");
        List<User> users = new ArrayList<>(Arrays.asList(user1,user2,user3,user4));

        // Adding Expenses

        ShareExpenses shareExpenses = new ShareExpenses(users);
        UserService userService = new UserService(users);

        while (true){
            Scanner scan = new Scanner(System.in);
            Type type = Type.of(scan.next());
            switch (type){
                case EXPENSE:
                    String user = scan.next();
                    int amountSpend = scan.nextInt();
                    double totalMembers = scan.nextDouble();
                    List<User> owedUsers = new ArrayList<>();
                    for(int i=0;i< totalMembers;i++){
                        owedUsers.add(userService.getUser(scan.next()).get());
                    }
                    Expense expense = Expense.of(scan.next());
                    switch (expense){
                        case EQUAL:
                            shareExpenses.splitEqualExpenses(
                                    user,amountSpend,owedUsers
                            );
                            break;
                        case EXACT:
                            HashMap<User,Double> owedAmountMap = new HashMap<>();
                            double sum = 0;
                            for(int i=0;i<totalMembers;i++){
                                double amount = scan.nextDouble();
                                sum+=amount;
                                owedAmountMap.put(owedUsers.get(i),amount);
                            }
                            if(sum!=amountSpend){
                                System.out.println("Sum not Matches to actual split amount");
                                break;
                            }
                            shareExpenses.splitExactExpenses(
                                    user,amountSpend,owedUsers,owedAmountMap
                            );
                            break;
                        case PERCENT:
                            HashMap<User,Integer> owedPercentageMap = new HashMap<>();
                            int per =0;
                            for(int i=0;i<totalMembers;i++){
                                int amount = scan.nextInt();
                                per+=amount;
                                owedPercentageMap.put(owedUsers.get(i),amount);
                            }
                            if(per!=100){
                                System.out.println("Sum Percentage not Matches to 100");
                                break;
                            }
                            shareExpenses.splitPercentageExpenses(
                                    user,amountSpend,owedUsers,owedPercentageMap
                            );
                            break;
                    }
                    break;
                case SHOW:
                    String userName = scan.next();
                    shareExpenses.showExpenses(userName);
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

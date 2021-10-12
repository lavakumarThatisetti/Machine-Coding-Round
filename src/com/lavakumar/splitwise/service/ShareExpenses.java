package com.lavakumar.splitwise.service;

import com.lavakumar.splitwise.model.OwedUser;
import com.lavakumar.splitwise.model.User;

import java.util.*;

public class ShareExpenses {
    List<User> users;
    HashMap<User, HashMap<User,OwedUser>> expensesMap = new HashMap<>();


    public ShareExpenses(List<User> users){
        this.users = users;
    }


    public void splitEqualExpenses(String userName, double totalAmount,List<User> owedUsers){
        Optional<User> user = getUser(userName);
        int totalSharedUsers = owedUsers.size();
        if(user.isPresent()){
            double shares = totalAmount/totalSharedUsers;
            HashMap<User,OwedUser> userSpeicficMap;
            if(expensesMap.get(user.get())!=null){
                userSpeicficMap = expensesMap.get(user.get());
            }else{
                userSpeicficMap = new HashMap<>();
            }
            for (User owedUser : owedUsers) {
                if(userSpeicficMap.get(owedUser)!=null){
                    double initialBalance = userSpeicficMap.get(owedUser).getBalance();
                    userSpeicficMap.get(owedUser).setBalance(initialBalance + shares);
                }else{
                    OwedUser owedUserObj = new OwedUser(owedUser, shares);
                    userSpeicficMap.put(owedUser, owedUserObj);
                }
            }
            expensesMap.put(user.get(), userSpeicficMap);
        }else{
            System.out.println("User Not in BList");
        }

    }

    public void splitExactExpenses(String userName, double totalAmount, List<User> owedUsers, HashMap<User,Double> owedAmountMap ){
        Optional<User> user = getUser(userName);
        if(user.isPresent()){
            HashMap<User,OwedUser> userSpeicficMap;
            if(expensesMap.get(user.get())!=null){
                userSpeicficMap = expensesMap.get(user.get());
            }else{
                userSpeicficMap = new HashMap<>();
            }
            for (User owedUser : owedUsers) {
                if(userSpeicficMap.get(owedUser)!=null){
                    double initialBalance = userSpeicficMap.get(owedUser).getBalance();
                    userSpeicficMap.get(owedUser).setBalance(initialBalance + owedAmountMap.get(owedUser));
                }else{
                    OwedUser owedUserObj = new OwedUser(owedUser, owedAmountMap.get(owedUser));
                    userSpeicficMap.put(owedUser, owedUserObj);
                }
            }
            expensesMap.put(user.get(), userSpeicficMap);
        }else{
            System.out.println("User Not in BList");
        }

    }

    public void splitPercentageExpenses(String userName, double totalAmount, List<User> owedUsers, HashMap<User,Integer> owedPercentageMap ){
        Optional<User> user = getUser(userName);
        if(user.isPresent()){
            HashMap<User,OwedUser> userSpeicficMap;
            if(expensesMap.get(user.get())!=null){
                userSpeicficMap = expensesMap.get(user.get());
            }else{
                userSpeicficMap = new HashMap<>();
            }
            for (User owedUser : owedUsers) {
                if(userSpeicficMap.get(owedUser)!=null){
                    double initialBalance = userSpeicficMap.get(owedUser).getBalance();
                    double percentageValue = getPercentageAmount(totalAmount,owedPercentageMap.get(owedUser));
                    userSpeicficMap.get(owedUser).setBalance(initialBalance + percentageValue);
                }else{
                    OwedUser owedUserObj = new OwedUser(owedUser, getPercentageAmount(totalAmount,owedPercentageMap.get(owedUser)));
                    userSpeicficMap.put(owedUser, owedUserObj);
                }
            }
            expensesMap.put(user.get(), userSpeicficMap);
        }else{
            System.out.println("User Not in BList");
        }
    }

    private double getPercentageAmount(double amount, int percentage){
        return (amount*percentage)/100;
    }

    public void showExpenses(String userName){
        Optional<User> showUser = getUser(userName);
        if(showUser.isPresent()){
            // Expenses For you statements
            if(expensesMap.get(showUser.get())!=null){
                HashMap<User,OwedUser> owedUsers = expensesMap.get(showUser.get());
                for(Map.Entry<User, OwedUser> entry:owedUsers.entrySet()){
                    System.out.println(entry.getKey().getUserName()+" Owes "+showUser.get().getUserName()+": "+entry.getValue().getBalance());
                }
            }

            // Expenses your responsbile statments
            users.stream()
                    .filter(user -> expensesMap.get(user)!=null)
                    .filter(user -> !user.equals(showUser.get()))
                    .forEach(user ->
                       expensesMap.get(user).forEach(
                                (user1, owedUser) -> {
                                    if(user1.getUserName().equals(userName)){
                                        System.out.println(user.getUserName()+" Owes "+owedUser.getUser().getUserName()+": "+owedUser.getBalance());
                                    }
                                }
                        )
                    );
        }else{
            if(expensesMap.size()>0){
                users.stream()
                        .filter(user -> expensesMap.get(user)!=null)
                        .forEach(user ->{
                            HashMap<User,OwedUser> owedUsers = expensesMap.get(user);
                            for(Map.Entry<User, OwedUser> entry:owedUsers.entrySet()){
                                System.out.println(entry.getKey().getUserName()+" Owes "+user.getUserName()+": "+entry.getValue().getBalance());
                            }
                        });
            }else{
                System.out.println("NO Balance");
            }


        }

    }

    private Optional<User> getUser(String userName){
        return users.stream().filter(u->u.getUserName().equals(userName)).findFirst();
    }

}

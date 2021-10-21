package com.lavakumar.splitwise.service;

import com.lavakumar.splitwise.model.OwedUser;
import com.lavakumar.splitwise.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
            HashMap<User,OwedUser> userSpecificMap;
            if(expensesMap.get(user.get())!=null){
                userSpecificMap = expensesMap.get(user.get());
            }else{
                userSpecificMap = new HashMap<>();
            }
            for (User owedUser : owedUsers) {
                if(expensesMap.get(owedUser)!=null && owedUser!=user.get()) {
                    double remaining = expensesMap.get(owedUser).get(user.get()).getBalance() - shares;
                    if(remaining > 0) {
                        expensesMap.get(owedUser).get(user.get()).setBalance(remaining);
                    } else if(remaining < 0){
                        expensesMap.get(owedUser).remove(user.get());
                        OwedUser owedUserObj = new OwedUser(owedUser, Math.abs(remaining));
                        userSpecificMap.put(owedUser, owedUserObj);
                        // ADD
                    } else {
                        expensesMap.get(owedUser).remove(user.get());
                    }
                } else {
                    if(userSpecificMap.get(owedUser)!=null) {
                        double initialBalance = userSpecificMap.get(owedUser).getBalance();
                        userSpecificMap.get(owedUser).setBalance(initialBalance + shares);
                    }else{
                        OwedUser owedUserObj = new OwedUser(owedUser, shares);
                        userSpecificMap.put(owedUser, owedUserObj);
                    }
                }
            }
            expensesMap.put(user.get(), userSpecificMap);
        }else{
            System.out.println("User Not in BList");
        }

    }

    public void splitExactExpenses(String userName, double totalAmount, List<User> owedUsers, HashMap<User,Double> owedAmountMap ){
        Optional<User> user = getUser(userName);
        if(user.isPresent()){
            HashMap<User,OwedUser> userSpecificMap;
            if(expensesMap.get(user.get())!=null){
                userSpecificMap = expensesMap.get(user.get());
            }else{
                userSpecificMap = new HashMap<>();
            }
            for (User owedUser : owedUsers) {
                if(expensesMap.get(owedUser)!=null && owedUser!=user.get()) {
                    double remaining = expensesMap.get(owedUser).get(user.get()).getBalance() - owedAmountMap.get(owedUser);
                    if(remaining > 0) {
                        expensesMap.get(owedUser).get(user.get()).setBalance(remaining);
                    } else if(remaining < 0){
                        expensesMap.get(owedUser).remove(user.get());
                        OwedUser owedUserObj = new OwedUser(owedUser, Math.abs(remaining));
                        userSpecificMap.put(owedUser, owedUserObj);
                        // ADD
                    } else {
                        expensesMap.get(owedUser).remove(user.get());
                    }
                } else {
                    if (userSpecificMap.get(owedUser) != null) {
                        double initialBalance = userSpecificMap.get(owedUser).getBalance();
                        userSpecificMap.get(owedUser).setBalance(initialBalance + owedAmountMap.get(owedUser));
                    } else {
                        OwedUser owedUserObj = new OwedUser(owedUser, owedAmountMap.get(owedUser));
                        userSpecificMap.put(owedUser, owedUserObj);
                    }
                }
            }
            expensesMap.put(user.get(), userSpecificMap);
        }else{
            System.out.println("User Not in BList");
        }

    }

    public void splitPercentageExpenses(String userName, double totalAmount, List<User> owedUsers, HashMap<User,Integer> owedPercentageMap ){
        Optional<User> user = getUser(userName);
        if(user.isPresent()){
            HashMap<User,OwedUser> userSpecificMap;
            if(expensesMap.get(user.get())!=null){
                userSpecificMap = expensesMap.get(user.get());
            }else{
                userSpecificMap = new HashMap<>();
            }
            for (User owedUser : owedUsers) {
                if(expensesMap.get(owedUser)!=null && owedUser!=user.get()) {
                    double remaining = expensesMap.get(owedUser).get(user.get()).getBalance() - getPercentageAmount(totalAmount, owedPercentageMap.get(owedUser));
                    if(remaining > 0) {
                        expensesMap.get(owedUser).get(user.get()).setBalance(remaining);
                    } else if(remaining < 0){
                        expensesMap.get(owedUser).remove(user.get());
                        OwedUser owedUserObj = new OwedUser(owedUser, Math.abs(remaining));
                        userSpecificMap.put(owedUser, owedUserObj);
                        // ADD
                    } else {
                        expensesMap.get(owedUser).remove(user.get());
                    }
                } else {
                    if (userSpecificMap.get(owedUser) != null) {
                        double initialBalance = userSpecificMap.get(owedUser).getBalance();
                        double percentageValue = getPercentageAmount(totalAmount, owedPercentageMap.get(owedUser));
                        userSpecificMap.get(owedUser).setBalance(initialBalance + percentageValue);
                    } else {
                        OwedUser owedUserObj = new OwedUser(owedUser, getPercentageAmount(totalAmount, owedPercentageMap.get(owedUser)));
                        userSpecificMap.put(owedUser, owedUserObj);
                    }
                }
            }
            expensesMap.put(user.get(), userSpecificMap);
        }else{
            System.out.println("User Not in BList");
        }
    }

    private double getPercentageAmount(double amount, int percentage){
        return (amount*percentage)/100;
    }

    public void showExpenses(String userName){
        if(userName.isEmpty() && expensesMap.size()==0){
            System.out.println("NO Balance");
            return;
        } else if(userName.isEmpty()) {
            showAllUsersData();
            return;
        }
        AtomicBoolean isUserHasData = new AtomicBoolean(false);
        Optional<User> showUser = getUser(userName);
        if(showUser.isPresent()){
            // Expenses For you statements
            if(expensesMap.get(showUser.get())!=null){
                HashMap<User,OwedUser> owedUsers = expensesMap.get(showUser.get());
                for(Map.Entry<User, OwedUser> entry:owedUsers.entrySet()){
                    isUserHasData.set(true);
                    if(!entry.getKey().getUserName().equals(showUser.get().getUserName()))
                    System.out.println(entry.getKey().getUserName()+" Owes "+showUser.get().getUserName()+": "+entry.getValue().getBalance());
                }
            }
            users
                    .forEach(user ->{
                              if(expensesMap.get(user) !=null && user != showUser.get()) {
                                  expensesMap.get(user).forEach(
                                          (user1, owedUser) -> {
                                              if(user1.getUserName().equals(showUser.get().getUserName())
                                          ||owedUser.getUser().getUserName().equals(showUser.get().getUserName())){
                                                    isUserHasData.set(true);
                                                    System.out.println(owedUser.getUser().getUserName()+" Owes "+user.getUserName()+": "+owedUser.getBalance());
                                              }
                                          }
                                  );
                              }
                            }
                    );
            if(!isUserHasData.get()){
                System.out.println("NO Balance");
            }
            isUserHasData.set(false);
        }else {
            System.out.println("NO User");
        }

    }

    private void showAllUsersData(){
        users.forEach(user ->{
            if(expensesMap.get(user) !=null) {
                expensesMap.get(user).forEach(
                        (user1, owedUser) -> {
                            if(!owedUser.getUser().getUserName().equals(user.getUserName()))
                                System.out.println(owedUser.getUser().getUserName() + " Owes " + user.getUserName() + ": " + owedUser.getBalance());
                        }
                );
            }
        });
    }

    private Optional<User> getUser(String userName){
        for(User user: users){
            if(user.getUserName().equals(userName.trim())){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

}

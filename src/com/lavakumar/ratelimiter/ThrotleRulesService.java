package com.lavakumar.ratelimiter;


import java.util.HashMap;

public class ThrotleRulesService {
    HashMap<String, ThrotleRule> clientThrotleRules;
    private static volatile ThrotleRulesService instance;

    public ThrotleRulesService(){
        clientThrotleRules = new HashMap<>();
    }

    public void createRule(String clientId, ThrotleRule throtleRule){
        clientThrotleRules.put(clientId, throtleRule);
    }

    public ThrotleRule getClientRules(String clientId){
        return clientThrotleRules.get(clientId);
    }

    public static ThrotleRulesService getInstance(){

        if (instance == null) {
            synchronized (ThrotleRulesService.class) {
                // Double check
                if (instance == null) {
                    instance = new ThrotleRulesService();
                }
            }
        }
        return instance;
    }
}

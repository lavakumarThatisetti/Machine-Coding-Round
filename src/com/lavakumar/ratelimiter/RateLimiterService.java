package com.lavakumar.ratelimiter;

import com.lavakumar.ratelimiter.algorthims.RateLimiter;
import com.lavakumar.ratelimiter.algorthims.SlidingWindow;
import com.lavakumar.ratelimiter.algorthims.TokenBucket;

import java.util.HashMap;

public class RateLimiterService {
    HashMap<String, ThrotleRule> clientRulesCache;
    HashMap<String, RateLimiter> rateLimiterHashMap;
    ThrotleRulesService throtleRulesService;

    public RateLimiterService(){
        this.throtleRulesService = ThrotleRulesService.getInstance();
        this.clientRulesCache = new HashMap<>();
        this.rateLimiterHashMap = new HashMap<>();
    }


    public boolean isRateLimitedUserRequest(String userId){
        createUserIfNotTheir(userId);
        return rateLimiterHashMap.get(userId).allowRequest();
    }

    private void createUserIfNotTheir(String userId){
        if(!clientRulesCache.containsKey(userId)){
            ThrotleRule clientRules = throtleRulesService.getClientRules(userId);
            clientRulesCache.put(userId, clientRules);
        }
        if(!rateLimiterHashMap.containsKey(userId)){
            ThrotleRule throtleRule = clientRulesCache.get(userId);
            RateLimiter rateLimiter = new TokenBucket(throtleRule.getBucketSize(), throtleRule.getRefillRate());
            rateLimiterHashMap.put(userId,rateLimiter);
        }
    }
}

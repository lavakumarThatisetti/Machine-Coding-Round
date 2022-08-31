package com.lavakumar.ratelimiter;


public class UserIdentificationService {

    RateLimiterService rateLimiterService;

    public UserIdentificationService(){
        this.rateLimiterService = new RateLimiterService();
    }
    public String serveRequest(String clientId){

        boolean isAllowed = rateLimiterService.isRateLimitedUserRequest(clientId);
        if(isAllowed){
            return "Request Served";
        } else return "Too Many Requests please try again";
    }

}

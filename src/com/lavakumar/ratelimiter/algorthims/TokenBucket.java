package com.lavakumar.ratelimiter.algorthims;


/*
   10  , 10  , <=10,
*/
public class TokenBucket implements RateLimiter {
    private final long maxBucketSize;
    private final long refillRate;
    private double currentBucketSize;
    private long lastRefillTimeStamp;

    public TokenBucket(long maxBucketSize, long refillRate){
        this.maxBucketSize = maxBucketSize;
        this.refillRate = refillRate;
        currentBucketSize = maxBucketSize;
        lastRefillTimeStamp = System.nanoTime();
    }

    @Override
    public synchronized boolean allowRequest(){
        refill();
        if(currentBucketSize>=1){
            currentBucketSize-=1;
          //  System.out.print("  current bucket Size: "+currentBucketSize);
            return true;
        }
        return false;
    }

    private void refill(){
        long now = System.nanoTime();
        double tokensToAdd  = (now-lastRefillTimeStamp)*refillRate/1e9;
        System.out.print("before refilled : "+currentBucketSize+"  ");
        currentBucketSize = Math.min(currentBucketSize+tokensToAdd, maxBucketSize);
        System.out.print("After refilled : "+currentBucketSize);
        lastRefillTimeStamp = now;
    }

}

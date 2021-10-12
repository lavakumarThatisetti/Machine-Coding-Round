package com.lavakumar.tokenbucket;

public class TokenBucket {
    private final long maxBucketSize;
    private final long refillRate;
    private double currentBucketSize;
    private long lastRefillTimeStamp;

    public TokenBucket(long maxBucketSize,long refillRate){
        this.maxBucketSize = maxBucketSize;
        this.refillRate = refillRate;
        currentBucketSize = maxBucketSize;
        lastRefillTimeStamp = System.nanoTime();
    }

    public synchronized boolean allowRequest(int tokens){
        refill();
        if(currentBucketSize>tokens){
            currentBucketSize-=tokens;
            System.out.println("current bucket Size: "+currentBucketSize);
            return true;
        }
        return false;
    }

    private void refill(){
        long now = System.nanoTime();
        double tokensToAdd  = (now-lastRefillTimeStamp)/1e9;
        System.out.println("before refilled : "+currentBucketSize);
        currentBucketSize = Math.min(currentBucketSize+tokensToAdd, maxBucketSize);
        System.out.println("refilled : "+currentBucketSize);
        lastRefillTimeStamp = now;
    }

}

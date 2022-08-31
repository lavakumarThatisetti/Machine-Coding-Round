package com.lavakumar.ratelimiter;

public class ThrotleRule {
    public long bucketSize;
    public long refillRate;

    public ThrotleRule(){
        this.bucketSize = 10;
        this.refillRate = 10;
    }

    public ThrotleRule(long bucketSize, long refillRate) {
        this.bucketSize = bucketSize;
        this.refillRate = refillRate;
    }

    public long getBucketSize() {
        return bucketSize;
    }

    public long getRefillRate() {
        return refillRate;
    }
}

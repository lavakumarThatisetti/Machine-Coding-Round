package com.lavakumar.ratelimiter;

public class Config {
    public static long maxBucketSize = 10;
    public static long refillRate = 10;
    public static int timeWindowInSeconds = 1;
    public static int bucketSize = 10;
    public static int leakyCapacity = 10;
}

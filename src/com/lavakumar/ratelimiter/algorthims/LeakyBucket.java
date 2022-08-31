package com.lavakumar.ratelimiter.algorthims;


import com.lavakumar.ratelimiter.Config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LeakyBucket implements RateLimiter {
    BlockingQueue<Integer> queue;

    public LeakyBucket() {
        this.queue = new LinkedBlockingQueue<>(Config.leakyCapacity);
    }

    @Override
    public synchronized boolean allowRequest() {
        if(queue.remainingCapacity() > 0){
            queue.add(1);
            return true;
        }
        return false;
    }
}
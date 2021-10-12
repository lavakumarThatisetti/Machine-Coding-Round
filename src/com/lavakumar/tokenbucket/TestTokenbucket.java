package com.lavakumar.tokenbucket;

public class TestTokenbucket {
    public static void main(String[] args)  throws Exception{
        TokenBucket tokenBucket = new TokenBucket(10,10);
        Thread.sleep(300);
        tokenBucket.allowRequest(6);
        Thread.sleep(200);
        tokenBucket.allowRequest(5);
        Thread.sleep(1000);
        tokenBucket.allowRequest(5);
    }
}

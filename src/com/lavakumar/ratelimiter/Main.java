package com.lavakumar.ratelimiter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {

        ThrotleRule rule = new ThrotleRule();
        ThrotleRulesService throtleRulesService = ThrotleRulesService.getInstance();
        throtleRulesService.createRule("client1", rule);

        UserIdentificationService request = new UserIdentificationService();

        ExecutorService executor = Executors.newFixedThreadPool(1);

        ScheduledExecutorService scheduledExecutor  =  Executors.newScheduledThreadPool(5);

        Long startTime = System.currentTimeMillis();

        for (int i = 0; i < 12; i++) {
            executor.execute(() -> {
                System.out.println(" client1 "+Thread.currentThread().getName() + "--" + request.serveRequest("client1"));
          //      System.out.println(" client2 "+Thread.currentThread().getName() + "--" + handleRequest.serveRequest("client2"));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        Runnable r = () -> {
            System.out.println(" client1 "+Thread.currentThread().getName() + "--" + request.serveRequest("client1"));
        };
     //   scheduledExecutor.scheduleAtFixedRate( r,0,500, TimeUnit.MILLISECONDS);
        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            Long endTime = System.currentTimeMillis();
            System.out.println("total time " + (endTime - startTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

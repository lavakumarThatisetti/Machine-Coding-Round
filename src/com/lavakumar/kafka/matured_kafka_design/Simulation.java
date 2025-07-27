package com.lavakumar.kafka.matured_kafka_design;

import com.lavakumar.kafka.OffsetResetStrategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Simulation {

    public static void main(String[] args) throws InterruptedException {
        KafkaBroker broker = new KafkaBroker();
        broker.createTopic("orders", 4);

        Consumer c1 = new Consumer("Consumer-1", broker, "orders");
        Consumer c2 = new Consumer("Consumer-2", broker, "orders");

        System.out.println("\n--- Initial Rebalance ---");
        broker.registerConsumer("orders", c1);
        broker.registerConsumer("orders", c2);

        Thread t1 = new Thread(c1);
        Thread t2 = new Thread(c2);
        t1.start();
        t2.start();

        System.out.println("\n---Start Publishing ---");

        ExecutorService producerPool = Executors.newFixedThreadPool(1);
        producerPool.submit(() -> {
            for (int i = 0; i < 20; i++) {
                broker.publish("orders", "Order - " + i);
                sleep(200);
            }
        });

        producerPool.shutdown();
        producerPool.awaitTermination(5, TimeUnit.SECONDS);

        Thread.sleep(3000);

        System.out.println("\n--- Adding Consumer-3 and Triggering Rebalance ---");
        Consumer c3 = new Consumer("Consumer-3", broker, "orders");
        broker.registerConsumer("orders", c3);
        Thread t3 = new Thread(c3);
        t3.start();

        Thread.sleep(3000);

        System.out.println("\nResetting Consumer-1's offsets to EARLIEST...");
        c1.getAssignedPartitions().forEach(p -> System.out.println("Consumer-1 owns Partition: " + p));
        c1.forceResetOffset(OffsetResetStrategy.EARLIEST, t1);

        System.out.println("Now after reset it started again consume");


        Thread.sleep(3000);

        c1.stop();
        c2.stop();
        c3.stop();
        t1.interrupt();
        t2.interrupt();
        t3.interrupt();
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}

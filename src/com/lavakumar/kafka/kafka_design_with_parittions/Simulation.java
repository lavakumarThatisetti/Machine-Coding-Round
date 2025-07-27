package com.lavakumar.kafka.kafka_design_with_parittions;

import com.lavakumar.kafka.OffsetResetStrategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Simulation {
    public static void main(String[] args) throws InterruptedException {

        KafkaBroker broker = new KafkaBroker();

        broker.createTopic("bookings", 3);
        broker.createTopic("payments", 3);

        Producer producer = new Producer(broker);

        Consumer bookingConsumer = new Consumer("Booking-Consumer", broker, "bookings");
        Consumer paymentConsumer = new Consumer("Payment-Consumer", broker, "payments");

        Thread t1 = new Thread(bookingConsumer);
        Thread t2 = new Thread(paymentConsumer);
        t1.start();
        t2.start();

        ExecutorService producerPool = Executors.newFixedThreadPool(2);
        producerPool.submit(() -> {
            for (int i = 0; i < 10; i++) {
                producer.send("bookings", "New Booking - " + i);
                sleep(200);
            }
        });

        producerPool.submit(() -> {
            for (int i = 0; i < 5; i++) {
                producer.send("payments", "Payment Done - " + i);
                sleep(300);
            }
        });

        producerPool.shutdown();
        producerPool.awaitTermination(5, TimeUnit.SECONDS);

        Thread.sleep(2000);

        System.out.println("\nResetting bookings topic consumer to EARLIEST...");
        bookingConsumer.forceResetOffset(OffsetResetStrategy.EARLIEST, t1);

        Thread.sleep(2000);
        bookingConsumer.stop();
        paymentConsumer.stop();
        t1.interrupt();
        t2.interrupt();
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}

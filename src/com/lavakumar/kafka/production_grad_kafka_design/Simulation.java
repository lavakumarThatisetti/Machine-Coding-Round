package com.lavakumar.kafka.production_grad_kafka_design;

import com.lavakumar.kafka.OffsetResetStrategy;

public class Simulation {
    public static void main(String[] args) throws InterruptedException {

        KafkaCluster cluster = new KafkaCluster();

        Producer producer = new Producer(cluster);

        KafkaBroker broker1 = new KafkaBroker("Broker1");
        KafkaBroker broker2 = new KafkaBroker("Broker2");


        cluster.registerBroker(broker1);
        cluster.registerBroker(broker2);

        cluster.createTopic("payments", 4);

        ConsumerWithGroup c1 = new ConsumerWithGroup("C1", cluster, "payments", "group-A");
        ConsumerWithGroup c2 = new ConsumerWithGroup("C2", cluster, "payments", "group-A");

        Thread t1 = new Thread(c1);
        Thread t2 = new Thread(c2);
        t1.start();
        t2.start();

        System.out.println("\n--- Start Publishing ---");
        for (int i = 0; i < 20; i++) {
            producer.send("payments", "Txn-" + i);
            Thread.sleep(150);
        }

        Thread.sleep(2000);

        System.out.println("\n--- Adding Consumer C3 to group-A, triggering rebalance ---");
        ConsumerWithGroup c3 = new ConsumerWithGroup("C3", cluster, "payments", "group-A");
        Thread t3 = new Thread(c3);
        t3.start();

        Thread.sleep(2000);

        System.out.println("\n--- Resetting C1's offset to EARLIEST ---");
        c1.forceResetOffset(OffsetResetStrategy.EARLIEST, t1);

        Thread.sleep(3000);

        c1.stop();
        c2.stop();
        c3.stop();
        t1.interrupt();
        t2.interrupt();
        t3.interrupt();
    }
}

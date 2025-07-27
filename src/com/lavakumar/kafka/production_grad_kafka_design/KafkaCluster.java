package com.lavakumar.kafka.production_grad_kafka_design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class KafkaCluster {
    private final Map<Integer, KafkaBroker> brokers = new HashMap<>();

    private final Map<String, Topic> topics = new HashMap<>();

    // topicName -> ( partitionId -> BrokerId )
    private final Map<String, Map<Integer, Integer>> partitionToBroker = new HashMap<>();
    private int brokerCounter = 0;
    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);

    public int registerBroker(KafkaBroker broker) {
        int id = brokerCounter++;
        brokers.put(id, broker);
        return id;
    }

    /**
     * Create a topic with partition count. Distributes partitions across brokers.
     */
    public void createTopic(String topicName, int partitions) {
        System.out.println("Created Topic "+ topicName + " With Partitions " + partitions);
        topics.putIfAbsent(topicName, new Topic(topicName, partitions));

        for (int i = 0; i < partitions; i++) {
            int brokerId = i % brokers.size();
            KafkaBroker broker = brokers.get(brokerId);
            broker.createPartition(topicName, i);
            partitionToBroker.computeIfAbsent(topicName, k -> new HashMap<>()).put(i, brokerId);
            System.out.println("Partition-" + i + " assigned to Broker [" + broker.getBrokerName() + "]");
        }
    }

    /**
     * Publishes message to a topic using round-robin partition assignment.
     */
    public void publish(String topicName, String message) {
        int partitionId = roundRobinIndex.getAndUpdate(i -> (i + 1) % partitionToBroker.get(topicName).size());
        int brokerId = partitionToBroker.get(topicName).get(partitionId);
        KafkaBroker broker = brokers.get(brokerId);
        broker.publish(topicName, partitionId, message);
    }

    public void rebalance(String topicName, String groupId) {
        List<ConsumerWithGroup> consumers = new ArrayList<>();

        for (KafkaBroker broker : brokers.values()) {
            consumers.addAll(broker.getConsumers(topicName, groupId));
        }

        if (consumers.isEmpty()) return;

        System.out.println("\n--- Rebalancing Partitions for Topic: " + topicName + " | Group: " + groupId + " ---");

        for (ConsumerWithGroup c : consumers) {
            c.clearAssignments();
        }

        int numPartitions = topics.get(topicName).getPartitionCount();

        for (int i = 0; i < numPartitions; i++) {
            ConsumerWithGroup consumer = consumers.get(i % consumers.size());
            consumer.assignPartition(i);
            System.out.println("Partition-" + i + " assigned to [" + consumer.getName() + "]");
        }
    }


    public KafkaBroker getBroker(int brokerId) {
        return brokers.get(brokerId);
    }

    public Map<Integer, KafkaBroker> getBrokers() {
        return brokers;
    }

    public Map<String, Map<Integer, Integer>> getPartitionToBroker() {
        return partitionToBroker;
    }
}
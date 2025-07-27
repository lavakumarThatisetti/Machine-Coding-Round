package com.lavakumar.kafka.matured_kafka_design;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class KafkaBroker {
    private final Map<String, Topic> topics = new ConcurrentHashMap<>();
    private final Map<String, List<Consumer>> topicConsumers = new ConcurrentHashMap<>();

    public void createTopic(String topicName, int partitions) {
        topics.putIfAbsent(topicName, new Topic(topicName, partitions));
    }

    public void registerConsumer(String topicName, Consumer consumer) {
        topicConsumers.computeIfAbsent(topicName, k -> new ArrayList<>()).add(consumer);
        rebalance(topicName);
    }

    public void rebalance(String topicName) {

        Topic topic = topics.get(topicName);
        List<Consumer> consumers = topicConsumers.get(topicName);

        for (Consumer consumer : consumers) {
            consumer.clearAssignments();
        }

        if (topic == null || consumers == null || consumers.isEmpty()) return;

        System.out.println("\n--- Rebalancing Partitions for Topic: " + topicName + " ---");

        int numPartitions = topic.getPartitionCount();
        for (int i = 0; i < numPartitions; i++) {
            Consumer consumer = consumers.get(i % consumers.size());
            consumer.assignPartition(i);
            System.out.println("Partition-" + i + " assigned to [" + consumer.getName() + "]");
        }
    }

    public void publish(String topicName, String value) {
        Topic topic = topics.get(topicName);
        if (topic != null) {
            topic.publish(new Message(value));
        }
    }


    public Topic getTopic(String topicName) {
        return topics.get(topicName);
    }
}

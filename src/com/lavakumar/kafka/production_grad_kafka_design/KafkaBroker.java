package com.lavakumar.kafka.production_grad_kafka_design;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class KafkaBroker {

    private String brokerName;

    private final Map<String, Map<Integer, Partition>> topicPartitions = new ConcurrentHashMap<>();
    private final Map<String, Map<String, List<ConsumerWithGroup>>> consumerGroups = new ConcurrentHashMap<>();

    KafkaBroker(String brokerName) {
        this.brokerName = brokerName;
        System.out.println("Broker "+brokerName + " Created ");
    }

    public void createPartition(String topicName, int partitionId) {
        topicPartitions.computeIfAbsent(topicName, k -> new ConcurrentHashMap<>())
                .putIfAbsent(partitionId, new Partition());
    }

    public Partition getPartition(String topicName, int partitionId) {
        return topicPartitions.getOrDefault(topicName, Collections.emptyMap()).get(partitionId);
    }

    public List<ConsumerWithGroup> getConsumers(String topicName, String groupId) {
        return consumerGroups.getOrDefault(topicName, Collections.emptyMap())
                .getOrDefault(groupId, Collections.emptyList());
    }

    public void publish(String topicName, int partitionId, String value) {
        Partition partition = getPartition(topicName, partitionId);
        if (partition != null) {
            partition.publish(new Message(value));
        }
    }

    public void registerConsumer(String topicName, String groupId, ConsumerWithGroup consumer) {
        System.out.println("\n Broker [" + brokerName + "] registered consumer " + consumer.getName() + " for Topic: " + topicName);
        consumerGroups.computeIfAbsent(topicName, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(groupId, g -> new ArrayList<>())
                .add(consumer);
    }

    public String getBrokerName() {
        return brokerName;
    }
}


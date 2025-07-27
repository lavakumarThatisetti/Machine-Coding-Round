package com.lavakumar.kafka.production_grad_kafka_design;

import com.lavakumar.kafka.OffsetResetStrategy;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class ConsumerWithGroup implements Runnable {
    private final String name;
    private final String groupId;
    private final KafkaCluster cluster;
    private final String topicName;
    private final Set<Integer> assignedPartitions = ConcurrentHashMap.newKeySet();
    private final Map<Integer, Integer> partitionOffsets = new ConcurrentHashMap<>();
    private volatile boolean running = true;

    public ConsumerWithGroup(String name, KafkaCluster cluster, String topicName, String groupId) {
        this.name = name;
        this.cluster = cluster;
        this.topicName = topicName;
        this.groupId = groupId;

        // Register with all brokers for group membership awareness
        for (KafkaBroker broker : cluster.getBrokers().values()) {
            broker.registerConsumer(topicName, groupId, this);
        }
        cluster.rebalance(topicName, groupId);
    }

    public void assignPartition(int partitionId) {
        System.out.println("[" + name + "] Assigned Partition-" + partitionId);
        assignedPartitions.add(partitionId);
        partitionOffsets.putIfAbsent(partitionId, 0);
    }

    public void clearAssignments() {
        assignedPartitions.clear();
    }

    public void resetOffset(OffsetResetStrategy strategy) {
        for (int pid : assignedPartitions) {
            int brokerId = cluster.getPartitionToBroker().get(topicName).get(pid);
            KafkaBroker responsibleBroker = cluster.getBroker(brokerId);
            Partition partition = responsibleBroker.getPartition(topicName, pid);
            int offset = strategy == OffsetResetStrategy.EARLIEST ? 0 : partition.size();
            partitionOffsets.put(pid, offset);
        }
    }

    public void forceResetOffset(OffsetResetStrategy strategy, Thread consumerThread) {
        resetOffset(strategy);
        consumerThread.interrupt();
    }

    public void stop() {
        running = false;
    }

    @Override
    /**
     * Kafka-style polling loop.
     */
    public void run() {
        while (running) {
            poll(Duration.ofMillis(100));
        }
    }

    public void poll(Duration timeout) {
        for (int pid : assignedPartitions) {
            int brokerId = cluster.getPartitionToBroker().get(topicName).get(pid);
            KafkaBroker responsibleBroker = cluster.getBroker(brokerId);
            Partition partition = responsibleBroker.getPartition(topicName, pid);
            int offset = partitionOffsets.getOrDefault(pid, 0);
            Message msg = partition.readBlocking(offset);
            if (msg != null) {
                System.out.println("[" + name + "] Partition-" + pid + " Message: " + msg.getValue());
                partitionOffsets.put(pid, offset + 1);
            }
        }
        try {
            Thread.sleep(timeout.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String getName() {
        return name;
    }
}
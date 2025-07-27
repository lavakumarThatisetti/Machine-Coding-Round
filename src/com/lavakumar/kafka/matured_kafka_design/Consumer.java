package com.lavakumar.kafka.matured_kafka_design;

import com.lavakumar.kafka.OffsetResetStrategy;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Consumer class
class Consumer implements Runnable {
    private final String name;
    private final KafkaBroker broker;
    private final String topicName;
    private volatile boolean running = true;

    /*
    Keeps track of which partitions this consumer owns
    Acts like a filter so consumer only polls its partitions
     */
    private final Set<Integer> assignedPartitions = ConcurrentHashMap.newKeySet();

    /*
    Maps partitionId → last consumed offset
    Tracks how far along the consumer is in each partition
    Even if you own a partition, you need to know where you left off
     */
    private final Map<Integer, Integer> partitionOffsets = new ConcurrentHashMap<>();

    public Consumer(String name, KafkaBroker broker, String topicName) {
        this.name = name;
        this.broker = broker;
        this.topicName = topicName;
        Topic topic = broker.getTopic(topicName);
        for (int i = 0; i < topic.getPartitionCount(); i++) {
            partitionOffsets.put(i, 0);
        }
    }


    public void assignPartition(int partitionId) {
        assignedPartitions.add(partitionId);
        partitionOffsets.putIfAbsent(partitionId, 0);
    }

    public void forceResetOffset(OffsetResetStrategy strategy, Thread consumerThread) {
        resetOffset(strategy);
        consumerThread.interrupt();  // Wake the thread if blocked
    }


    public void resetOffset(OffsetResetStrategy strategy) {
        Topic topic = broker.getTopic(topicName);
        for (int pid : assignedPartitions) {
            int offset = strategy == OffsetResetStrategy.EARLIEST ? 0 : topic.getPartition(pid).size();
            partitionOffsets.put(pid, offset);
        }
    }

    public void stop() {
        running = false;
    }

    /**
     * Kafka-style polling loop.
     * This simulates KafkaConsumer.poll():
     * - Runs infinitely until stopped via stop()
     * - Polls assigned partitions only
     * - Waits for new messages using blocking reads
     */
    @Override
    public void run() {
        Topic topic = broker.getTopic(topicName);
        while (running) {
            for (int pid : assignedPartitions) {
                int offset = partitionOffsets.get(pid);
                Message msg = topic.getPartition(pid).readBlocking(offset);
                if (msg != null) {
                    System.out.println("[" + name + "] Partition-" + pid + " Message: " + msg.getValue());
                    partitionOffsets.put(pid, offset + 1);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public Set<Integer> getAssignedPartitions() {
        return assignedPartitions;
    }

    public void clearAssignments() {
        assignedPartitions.clear();
    }
}

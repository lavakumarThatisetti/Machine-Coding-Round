package com.lavakumar.kafka.kafka_design_with_parittions;

import com.lavakumar.kafka.OffsetResetStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Consumer class
class Consumer implements Runnable {
    private final String name;
    private final KafkaBroker broker;
    private final String topicName;
    private volatile boolean running = true;

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

    public void forceResetOffset(OffsetResetStrategy strategy, Thread consumerThread) {
        resetOffset(strategy);
        consumerThread.interrupt();  // Wake the thread if blocked
    }

    public void resetOffset(OffsetResetStrategy strategy) {
        Topic topic = broker.getTopic(topicName);
        for (int i = 0; i < topic.getPartitionCount(); i++) {
            int offset = strategy == OffsetResetStrategy.EARLIEST ? 0 : topic.getPartition(i).size();
            partitionOffsets.put(i, offset);
        }
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        Topic topic = broker.getTopic(topicName);
        while (running) {
            for (int i = 0; i < topic.getPartitionCount(); i++) {
                int offset = partitionOffsets.get(i);
                Message msg = topic.getPartition(i).readBlocking(offset);
                if (msg != null) {
                    System.out.println("[" + name + "] Received from partition-" + i + ": " + msg.getValue());
                    partitionOffsets.put(i, offset + 1);
                }
            }
        }
    }
}

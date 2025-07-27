package com.lavakumar.kafka.kafka_design_with_parittions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

// Topic class
class Topic {
    private final String name;
    private final List<Partition> partitions;
    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);

    public Topic(String name, int partitionCount) {
        this.name = name;
        this.partitions = new ArrayList<>();
        for (int i = 0; i < partitionCount; i++) {
            partitions.add(new Partition());
        }
    }

    public void publish(Message message) {
        int index = roundRobinIndex.getAndUpdate(i -> (i + 1) % partitions.size());
        partitions.get(index).publish(message);
    }

    public Partition getPartition(int index) {
        return partitions.get(index);
    }

    public int getPartitionCount() {
        return partitions.size();
    }

    public String getName() {
        return name;
    }
}

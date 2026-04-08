package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Topic {
    private final String name;
    private final List<Partition> partitions;
    // Because multiple producers may publish concurrently.
    // This gives thread-safe round-robin partition routing without locking the whole topic.
    private final AtomicInteger nextPartitionIndex;

    public Topic(String name, int partitionCount) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("topic name cannot be blank");
        }
        if (partitionCount <= 0) {
            throw new IllegalArgumentException("partitionCount must be greater than 0");
        }

        this.name = name;
        this.partitions = new ArrayList<>(partitionCount);
        this.nextPartitionIndex = new AtomicInteger(0);

        for (int partitionId = 0; partitionId < partitionCount; partitionId++) {
            partitions.add(new Partition(name, partitionId));
        }
    }

    public MessageRecord publish(String payload) {
        Objects.requireNonNull(payload, "payload cannot be null");

        int partitionIndex = nextPartitionIndex.getAndUpdate(
                current -> (current + 1) % partitions.size()
        );

        Partition partition = partitions.get(partitionIndex);
        return partition.append(payload);
    }

    public Partition getPartition(int partitionId) {
        if (partitionId < 0 || partitionId >= partitions.size()) {
            throw new IllegalArgumentException("Invalid partitionId: " + partitionId);
        }
        return partitions.get(partitionId);
    }

    public int getPartitionCount() {
        return partitions.size();
    }

    public String getName() {
        return name;
    }
}
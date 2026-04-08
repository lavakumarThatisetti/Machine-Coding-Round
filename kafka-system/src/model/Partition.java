package model;

import java.util.ArrayList;
import java.util.List;

public class Partition {
    private final String topicName;
    private final int partitionId;
    private final List<MessageRecord> records;

    public Partition(String topicName, int partitionId) {
        this.topicName = topicName;
        this.partitionId = partitionId;
        // CopyOnWriteArrayList is bad when appends are frequent.
        // Simple ArrayList + synchronized is more appropriate here for interview scope.
        this.records = new ArrayList<>();
    }

    public synchronized MessageRecord append(String payload) {
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("payload cannot be blank");
        }

        long offset = records.size();

        MessageRecord record = new MessageRecord(
                topicName,
                partitionId,
                offset,
                payload,
                System.currentTimeMillis()
        );

        records.add(record);
        return record;
    }

    public synchronized MessageRecord read(long offset) {
        if (offset < 0 || offset >= records.size()) {
            return null;
        }
        return records.get((int) offset);
    }

    public synchronized int size() {
        return records.size();
    }

    public String getTopicName() {
        return topicName;
    }

    public int getPartitionId() {
        return partitionId;
    }
}
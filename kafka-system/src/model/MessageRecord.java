package model;

import java.util.Objects;

public final class MessageRecord {
    private final String topicName;
    private final int partitionId;
    private final long offset;
    private final String payload;
    private final long timestampMillis;

    public MessageRecord(String topicName,
                         int partitionId,
                         long offset,
                         String payload,
                         long timestampMillis) {
        this.topicName = Objects.requireNonNull(topicName, "topicName cannot be null");
        this.partitionId = partitionId;
        this.offset = offset;
        this.payload = Objects.requireNonNull(payload, "payload cannot be null");
        this.timestampMillis = timestampMillis;
    }

    public String getTopicName() {
        return topicName;
    }

    public int getPartitionId() {
        return partitionId;
    }

    public long getOffset() {
        return offset;
    }

    public String getPayload() {
        return payload;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    @Override
    public String toString() {
        return "MessageRecord{" +
                "topicName='" + topicName + '\'' +
                ", partitionId=" + partitionId +
                ", offset=" + offset +
                ", payload='" + payload + '\'' +
                ", timestampMillis=" + timestampMillis +
                '}';
    }
}
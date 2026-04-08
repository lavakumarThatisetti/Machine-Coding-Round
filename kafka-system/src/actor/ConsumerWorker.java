package actor;

import coordination.ConsumerMember;
import coordination.KafkaBroker;
import model.MessageRecord;
import model.Partition;
import model.Topic;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerWorker implements Runnable, ConsumerMember {
    private final String consumerId;
    private final String groupId;
    private final String topicName;
    private final KafkaBroker broker;
    private final RecordHandler recordHandler;
    private final long idleWaitMillis;

    private final Map<Integer, Long> partitionOffsets;
    private volatile Set<Integer> assignedPartitions;
    private volatile boolean running;

    public ConsumerWorker(String consumerId,
                          String groupId,
                          String topicName,
                          KafkaBroker broker,
                          RecordHandler recordHandler,
                          long idleWaitMillis) {
        if (consumerId == null || consumerId.isBlank()) {
            throw new IllegalArgumentException("consumerId cannot be blank");
        }
        if (groupId == null || groupId.isBlank()) {
            throw new IllegalArgumentException("groupId cannot be blank");
        }
        if (topicName == null || topicName.isBlank()) {
            throw new IllegalArgumentException("topicName cannot be blank");
        }
        if (idleWaitMillis < 0) {
            throw new IllegalArgumentException("idleWaitMillis cannot be negative");
        }

        this.consumerId = consumerId;
        this.groupId = groupId;
        this.topicName = topicName;
        this.broker = Objects.requireNonNull(broker, "broker cannot be null");
        this.recordHandler = Objects.requireNonNull(recordHandler, "recordHandler cannot be null");
        this.idleWaitMillis = idleWaitMillis;

        this.partitionOffsets = new ConcurrentHashMap<>();
        this.assignedPartitions = Set.of();
        this.running = true;
    }

    @Override
    public void run() {
        Topic topic = broker.getTopic(topicName);

        while (running && !Thread.currentThread().isInterrupted()) {
            boolean madeProgress = false;
            Set<Integer> assignmentSnapshot = assignedPartitions;

            for (int partitionId : assignmentSnapshot) {
                long nextOffset = partitionOffsets.getOrDefault(partitionId, 0L);
                Partition partition = topic.getPartition(partitionId);

                MessageRecord record = partition.read(nextOffset);
                if (record == null) {
                    continue;
                }

                try {
                    recordHandler.handle(record);
                    partitionOffsets.put(partitionId, nextOffset + 1);
                    madeProgress = true;
                } catch (Exception ex) {
                    System.out.printf(
                            "[Consumer=%s] Failed to process topic=%s partition=%d offset=%d error=%s%n",
                            consumerId,
                            record.getTopicName(),
                            record.getPartitionId(),
                            record.getOffset(),
                            ex.getMessage()
                    );
                }
            }

            if (!madeProgress) {
                sleepQuietly(idleWaitMillis);
            }
        }

        System.out.printf("[Consumer=%s] Stopped%n", consumerId);
    }

    @Override
    public String getConsumerId() {
        return consumerId;
    }

    @Override
    public void replaceAssignments(Set<Integer> newPartitions) {
        Objects.requireNonNull(newPartitions, "newPartitions cannot be null");
        this.assignedPartitions = Set.copyOf(newPartitions);

        for (Integer partitionId : newPartitions) {
            partitionOffsets.putIfAbsent(partitionId, 0L);
        }

        System.out.printf(
                "[Consumer=%s] New assignments=%s%n",
                consumerId,
                this.assignedPartitions
        );
    }

    public void resetOffsets(OffsetResetStrategy strategy) {
        Topic topic = broker.getTopic(topicName);

        for (int partitionId : assignedPartitions) {
            long offset = strategy == OffsetResetStrategy.EARLIEST
                    ? 0L
                    : topic.getPartition(partitionId).size();

            partitionOffsets.put(partitionId, offset);
        }

        System.out.printf(
                "[Consumer=%s] Offsets reset using strategy=%s for partitions=%s%n",
                consumerId,
                strategy,
                assignedPartitions
        );
    }

    public void stop() {
        this.running = false;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getTopicName() {
        return topicName;
    }

    public Set<Integer> getAssignedPartitions() {
        return assignedPartitions;
    }

    public Map<Integer, Long> getPartitionOffsetsSnapshot() {
        return Map.copyOf(partitionOffsets);
    }

    private void sleepQuietly(long millis) {
        if (millis <= 0) {
            return;
        }

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
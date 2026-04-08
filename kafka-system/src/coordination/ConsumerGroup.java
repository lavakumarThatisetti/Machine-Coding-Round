package coordination;

import model.Topic;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConsumerGroup {
    private final String groupId;
    private final Topic topic;
    private final CopyOnWriteArrayList<ConsumerMember> members;

    public ConsumerGroup(String groupId, Topic topic) {
        if (groupId == null || groupId.isBlank()) {
            throw new IllegalArgumentException("groupId cannot be blank");
        }

        this.groupId = groupId;
        this.topic = Objects.requireNonNull(topic, "topic cannot be null");
        this.members = new CopyOnWriteArrayList<>();
    }

    public void registerMember(ConsumerMember member) {
        Objects.requireNonNull(member, "member cannot be null");
        members.addIfAbsent(member);
        rebalance();
    }

    public void unregisterMember(String consumerId) {
        if (consumerId == null || consumerId.isBlank()) {
            throw new IllegalArgumentException("consumerId cannot be blank");
        }

        members.removeIf(member -> member.getConsumerId().equals(consumerId));
        rebalance();
    }

    // Why is rebalance() synchronized? -> Because rebalance should be treated as one coordinated operation.
    /**
     * Recomputes partition ownership for all active consumers in this group.
     *
     * Why rebalance is needed:
     * - A consumer group shares the partitions of one topic among its members.
     * - Each partition must be assigned to exactly one consumer in the group.
     * - One consumer may own multiple partitions.
     * - Whenever membership changes (consumer joins / leaves), ownership must be recalculated.
     *
     * What this method does:
     * 1. Takes a snapshot of current group members.
     * 2. Builds a fresh assignment map in memory:
     *      consumerId -> set of partitionIds
     * 3. Distributes topic partitions across consumers using round-robin:
     *      partitionId % memberCount
     * 4. Replaces each consumer's assignments atomically using replaceAssignments(...)
     *
     * Why compute full assignment first:
     * - Avoids partially updated state during rebalance.
     * - A consumer should see either the old full assignment or the new full assignment,
     *   not a temporary half-cleared / half-reassigned state.
     *
     * Why replaceAssignments(...) is used:
     * - Better than:
     *      clearAssignments()
     *      assignPartition(...)
     *      assignPartition(...)
     * - Snapshot replacement is safer and cleaner for concurrent readers.
     *
     * Example:
     * - Partitions: P0, P1, P2, P3
     * - Consumers: C1, C2
     * - Assignment result:
     *      C1 -> {0, 2}
     *      C2 -> {1, 3}
     *
     * Thread-safety:
     * - Synchronized so that two rebalance operations do not interleave.
     * - Prevents inconsistent assignment updates when multiple membership changes happen close together.
     *
     * Current limitation:
     * - This is a simple full rebalance strategy.
     * - It does not try to preserve previous ownership.
     * - Real Kafka uses more advanced assignment strategies to reduce movement.
     */
    public synchronized void rebalance() {
        List<ConsumerMember> currentMembers = new ArrayList<>(members);

        if (currentMembers.isEmpty()) {
            return;
        }

        Map<String, Set<Integer>> newAssignments = new HashMap<>();
        for (ConsumerMember member : currentMembers) {
            newAssignments.put(member.getConsumerId(), new HashSet<>());
        }

        int partitionCount = topic.getPartitionCount();
        for (int partitionId = 0; partitionId < partitionCount; partitionId++) {
            ConsumerMember member = currentMembers.get(partitionId % currentMembers.size());
            newAssignments.get(member.getConsumerId()).add(partitionId);
        }

        System.out.println("\n--- Rebalancing Group: " + groupId + " Topic: " + topic.getName() + " ---");
        for (ConsumerMember member : currentMembers) {
            Set<Integer> assignedPartitions = Collections.unmodifiableSet(newAssignments.get(member.getConsumerId()));
            member.replaceAssignments(assignedPartitions);
            System.out.println("Consumer [" + member.getConsumerId() + "] assigned partitions " + assignedPartitions);
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public Topic getTopic() {
        return topic;
    }

    public List<ConsumerMember> getMembers() {
        return List.copyOf(members);
    }
}

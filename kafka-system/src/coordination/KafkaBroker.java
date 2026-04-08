package coordination;

import model.MessageRecord;
import model.Topic;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class KafkaBroker {
    private final Map<String, Topic> topics;
    private final Map<String, ConsumerGroup> consumerGroups;

    public KafkaBroker() {
        this.topics = new ConcurrentHashMap<>();
        this.consumerGroups = new ConcurrentHashMap<>();
    }

    public void createTopic(String topicName, int partitionCount) {
        Topic topic = new Topic(topicName, partitionCount);
        Topic existing = topics.putIfAbsent(topicName, topic);

        if (existing != null) {
            throw new IllegalArgumentException("Topic already exists: " + topicName);
        }
    }

    public Topic getTopic(String topicName) {
        Topic topic = topics.get(topicName);
        if (topic == null) {
            throw new IllegalArgumentException("Topic not found: " + topicName);
        }
        return topic;
    }

    public MessageRecord publish(String topicName, String payload) {
        Topic topic = getTopic(topicName);
        return topic.publish(payload);
    }

    public ConsumerGroup createConsumerGroup(String groupId, String topicName) {
        Objects.requireNonNull(groupId, "groupId cannot be null");
        Objects.requireNonNull(topicName, "topicName cannot be null");

        Topic topic = getTopic(topicName);
        ConsumerGroup group = new ConsumerGroup(groupId, topic);

        ConsumerGroup existing = consumerGroups.putIfAbsent(groupId, group);
        if (existing != null) {
            throw new IllegalArgumentException("Consumer group already exists: " + groupId);
        }

        return group;
    }

    public ConsumerGroup getConsumerGroup(String groupId) {
        ConsumerGroup group = consumerGroups.get(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Consumer group not found: " + groupId);
        }
        return group;
    }

    public void registerConsumer(String groupId, ConsumerMember member) {
        Objects.requireNonNull(member, "member cannot be null");
        ConsumerGroup group = getConsumerGroup(groupId);
        group.registerMember(member);
    }

    public void unregisterConsumer(String groupId, String consumerId) {
        ConsumerGroup group = getConsumerGroup(groupId);
        group.unregisterMember(consumerId);
    }
}

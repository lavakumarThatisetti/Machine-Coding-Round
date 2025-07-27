package com.lavakumar.kafka.kafka_design_with_parittions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// KafkaBroker class
class KafkaBroker {
    private final Map<String, Topic> topics = new ConcurrentHashMap<>();

    public void createTopic(String topicName, int partitions) {
        topics.putIfAbsent(topicName, new Topic(topicName, partitions));
    }

    public void publish(String topicName, String value) {
        Topic topic = topics.get(topicName);
        if (topic != null) {
            topic.publish(new Message(value));
        }
    }

    public Topic getTopic(String topicName) {
        return topics.get(topicName);
    }
}


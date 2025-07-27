package com.lavakumar.kafka.simple_kafka_design;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// KafkaBroker class
class KafkaBroker {
    private final Map<String, Topic> topics = new ConcurrentHashMap<>();

    public void createTopic(String topicName) {
        topics.putIfAbsent(topicName, new Topic(topicName));
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


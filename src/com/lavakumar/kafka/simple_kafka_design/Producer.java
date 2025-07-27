package com.lavakumar.kafka.simple_kafka_design;

// Producer class
class Producer {
    private final KafkaBroker broker;

    public Producer(KafkaBroker broker) {
        this.broker = broker;
    }

    public void send(String topicName, String value) {
        broker.publish(topicName, value);
    }
}

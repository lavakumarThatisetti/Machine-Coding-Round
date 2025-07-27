package com.lavakumar.kafka.kafka_design_with_parittions;

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

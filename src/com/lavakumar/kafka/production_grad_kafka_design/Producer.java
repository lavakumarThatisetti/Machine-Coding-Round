package com.lavakumar.kafka.production_grad_kafka_design;

// Producer class
class Producer {
    private final KafkaCluster kafkaCluster;

    public Producer(KafkaCluster kafkaCluster) {
        this.kafkaCluster = kafkaCluster;
    }

    public void send(String topicName, String value) {
        kafkaCluster.publish(topicName, value);
    }
}

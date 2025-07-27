package com.lavakumar.kafka.kafka_design_with_parittions;

class Message {
    private final String value;
    private final long timestamp;

    public Message(String value) {
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }

    public String getValue() { return value; }
    public long getTimestamp() { return timestamp; }
}

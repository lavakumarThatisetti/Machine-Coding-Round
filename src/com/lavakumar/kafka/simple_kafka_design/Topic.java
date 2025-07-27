package com.lavakumar.kafka.simple_kafka_design;


import java.util.ArrayList;
import java.util.List;

// Topic class
class Topic {
    private final String name;
    private final List<Message> messages = new ArrayList<>();

    public Topic(String name) {
        this.name = name;
    }

    public synchronized void publish(Message message) {
        messages.add(message);
        notifyAll();
    }

    public synchronized Message readBlocking(int offset) {
        while (offset >= messages.size()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return messages.get(offset);
    }

    public synchronized int size() {
        return messages.size();
    }

    public String getName() {
        return name;
    }
}

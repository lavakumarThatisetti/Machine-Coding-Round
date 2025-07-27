package com.lavakumar.kafka.kafka_design_with_parittions;

import java.util.ArrayList;
import java.util.List;

// Partition class
class Partition {
    private final List<Message> messages = new ArrayList<>();

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
}
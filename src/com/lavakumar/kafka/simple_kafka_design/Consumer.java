package com.lavakumar.kafka.simple_kafka_design;

import com.lavakumar.kafka.OffsetResetStrategy;

import java.util.concurrent.atomic.AtomicInteger;

// Consumer class
class Consumer implements Runnable {
    private final String name;
    private final KafkaBroker broker;
    private final String topicName;
    private volatile boolean running = true;

    private final AtomicInteger offset =  new AtomicInteger(0);

    public Consumer(String name, KafkaBroker broker, String topicName) {
        this.name = name;
        this.broker = broker;
        this.topicName = topicName;
    }

    public void stop() {
        running = false;
    }

    public void forceResetOffset(OffsetResetStrategy strategy, Thread consumerThread) {
        resetOffset(strategy);
        consumerThread.interrupt();  // Wake the thread if blocked
    }

    private void resetOffset(OffsetResetStrategy strategy) {
        Topic topic = broker.getTopic(topicName);
        int offsetToSet = strategy == OffsetResetStrategy.EARLIEST ? 0 : topic.size();
        offset.set(offsetToSet);
    }

    @Override
    public void run() {
        Topic topic = broker.getTopic(topicName);
        while (running) {
            int offsetValue = offset.get();
            Message msg = topic.readBlocking(offsetValue);
            if (msg != null) {
                System.out.println("[" + name + "] Received  Message - " + msg.getValue());
                offset.compareAndSet(offsetValue,offsetValue + 1);
            }
        }
    }
}

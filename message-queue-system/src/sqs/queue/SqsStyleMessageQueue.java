package sqs.queue;

import sqs.model.Delivery;
import sqs.model.Message;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SqsStyleMessageQueue implements AckMessageQueue {
    private final Deque<MessageEnvelope> visibleQueue;
    private final Map<String, InFlightMessage> inFlightMessages;
    private final long visibilityTimeoutMillis;

    private final ReentrantLock lock;
    private final Condition messageAvailable;

    private volatile boolean shutdown;

    public SqsStyleMessageQueue(long visibilityTimeoutMillis) {
        if (visibilityTimeoutMillis <= 0) {
            throw new IllegalArgumentException("visibilityTimeoutMillis must be greater than 0");
        }

        this.visibleQueue = new ArrayDeque<>();
        this.inFlightMessages = new HashMap<>();
        this.visibilityTimeoutMillis = visibilityTimeoutMillis;
        this.lock = new ReentrantLock();
        this.messageAvailable = lock.newCondition();
        this.shutdown = false;
    }

    @Override
    public void publish(Message message) throws InterruptedException {
        Objects.requireNonNull(message, "message cannot be null");

        lock.lockInterruptibly();
        try {
            if (shutdown) {
                throw new IllegalStateException("Queue is shut down. Cannot publish new messages.");
            }

            requeueExpiredInFlightMessages();

            visibleQueue.addLast(new MessageEnvelope(message));
            messageAvailable.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Delivery receive() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (true) {
                requeueExpiredInFlightMessages();

                if (!visibleQueue.isEmpty()) {
                    MessageEnvelope envelope = visibleQueue.removeFirst();
                    envelope.incrementReceiveCount();

                    String receiptHandle = UUID.randomUUID().toString();
                    long visibilityDeadline = System.currentTimeMillis() + visibilityTimeoutMillis;

                    InFlightMessage inFlightMessage =
                            new InFlightMessage(receiptHandle, envelope, visibilityDeadline);

                    inFlightMessages.put(receiptHandle, inFlightMessage);

                    return new Delivery(
                            receiptHandle,
                            envelope.getMessage(),
                            envelope.getReceiveCount()
                    );
                }

                if (shutdown) {
                    return null;
                }

                messageAvailable.await();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void ack(String receiptHandle) {
        Objects.requireNonNull(receiptHandle, "receiptHandle cannot be null");

        lock.lock();
        try {
            requeueExpiredInFlightMessages();
            inFlightMessages.remove(receiptHandle);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void nack(String receiptHandle) {
        Objects.requireNonNull(receiptHandle, "receiptHandle cannot be null");

        lock.lock();
        try {
            requeueExpiredInFlightMessages();

            InFlightMessage inFlightMessage = inFlightMessages.remove(receiptHandle);
            if (inFlightMessage != null) {
                visibleQueue.addLast(inFlightMessage.getEnvelope());
                messageAvailable.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int visibleSize() {
        lock.lock();
        try {
            requeueExpiredInFlightMessages();
            return visibleQueue.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int inFlightSize() {
        lock.lock();
        try {
            requeueExpiredInFlightMessages();
            return inFlightMessages.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shutdown() {
        lock.lock();
        try {
            shutdown = true;
            messageAvailable.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void requeueExpiredInFlightMessages() {
        long nowMillis = System.currentTimeMillis();

        Iterator<Map.Entry<String, InFlightMessage>> iterator = inFlightMessages.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, InFlightMessage> entry = iterator.next();
            InFlightMessage inFlightMessage = entry.getValue();

            if (inFlightMessage.isExpired(nowMillis)) {
                visibleQueue.addLast(inFlightMessage.getEnvelope());
                iterator.remove();
            }
        }

        if (!visibleQueue.isEmpty()) {
            messageAvailable.signalAll();
        }
    }
}
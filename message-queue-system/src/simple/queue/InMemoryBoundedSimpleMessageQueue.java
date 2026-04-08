package simple.queue;

import simple.model.Message;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryBoundedSimpleMessageQueue implements SimpleMessageQueue {
    private final Deque<Message> buffer;
    private final int capacity;

    private final ReentrantLock lock;
    private final Condition spaceAvailable;
    private final Condition messageAvailable;

    private volatile boolean shutdown;

    public InMemoryBoundedSimpleMessageQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than 0");
        }

        this.capacity = capacity;
        this.buffer = new ArrayDeque<>(capacity);
        this.lock = new ReentrantLock();
        this.spaceAvailable = lock.newCondition();
        this.messageAvailable = lock.newCondition();
        this.shutdown = false;
    }

    @Override
    public void publish(Message message) throws InterruptedException {
        Objects.requireNonNull(message, "message cannot be null");

        lock.lockInterruptibly();
        try {
            while (buffer.size() == capacity && !shutdown) {
                spaceAvailable.await();
            }

            if (shutdown) {
                throw new IllegalStateException("Queue is shut down. Cannot publish new messages.");
            }

            buffer.addLast(message);
            messageAvailable.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Message consume() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (buffer.isEmpty() && !shutdown) {
                messageAvailable.await();
            }

            if (buffer.isEmpty() && shutdown) {
                return null;
            }

            Message message = buffer.removeFirst();
            spaceAvailable.signal();
            return message;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return buffer.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shutdown() {
        lock.lock();
        try {
            shutdown = true;
            spaceAvailable.signalAll();
            messageAvailable.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
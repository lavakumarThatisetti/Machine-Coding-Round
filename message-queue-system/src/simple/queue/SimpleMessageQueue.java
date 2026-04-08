package simple.queue;

import simple.model.Message;

public interface SimpleMessageQueue {
    void publish(Message message) throws InterruptedException;

    Message consume() throws InterruptedException;

    int size();

    void shutdown();
}

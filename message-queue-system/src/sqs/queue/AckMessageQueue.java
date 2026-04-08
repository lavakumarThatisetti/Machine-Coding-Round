package sqs.queue;

import sqs.model.Delivery;
import sqs.model.Message;

public interface AckMessageQueue {
    void publish(Message message) throws InterruptedException;

    Delivery receive() throws InterruptedException;

    void ack(String receiptHandle);

    void nack(String receiptHandle);

    int visibleSize();

    int inFlightSize();

    void shutdown();
}
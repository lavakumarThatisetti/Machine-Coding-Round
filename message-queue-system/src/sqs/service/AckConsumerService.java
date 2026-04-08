package sqs.service;


import sqs.model.Delivery;
import sqs.model.Message;
import sqs.queue.AckMessageQueue;

import java.util.Objects;

public class AckConsumerService {
    private final AckMessageQueue messageQueue;

    public AckConsumerService(AckMessageQueue messageQueue) {
        this.messageQueue = Objects.requireNonNull(messageQueue, "messageQueue cannot be null");
    }

    public Delivery receive() throws InterruptedException {
        return messageQueue.receive();
    }

    public void ack(String receiptHandle) {
        messageQueue.ack(receiptHandle);
    }

    public void nack(String receiptHandle) {
        messageQueue.nack(receiptHandle);
    }

    public void consumeOnce(String consumerId, MessageHandler handler) throws InterruptedException {
        Objects.requireNonNull(consumerId, "consumerId cannot be null");
        Objects.requireNonNull(handler, "handler cannot be null");

        Delivery delivery = messageQueue.receive();
        if (delivery == null) {
            return;
        }

        try {
            handler.handle(delivery.getMessage());
            ack(delivery.getReceiptHandle());

            System.out.printf(
                    "[Consumer=%s] ACK messageId=%s receiveCount=%d%n",
                    consumerId,
                    delivery.getMessage().getMessageId(),
                    delivery.getReceiveCount()
            );
        } catch (Exception ex) {
            nack(delivery.getReceiptHandle());

            System.out.printf(
                    "[Consumer=%s] NACK messageId=%s receiveCount=%d reason=%s%n",
                    consumerId,
                    delivery.getMessage().getMessageId(),
                    delivery.getReceiveCount(),
                    ex.getMessage()
            );
        }
    }
}
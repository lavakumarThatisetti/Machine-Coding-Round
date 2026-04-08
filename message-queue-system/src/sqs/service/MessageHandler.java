package sqs.service;

import sqs.model.Message;

@FunctionalInterface
public interface MessageHandler {
    void handle(Message message) throws Exception;
}

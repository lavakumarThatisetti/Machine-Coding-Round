package actor;

import model.MessageRecord;

@FunctionalInterface
public interface RecordHandler {
    void handle(MessageRecord record) throws Exception;
}
package actor;

import coordination.KafkaBroker;
import model.MessageRecord;

import java.util.Objects;

public class Producer {
    private final KafkaBroker broker;

    public Producer(KafkaBroker broker) {
        this.broker = Objects.requireNonNull(broker, "broker cannot be null");
    }

    public MessageRecord send(String topicName, String payload) {
        if (topicName == null || topicName.isBlank()) {
            throw new IllegalArgumentException("topicName cannot be blank");
        }
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("payload cannot be blank");
        }

        return broker.publish(topicName, payload);
    }
}
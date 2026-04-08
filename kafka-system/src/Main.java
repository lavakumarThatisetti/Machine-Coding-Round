
import actor.ConsumerWorker;
import actor.Producer;
import actor.RecordHandler;
import actor.OffsetResetStrategy;
import coordination.ConsumerGroup;
import coordination.KafkaBroker;
import model.MessageRecord;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {
        Main simulation = new Main();
        simulation.runKafkaStylePubSubDemo();
    }

    private void runKafkaStylePubSubDemo() throws Exception {
        System.out.println("========================================");
        System.out.println("Kafka-Style Pub/Sub Simulation Started");
        System.out.println("========================================");

        KafkaBroker broker = new KafkaBroker();
        broker.createTopic("orders", 4);

        ConsumerGroup consumerGroup = broker.createConsumerGroup("order-processing-group", "orders");
        Producer producer = new Producer(broker);

        RecordHandler recordHandler = this::handleRecord;

        ConsumerWorker consumer1 = new ConsumerWorker(
                "consumer-1",
                "order-processing-group",
                "orders",
                broker,
                recordHandler,
                100
        );

        ConsumerWorker consumer2 = new ConsumerWorker(
                "consumer-2",
                "order-processing-group",
                "orders",
                broker,
                recordHandler,
                100
        );

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        broker.registerConsumer(consumerGroup.getGroupId(), consumer1);
        broker.registerConsumer(consumerGroup.getGroupId(), consumer2);

        executorService.submit(consumer1);
        executorService.submit(consumer2);

        System.out.println("\n--- Publishing first batch of messages ---");
        publishMessages(producer, "orders", 10, "Order");

        Thread.sleep(3000);

        System.out.println("\n--- Adding consumer-3, which triggers rebalance ---");
        ConsumerWorker consumer3 = new ConsumerWorker(
                "consumer-3",
                "order-processing-group",
                "orders",
                broker,
                recordHandler,
                100
        );

        broker.registerConsumer(consumerGroup.getGroupId(), consumer3);
        executorService.submit(consumer3);

        Thread.sleep(1000);

        System.out.println("\n--- Publishing second batch of messages after rebalance ---");
        publishMessages(producer, "orders", 10, "Rebalanced-Order");

        Thread.sleep(3000);

        System.out.println("\n--- Resetting offsets of consumer-1 to EARLIEST ---");
        consumer1.resetOffsets(OffsetResetStrategy.EARLIEST);

        Thread.sleep(3000);

        System.out.println("\n--- Publishing final batch of messages ---");
        publishMessages(producer, "orders", 5, "Final-Order");

        Thread.sleep(3000);

        System.out.println("\n--- Current Consumer Offset Snapshots ---");
        printConsumerOffsets(consumer1);
        printConsumerOffsets(consumer2);
        printConsumerOffsets(consumer3);

        System.out.println("\n--- Shutting down consumers ---");
        shutdownConsumer(broker, consumerGroup.getGroupId(), consumer1);
        shutdownConsumer(broker, consumerGroup.getGroupId(), consumer2);
        shutdownConsumer(broker, consumerGroup.getGroupId(), consumer3);

        executorService.shutdownNow();
        boolean terminated = executorService.awaitTermination(5, TimeUnit.SECONDS);

        if (!terminated) {
            throw new IllegalStateException("Executor did not terminate cleanly");
        }

        System.out.println("========================================");
        System.out.println("Kafka-Style Pub/Sub Simulation Completed");
        System.out.println("========================================");
    }

    private void publishMessages(Producer producer, String topicName, int count, String prefix) throws InterruptedException {
        for (int i = 1; i <= count; i++) {
            String payload = prefix + "-" + i;
            MessageRecord record = producer.send(topicName, payload);

            System.out.printf(
                    "[Producer] Published topic=%s partition=%d offset=%d payload=%s%n",
                    record.getTopicName(),
                    record.getPartitionId(),
                    record.getOffset(),
                    record.getPayload()
            );

            Thread.sleep(150);
        }
    }

    private void handleRecord(MessageRecord record) {
        System.out.printf(
                "[Handler] Consumed topic=%s partition=%d offset=%d payload=%s%n",
                record.getTopicName(),
                record.getPartitionId(),
                record.getOffset(),
                record.getPayload()
        );
    }

    private void printConsumerOffsets(ConsumerWorker consumerWorker) {
        System.out.printf(
                "Consumer=%s assignedPartitions=%s offsets=%s%n",
                consumerWorker.getConsumerId(),
                consumerWorker.getAssignedPartitions(),
                consumerWorker.getPartitionOffsetsSnapshot()
        );
    }

    private void shutdownConsumer(KafkaBroker broker, String groupId, ConsumerWorker consumerWorker) {
        broker.unregisterConsumer(groupId, consumerWorker.getConsumerId());
        consumerWorker.stop();
    }
}
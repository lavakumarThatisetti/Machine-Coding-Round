Kafka Design Notes (LLD Perspective)

🎯 Is KafkaBroker a Queue?

No — KafkaBroker is not a queue. Instead, it acts as a repository of many queues.

🧱 Core Components Explained

1. KafkaBroker

Acts as the central hub.

Manages multiple topics.

Each topic contains multiple partitions.

Responsible for routing messages and notifying subscribers.

2. Topic

A logical name that groups messages.

Each topic is divided into partitions.

Consumers subscribe to topics, and topics manage a list of subscribers.

3. Partition

The true queue-like component.

An append-only structure that stores messages in order.

Producers publish messages to partitions (e.g., via round-robin or hash).

Consumers read from specific offsets in partitions.

4. Producer

Sends messages to topics (via broker).

Uses partitioning logic to distribute messages.

In this LLD, producers are encapsulated via a Producer class.

5. Consumer

Subscribes to topics.

Maintains offset tracking per partition.

Polls data from partitions and processes it.

🔄 High-Level Data Flow

Producer → KafkaBroker → Topic → Partition (Queue) → Consumer (via Offset)

Producers send messages via KafkaBroker.

Broker routes them to a topic.

Topic selects a partition to write to.

Each Partition is a FIFO queue.

Consumers subscribe to topics and read from partitions using offsets.

🧠 Final Note

KafkaBroker is not a queue, but a controller of queues.

Partitions are the actual queues.
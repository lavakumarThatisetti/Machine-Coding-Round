# Kafka Production-Grade Low-Level Design (LLD) in Java

This project simulates the internal architecture of a **Kafka-like distributed messaging system** with real-world production concepts implemented in pure Java.

---

## ⚡ Features

✅ Multi-Broker KafkaCluster with global partition awareness  
✅ Partitioned Topics distributed evenly across brokers  
✅ Consumer Groups with dynamic rebalancing and fair partition assignment  
✅ Offset tracking per partition, with support for EARLIEST / LATEST reset  
✅ Producer with round-robin publishing to partitions  
✅ Thread-safe blocking reads from partitions  
✅ Console logs to visualize partition assignments, rebalancing, and message flow

---

## 🏗️ System Components

| Concept            | Implementation |
|-------------------|----------------|
| KafkaCluster      | Manages brokers, partition-to-broker mapping, and global coordination |
| KafkaBroker       | Owns only its assigned partitions and consumer metadata |
| Partition         | Thread-safe message storage with blocking read mechanism |
| ConsumerWithGroup | Participates in consumer groups, polls assigned partitions, tracks offsets |
| Producer          | Publishes messages to partitions via the cluster |
| Rebalancing       | Triggered on consumer joins, partitions reassigned dynamically |

---

## 📦 Architecture Diagram (Simplified)

Producer → KafkaCluster → Brokers → Partitions
Consumers ← Brokers ← KafkaCluster (for partition ownership & lookup)


---

## 🚀 How to Run

1. Compile all `.java` files
2. Execute the `Simulation` class
3. Observe the console for partition assignments, message flow, and rebalancing behavior



## 💡 Supported Kafka Behaviors

✔ Partitioned Topics with even distribution  
✔ Consumer Group participation and fair partition rebalancing  
✔ Message production and consumption across distributed brokers  
✔ Offset management per partition for each consumer  
✔ Reset offsets to EARLIEST to re-consume data  
✔ Fully thread-safe, blocking reads from partitions

---

## 🧪 Example Console Output

--- Start Publishing ---

[C1] Partition-0 Message: Txn-0

[C2] Partition-1 Message: Txn-1
...

--- Adding Consumer C3 to group-A, triggering rebalance ---

Partition-0 assigned to [C1]

Partition-1 assigned to [C2]

Partition-2 assigned to [C3]

Partition-3 assigned to [C1]
...

--- Resetting C1's offset to EARLIEST ---

[C1] Partition-0 Message: Txn-0

[C1] Partition-3 Message: Txn-3


## 🎯 Ideal For

✔ System Design Interviews  
✔ Kafka Architecture Learning  
✔ Java Multi-threading Practice  
✔ Real-world Distributed System Simulation  
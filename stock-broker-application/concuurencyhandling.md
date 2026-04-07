## how will you handle concurrency?


### Level 1

Use thread-safe repositories like ConcurrentHashMap.

### Level 2

Use per-stock lock for order matching so matching for one stock is serialized, but different stocks can match concurrently.

### Level 3

Use per-user lock for wallet and holdings updates to prevent double-spend and oversell.

### Level 4
Reserve resources before order enters order book:

1. buy → reserve funds
2. sell → reserve stocks

### Level 5
Use strict lock ordering to avoid deadlocks:
1. user reservation outside book lock
2. settlement locks users in sorted order
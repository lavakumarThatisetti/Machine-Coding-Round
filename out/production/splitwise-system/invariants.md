These are the key rules our later services will rely on:

### User
1. immutable
2. identified by id

### Group
1. contains members
2. tracks expense ids
3. only members can participate in group expenses

### Expense
1. immutable
2. always stores normalized Share amounts
3. may or may not belong to a group

### Settlement
1. immutable
2. separate from expense
3. reduces debt

### Ledger
1. later will store normalized debts using UserPair
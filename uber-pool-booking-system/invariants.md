## Core invariants in cab pooling

These are the real interview points.

### Ride-level invariants
1. occupied seats must never exceed capacity 
2. available seats must never go negative 
3. a new rider can join only if ride is open/matchable 
4. completed/cancelled ride cannot accept new riders

### Rider-level invariants
1. one rider should not hold two active pool bookings unless allowed 
2. rider booking belongs to exactly one pool ride

### Concurrency invariants
1. two riders racing for last seat → only one succeeds 
2. cancellation and join should not corrupt seat count
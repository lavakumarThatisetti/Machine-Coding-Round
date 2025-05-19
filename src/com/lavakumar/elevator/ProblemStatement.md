**Goal**: Design an elevator system that supports multiple elevators across multiple floors.

Each elevator should be able to:

1. Accept floor requests from inside the elevator
2. Respond to external up/down calls from floors
3. Move up or down one floor at a time
4. Open/close doors when it reaches a destination floor

The system should also:

1. Decide which elevator to assign for each external request
2. Optimize for minimum wait time and efficient movement
3. Handle requests even while elevators are in motion”

You’ll model it in object-oriented design, write fully working code,
and simulate how elevators behave under different inputs

**Constraints**

1. Number of elevators: 4
2. Number of floors: 10
3. Each elevator has a current floor, direction (UP/DOWN/IDLE), and a queue of requests
4. Requests can come in any order, at any time
5. Each elevator can only move one floor per tick (we’ll simulate time in discrete steps)
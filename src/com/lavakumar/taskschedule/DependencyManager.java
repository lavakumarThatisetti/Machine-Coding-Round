package com.lavakumar.taskschedule;

import com.interview.taskschedule.task.TaskDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DependencyManager {
    // TaskId -> Number of parents it is still waiting for
    private final ConcurrentHashMap<String, AtomicInteger> inDegreeMap = new ConcurrentHashMap<>();

    // ParentTaskId -> List of ChildTaskIds waiting on it
    private final ConcurrentHashMap<String, List<String>> dependentsMap = new ConcurrentHashMap<>();

    // TaskId -> The actual definition, parked here until ready
    private final ConcurrentHashMap<String, TaskDefinition> waitingTasks = new ConcurrentHashMap<>();

    /**
     * Called when a new task is submitted to the scheduler.
     * Returns true if the task is immediately ready to run, false if it's blocked.
     */
    public boolean registerTask(TaskDefinition definition) {
        List<String> dependsOn = definition.dependencies;

        // If no dependencies, it's ready immediately
        if (dependsOn == null || dependsOn.isEmpty()) {
            return true;
        }

        // Park the task in the waiting room
        waitingTasks.put(definition.id, definition);
        inDegreeMap.put(definition.id, new AtomicInteger(dependsOn.size()));

        // Build the adjacency list (edges from parent to this child)
        for (String parentId : dependsOn) {
            dependentsMap.computeIfAbsent(parentId, _ -> new CopyOnWriteArrayList<>()).add(definition.id);
        }

        return false; // Task is blocked, do not schedule yet
    }

    /**
     * Called by a worker thread when a task completes successfully.
     * Returns a list of tasks that have just been unblocked.
     */
    public List<TaskDefinition> markTaskComplete(String completedTaskId) {
        List<TaskDefinition> newlyReadyTasks = new ArrayList<>();

        // Find all children waiting on this completed task
        List<String> dependents = dependentsMap.get(completedTaskId);

        if (dependents != null) {
            for (String childId : dependents) {
                AtomicInteger inDegree = inDegreeMap.get(childId);

                // Atomically decrement the dependency count. If it hits 0, it's ready!
                if (inDegree != null && inDegree.decrementAndGet() == 0) {
                    TaskDefinition readyTask = waitingTasks.remove(childId);
                    if (readyTask != null) {
                        newlyReadyTasks.add(readyTask);
                    }
                    inDegreeMap.remove(childId);
                }
            }
        }

        // Clean up the completed task's footprint
        dependentsMap.remove(completedTaskId);

        return newlyReadyTasks;
    }

    /**
     * Called when a task exhausts all retries and permanently fails.
     * Recursively aborts all downstream dependent tasks.
     */
    public void markTaskFailed(String failedTaskId) {
        // 1. Get all immediate children waiting on this failed task
        List<String> dependents = dependentsMap.remove(failedTaskId);

        if (dependents != null) {
            for (String childId : dependents) {
                // 2. Remove the child from the waiting room and tracking maps
                TaskDefinition abortedTask = waitingTasks.remove(childId);
                inDegreeMap.remove(childId);

                if (abortedTask != null) {
                    System.out.println("CASCADING FAILURE: Aborting task " + childId +
                            " because upstream parent " + failedTaskId + " failed.");

                    // 3. Recursively fail this child's children
                    markTaskFailed(childId);
                }
            }
        }
    }
}

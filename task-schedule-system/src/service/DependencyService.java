package service;

import model.TaskDefinition;
import model.TaskRuntime;
import model.TaskStatus;
import repository.InMemoryTaskRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DependencyService {

    private final InMemoryTaskRepository taskRepository;

    // parentTaskId -> childTaskIds
    private final ConcurrentHashMap<String, Set<String>> reverseDependencies = new ConcurrentHashMap<>();

    // taskId -> remaining unmet dependency count
    private final ConcurrentHashMap<String, AtomicInteger> remainingDependencyCount = new ConcurrentHashMap<>();

    // explicit private lock for graph mutation / graph reads that must be consistent
    private final Object graphLock = new Object();

    public DependencyService(InMemoryTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public DependencyRegistrationResult register(TaskDefinition taskDefinition) {
        synchronized (graphLock) {
            validateNoSelfDependency(taskDefinition);
            validateNoCycles(taskDefinition);

            List<String> dependencies = taskDefinition.getDependencies();
            if (dependencies.isEmpty()) {
                return new DependencyRegistrationResult(true, false, 0);
            }

            int unmet = 0;

            for (String dependencyId : dependencies) {
                TaskRuntime dependencyRuntime = taskRepository.getRuntime(dependencyId);

                if (dependencyRuntime == null) {
                    unmet++;
                } else {
                    TaskStatus status = dependencyRuntime.getStatus();

                    if (status == TaskStatus.FAILED || status == TaskStatus.SKIPPED) {
                        return new DependencyRegistrationResult(false, true, 0);
                    }

                    if (status != TaskStatus.SUCCEEDED) {
                        unmet++;
                    }
                }

                reverseDependencies
                        .computeIfAbsent(dependencyId, key -> ConcurrentHashMap.newKeySet())
                        .add(taskDefinition.getTaskId());
            }

            remainingDependencyCount.put(taskDefinition.getTaskId(), new AtomicInteger(unmet));
            return new DependencyRegistrationResult(unmet == 0, false, unmet);
        }
    }

    public List<String> onTaskSucceeded(String completedTaskId) {
        synchronized (graphLock) {
            List<String> newlyReadyTasks = new ArrayList<>();
            Set<String> children = reverseDependencies.getOrDefault(completedTaskId, Set.of());

            for (String childTaskId : children) {
                AtomicInteger counter = remainingDependencyCount.get(childTaskId);
                if (counter == null) {
                    continue;
                }

                int left = counter.decrementAndGet();
                if (left == 0) {
                    newlyReadyTasks.add(childTaskId);
                }
            }

            return newlyReadyTasks;
        }
    }

    public List<String> onTaskFailed(String failedTaskId) {
        synchronized (graphLock) {
            List<String> impacted = new ArrayList<>();
            Queue<String> queue = new ArrayDeque<>();
            Set<String> visited = new HashSet<>();

            queue.offer(failedTaskId);
            visited.add(failedTaskId);

            while (!queue.isEmpty()) {
                String current = queue.poll();
                Set<String> children = reverseDependencies.getOrDefault(current, Set.of());

                for (String child : children) {
                    if (visited.add(child)) {
                        impacted.add(child);
                        queue.offer(child);
                    }
                }
            }

            return impacted;
        }
    }


    private void validateNoSelfDependency(TaskDefinition taskDefinition) {
        if (taskDefinition.getDependencies().contains(taskDefinition.getTaskId())) {
            throw new IllegalArgumentException(
                    "Task cannot depend on itself: " + taskDefinition.getTaskId()
            );
        }
    }

    private void validateNoCycles(TaskDefinition newTask) {
        for (String dependencyId : newTask.getDependencies()) {
            if (hasPathTo(dependencyId, newTask.getTaskId(), new HashSet<>())) {
                throw new IllegalArgumentException(
                        "Cycle detected involving task: " + newTask.getTaskId()
                );
            }
        }
    }

    private boolean hasPathTo(String fromTaskId, String targetTaskId, Set<String> visited) {
        if (!visited.add(fromTaskId)) {
            return false;
        }

        if (fromTaskId.equals(targetTaskId)) {
            return true;
        }

        TaskDefinition definition = taskRepository.getDefinition(fromTaskId);
        if (definition == null) {
            return false;
        }

        for (String dependencyId : definition.getDependencies()) {
            if (hasPathTo(dependencyId, targetTaskId, visited)) {
                return true;
            }
        }

        return false;
    }
}
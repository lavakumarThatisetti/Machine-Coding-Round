package repository;

import model.TaskDefinition;
import model.TaskRuntime;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTaskRepository {
    private final ConcurrentHashMap<String, TaskDefinition> definitions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TaskRuntime> runtimes = new ConcurrentHashMap<>();

    public void saveDefinition(TaskDefinition definition) {
        TaskDefinition existing = definitions.putIfAbsent(definition.getTaskId(), definition);
        if (existing != null) {
            throw new IllegalArgumentException("Task already exists with id: " + definition.getTaskId());
        }
    }

    public TaskDefinition getDefinition(String taskId) {
        return definitions.get(taskId);
    }

    public boolean existsDefinition(String taskId) {
        return definitions.containsKey(taskId);
    }

    public void saveRuntime(TaskRuntime runtime) {
        runtimes.put(runtime.getTaskId(), runtime);
    }

    public TaskRuntime getRuntime(String taskId) {
        return runtimes.get(taskId);
    }

    public Collection<TaskDefinition> getAllDefinitions() {
        return definitions.values();
    }

    public Collection<TaskRuntime> getAllRuntimes() {
        return runtimes.values();
    }
}
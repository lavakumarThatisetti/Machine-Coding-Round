package model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TaskRuntime {
    private final String taskId;
    private final AtomicReference<TaskStatus> status;
    private final AtomicInteger attemptCount;
    private volatile long nextExecutionTime;
    private volatile Long lastStartTime;
    private volatile Long lastEndTime;
    private volatile String failureReason;

    public TaskRuntime(String taskId, TaskStatus initialStatus, long nextExecutionTime) {
        this.taskId = taskId;
        this.status = new AtomicReference<>(initialStatus);
        this.attemptCount = new AtomicInteger(0);
        this.nextExecutionTime = nextExecutionTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public TaskStatus getStatus() {
        return status.get();
    }

    public boolean compareAndSetStatus(TaskStatus expected, TaskStatus newStatus) {
        return status.compareAndSet(expected, newStatus);
    }

    public void forceStatus(TaskStatus newStatus) {
        status.set(newStatus);
    }

    public int getAttemptCount() {
        return attemptCount.get();
    }

    public int incrementAttemptCount() {
        return attemptCount.incrementAndGet();
    }

    public long getNextExecutionTime() {
        return nextExecutionTime;
    }

    public void setNextExecutionTime(long nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }

    public Long getLastStartTime() {
        return lastStartTime;
    }

    public void setLastStartTime(Long lastStartTime) {
        this.lastStartTime = lastStartTime;
    }

    public Long getLastEndTime() {
        return lastEndTime;
    }

    public void setLastEndTime(Long lastEndTime) {
        this.lastEndTime = lastEndTime;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    @Override
    public String toString() {
        return "TaskRuntime{" +
                "taskId='" + taskId + '\'' +
                ", status=" + status.get() +
                ", attemptCount=" + attemptCount.get() +
                ", nextExecutionTime=" + nextExecutionTime +
                ", lastStartTime=" + lastStartTime +
                ", lastEndTime=" + lastEndTime +
                ", failureReason='" + failureReason + '\'' +
                '}';
    }
}
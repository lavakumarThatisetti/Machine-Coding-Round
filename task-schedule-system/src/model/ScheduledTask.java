package model;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class ScheduledTask implements Delayed {
    private final String taskId;
    private final long executeAtMillis;

    public ScheduledTask(String taskId, long executeAtMillis) {
        this.taskId = taskId;
        this.executeAtMillis = executeAtMillis;
    }

    public String getTaskId() {
        return taskId;
    }

    public long getExecuteAtMillis() {
        return executeAtMillis;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = executeAtMillis - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        ScheduledTask o = (ScheduledTask) other;
        return Long.compare(this.executeAtMillis, o.executeAtMillis);
    }

    @Override
    public String toString() {
        return "ScheduledTask{" +
                "taskId='" + taskId + '\'' +
                ", executeAtMillis=" + executeAtMillis +
                '}';
    }
}
package model;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public record ScheduledTask(String taskId, long executeAtMillis) implements Delayed {

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
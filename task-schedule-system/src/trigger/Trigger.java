package trigger;

public interface Trigger {
    long nextExecutionTime(Long lastExecutionTime);
}
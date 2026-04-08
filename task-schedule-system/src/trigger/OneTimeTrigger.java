package trigger;

public class OneTimeTrigger implements Trigger {

    @Override
    public long nextExecutionTime(Long lastExecutionTime) {
        return System.currentTimeMillis();
    }
}
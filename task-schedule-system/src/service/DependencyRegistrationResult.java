package service;

public class DependencyRegistrationResult {
    private final boolean readyToSchedule;
    private final boolean blockedByFailedDependency;
    private final int remainingDependencies;

    public DependencyRegistrationResult(boolean readyToSchedule,
                                        boolean blockedByFailedDependency,
                                        int remainingDependencies) {
        this.readyToSchedule = readyToSchedule;
        this.blockedByFailedDependency = blockedByFailedDependency;
        this.remainingDependencies = remainingDependencies;
    }

    public boolean isReadyToSchedule() {
        return readyToSchedule;
    }

    public boolean isBlockedByFailedDependency() {
        return blockedByFailedDependency;
    }

    public int getRemainingDependencies() {
        return remainingDependencies;
    }

    @Override
    public String toString() {
        return "DependencyRegistrationResult{" +
                "readyToSchedule=" + readyToSchedule +
                ", blockedByFailedDependency=" + blockedByFailedDependency +
                ", remainingDependencies=" + remainingDependencies +
                '}';
    }
}
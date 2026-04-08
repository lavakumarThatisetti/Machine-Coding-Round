package model;

public enum TaskStatus {
    WAITING_FOR_DEPENDENCIES,
    SCHEDULED,
    RUNNING,
    SUCCEEDED,
    FAILED,
    SKIPPED
}

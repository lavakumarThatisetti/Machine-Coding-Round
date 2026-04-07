package domain;

public enum AttemptStatus {
    SUCCESS,
    RETRYABLE_FAILURE,
    NON_RETRYABLE_FAILURE,
    UNKNOWN
}

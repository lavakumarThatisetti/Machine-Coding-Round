package domain;

public enum PayoutStatus {
    CREATED,
    PROCESSING,
    SUCCESS,
    FAILED,
    UNKNOWN // Because downstream timeout may mean money sent but response lost.
}

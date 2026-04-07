package domain;

public class BankResult {
    private final AttemptStatus status;
    private final String message;

    public BankResult(AttemptStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public AttemptStatus getStatus() { return status; }
    public String getMessage() { return message; }

    public boolean isSuccess() {
        return status == AttemptStatus.SUCCESS;
    }

    public boolean isRetryableFailure() {
        return status == AttemptStatus.RETRYABLE_FAILURE;
    }

    public boolean isNonRetryableFailure() {
        return status == AttemptStatus.NON_RETRYABLE_FAILURE;
    }

    public boolean isUnknown() {
        return status == AttemptStatus.UNKNOWN;
    }
}

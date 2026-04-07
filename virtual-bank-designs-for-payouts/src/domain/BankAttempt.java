package domain;

public class BankAttempt {
    private final String bankName;
    private final AttemptStatus status;
    private final String reason;

    public BankAttempt(String bankName, AttemptStatus status, String reason) {
        this.bankName = bankName;
        this.status = status;
        this.reason = reason;
    }

    public String getBankName() { return bankName; }
    public AttemptStatus getStatus() { return status; }
    public String getReason() { return reason; }
}

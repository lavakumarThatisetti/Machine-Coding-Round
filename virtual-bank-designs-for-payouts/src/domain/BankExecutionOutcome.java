package domain;

public class BankExecutionOutcome {
    private final boolean attempted;
    private final BankResult result;
    private final String skipReason;

    private BankExecutionOutcome(boolean attempted, BankResult result, String skipReason) {
        this.attempted = attempted;
        this.result = result;
        this.skipReason = skipReason;
    }

    public static BankExecutionOutcome attempted(BankResult result) {
        return new BankExecutionOutcome(true, result, null);
    }

    public static BankExecutionOutcome skipped(String reason) {
        return new BankExecutionOutcome(false, null, reason);
    }

    public boolean attempted() { return attempted; }
    public BankResult result() { return result; }
    public String skipReason() { return skipReason; }
}

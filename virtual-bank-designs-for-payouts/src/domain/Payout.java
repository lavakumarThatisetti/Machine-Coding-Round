package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Payout {
    private final String payoutId;
    private final PayoutRequest request;
    private PayoutStatus status;
    private String selectedBank;
    private final List<BankAttempt> attempts = new ArrayList<>();
    private final List<String> notes = new ArrayList<>();

    public Payout(String payoutId, PayoutRequest request) {
        this.payoutId = payoutId;
        this.request = request;
        this.status = PayoutStatus.CREATED;
    }

    public String getPayoutId() {
        return payoutId;
    }

    public PayoutRequest getRequest() {
        return request;
    }

    public synchronized PayoutStatus getStatus() {
        return status;
    }

    public synchronized String getSelectedBank() {
        return selectedBank;
    }

    public synchronized void markProcessing() {
        ensureState(PayoutStatus.CREATED);
        this.status = PayoutStatus.PROCESSING;
    }

    public synchronized void markSuccess(String bankName) {
        ensureState(PayoutStatus.PROCESSING);
        this.status = PayoutStatus.SUCCESS;
        this.selectedBank = bankName;
    }

    public synchronized void markFailed() {
        ensureState(PayoutStatus.PROCESSING);
        this.status = PayoutStatus.FAILED;
    }

    public synchronized void markUnknown(String bankName) {
        ensureState(PayoutStatus.PROCESSING);
        this.status = PayoutStatus.UNKNOWN;
        this.selectedBank = bankName;
    }

    public synchronized void addAttempt(BankAttempt attempt) {
        this.attempts.add(attempt);
    }

    public synchronized List<BankAttempt> getAttempts() {
        return Collections.unmodifiableList(new ArrayList<>(attempts));
    }

    public synchronized void addNote(String note) {
        this.notes.add(note);
    }

    public synchronized List<String> getNotes() {
        return Collections.unmodifiableList(new ArrayList<>(notes));
    }

    private void ensureState(PayoutStatus expected) {
        if (status != expected) {
            throw new IllegalStateException("Expected state " + expected + " but found " + status);
        }
    }
}
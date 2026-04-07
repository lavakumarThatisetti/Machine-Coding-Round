package service;

import domain.*;
import provider.VirtualBank;
import provider.VirtualBankPool;
import repository.PayoutRepository;
import strategy.BankSelectionStrategy;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class PayoutService {
    private final PayoutRepository payoutRepository;
    private final VirtualBankPool bankPool;
    private final BankSelectionStrategy selectionStrategy;
    private final IdempotencyLockManager lockManager;
    private final BankExecutionService bankExecutionService;

    public PayoutService(
            PayoutRepository payoutRepository,
            VirtualBankPool bankPool,
            BankSelectionStrategy selectionStrategy,
            IdempotencyLockManager lockManager, BankExecutionService bankExecutionService) {
        this.payoutRepository = payoutRepository;
        this.bankPool = bankPool;
        this.selectionStrategy = selectionStrategy;
        this.lockManager = lockManager;
        this.bankExecutionService = bankExecutionService;
    }

    public Payout process(PayoutRequest request) {
        ReentrantLock lock = lockManager.getLock(request.idempotencyKey());
        lock.lock();
        try {
            Payout existing = payoutRepository.findByIdempotencyKey(request.idempotencyKey()).orElse(null);
            if (existing != null) {
                return existing;
            }

            Payout payout = new Payout(UUID.randomUUID().toString(), request);
            payout.markProcessing();
            payoutRepository.save(payout);

            List<VirtualBank> candidates = selectionStrategy.selectCandidates(request, bankPool.getAllBanks());

            for (VirtualBank bank : candidates) {
                BankExecutionOutcome outcome = bankExecutionService.execute(bank, request);

                if (!outcome.attempted()) {
                    payout.addNote("Skipped bank " + bank.name() + " because " + outcome.skipReason());
                    continue;
                }

                BankResult result = outcome.result();
                payout.addAttempt(new BankAttempt(bank.name(), result.getStatus(), result.getMessage()));

                if (result.isSuccess()) {
                    payout.markSuccess(bank.name());
                    return payout;
                }

                if (result.isUnknown()) {
                    payout.markUnknown(bank.name());
                    return payout;
                }

                if (result.isNonRetryableFailure()) {
                    payout.markFailed();
                    return payout;
                }
            }
            payout.markFailed();

            return payout;

        } finally {
            lock.unlock();
        }
    }
}

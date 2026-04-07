package repository.impl;


import model.Balance;
import model.LedgerKey;
import model.UserPair;
import repository.BalanceLedgerRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryBalanceLedgerRepository implements BalanceLedgerRepository {

    private final ConcurrentHashMap<LedgerKey, BigDecimal> ledger = new ConcurrentHashMap<>();

    @Override
    public void addDebt(String groupId, String debtorUserId, String creditorUserId, BigDecimal amount) {
        validateUsers(debtorUserId, creditorUserId);
        validatePositiveAmount(amount);

        LedgerKey key = new LedgerKey(groupId, new UserPair(debtorUserId, creditorUserId));

        ledger.compute(key, (k, existing) -> {
            BigDecimal current = existing == null ? BigDecimal.ZERO : existing;
            BigDecimal addition = toSignedAmount(k.userPair(), debtorUserId, creditorUserId, amount);
            BigDecimal updated = current.add(addition);
            return normalize(updated);
        });
    }

    @Override
    public void reduceDebt(String groupId, String debtorUserId, String creditorUserId, BigDecimal amount) {
        validateUsers(debtorUserId, creditorUserId);
        validatePositiveAmount(amount);

        LedgerKey key = new LedgerKey(groupId, new UserPair(debtorUserId, creditorUserId));

        ledger.compute(key, (k, existing) -> {
            BigDecimal current = existing == null ? BigDecimal.ZERO : existing;
            BigDecimal reduction = toSignedAmount(k.userPair(), debtorUserId, creditorUserId, amount);
            BigDecimal updated = current.subtract(reduction);
            return normalize(updated);
        });
    }

    @Override
    public List<Balance> findBalancesForUser(String userId) {
        validateUser(userId);

        List<Balance> result = new ArrayList<>();
        for (Map.Entry<LedgerKey, BigDecimal> entry : ledger.entrySet()) {
            LedgerKey key = entry.getKey();
            BigDecimal amount = entry.getValue();

            if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            if (key.userPair().contains(userId)) {
                Balance balance = toBalance(key, amount);
                if (balance.debtorUserId().equals(userId) || balance.creditorUserId().equals(userId)) {
                    result.add(balance);
                }
            }
        }
        return result;
    }

    @Override
    public List<Balance> findBalancesForUserInGroup(String groupId, String userId) {
        validateUser(userId);

        List<Balance> result = new ArrayList<>();
        for (Map.Entry<LedgerKey, BigDecimal> entry : ledger.entrySet()) {
            LedgerKey key = entry.getKey();
            BigDecimal amount = entry.getValue();

            if (!equalsNullable(groupId, key.groupId())) {
                continue;
            }

            if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            if (key.userPair().contains(userId)) {
                Balance balance = toBalance(key, amount);
                if (balance.debtorUserId().equals(userId) || balance.creditorUserId().equals(userId)) {
                    result.add(balance);
                }
            }
        }
        return result;
    }

    @Override
    public List<Balance> findBalancesForGroup(String groupId) {
        List<Balance> result = new ArrayList<>();

        for (Map.Entry<LedgerKey, BigDecimal> entry : ledger.entrySet()) {
            LedgerKey key = entry.getKey();
            BigDecimal amount = entry.getValue();

            if (!equalsNullable(groupId, key.groupId())) {
                continue;
            }

            if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            result.add(toBalance(key, amount));
        }

        return result;
    }

    @Override
    public List<Balance> findAllBalances() {
        List<Balance> result = new ArrayList<>();

        for (Map.Entry<LedgerKey, BigDecimal> entry : ledger.entrySet()) {
            BigDecimal amount = entry.getValue();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            result.add(toBalance(entry.getKey(), amount));
        }

        return result;
    }

    @Override
    public Optional<Balance> findBalanceBetween(String groupId, String userA, String userB) {
        validateUsers(userA, userB);

        LedgerKey key = new LedgerKey(groupId, new UserPair(userA, userB));
        BigDecimal signedAmount = ledger.get(key);

        if (signedAmount == null || signedAmount.compareTo(BigDecimal.ZERO) == 0) {
            return Optional.empty();
        }

        return Optional.of(toBalance(key, signedAmount));
    }

    private BigDecimal toSignedAmount(UserPair pair, String debtorUserId, String creditorUserId, BigDecimal amount) {
        if (pair.getFirstUserId().equals(creditorUserId) && pair.getSecondUserId().equals(debtorUserId)) {
            return amount;
        }

        if (pair.getFirstUserId().equals(debtorUserId) && pair.getSecondUserId().equals(creditorUserId)) {
            return amount.negate();
        }

        throw new IllegalStateException("Users do not match normalized pair");
    }

    private Balance toBalance(LedgerKey key, BigDecimal signedAmount) {
        UserPair pair = key.userPair();

        if (signedAmount.compareTo(BigDecimal.ZERO) > 0) {
            return new Balance(
                    pair.getSecondUserId(),
                    pair.getFirstUserId(),
                    signedAmount,
                    key.groupId()
            );
        }

        BigDecimal positive = signedAmount.abs();
        return new Balance(
                pair.getFirstUserId(),
                pair.getSecondUserId(),
                positive,
                key.groupId()
        );
    }

    private BigDecimal normalize(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return amount.stripTrailingZeros();
    }

    private void validateUsers(String debtorUserId, String creditorUserId) {
        validateUser(debtorUserId);
        validateUser(creditorUserId);

        if (debtorUserId.equals(creditorUserId)) {
            throw new IllegalArgumentException("Debtor and creditor cannot be same");
        }
    }

    private void validateUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User id cannot be blank");
        }
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    private boolean equalsNullable(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }
}

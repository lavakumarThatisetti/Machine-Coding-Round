package service;


import exception.ValidationException;
import model.Balance;
import repository.BalanceLedgerRepository;

import java.util.List;
import java.util.Objects;

public class BalanceQueryService {
    private final BalanceLedgerRepository balanceLedgerRepository;

    public BalanceQueryService(BalanceLedgerRepository balanceLedgerRepository) {
        this.balanceLedgerRepository = Objects.requireNonNull(
                balanceLedgerRepository,
                "BalanceLedgerRepository cannot be null"
        );
    }

    public List<Balance> getAllBalances() {
        return balanceLedgerRepository.findAllBalances();
    }

    public List<Balance> getBalancesForUser(String userId) {
        validateUserId(userId);
        return balanceLedgerRepository.findBalancesForUser(userId);
    }

    public List<Balance> getBalancesForGroup(String groupId) {
        validateGroupId(groupId);
        return balanceLedgerRepository.findBalancesForGroup(groupId);
    }

    public List<Balance> getBalancesForUserInGroup(String groupId, String userId) {
        validateGroupId(groupId);
        validateUserId(userId);
        return balanceLedgerRepository.findBalancesForUserInGroup(groupId, userId);
    }

    public Balance getBalanceBetween(String groupId, String userA, String userB) {
        validateUserId(userA);
        validateUserId(userB);
        if (userA.equals(userB)) {
            throw new ValidationException("Users must be different");
        }

        return balanceLedgerRepository.findBalanceBetween(groupId, userA, userB)
                .orElse(null);
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ValidationException("User id cannot be blank");
        }
    }

    private void validateGroupId(String groupId) {
        if (groupId == null || groupId.isBlank()) {
            throw new ValidationException("Group id cannot be blank");
        }
    }
}
package repository;

import model.Balance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BalanceLedgerRepository {

    void addDebt(String groupId, String debtorUserId, String creditorUserId, BigDecimal amount);

    void reduceDebt(String groupId, String debtorUserId, String creditorUserId, BigDecimal amount);

    Optional<Balance> findBalanceBetween(String groupId, String userA, String userB);

    List<Balance> findBalancesForUser(String userId);

    List<Balance> findBalancesForUserInGroup(String groupId, String userId);

    List<Balance> findBalancesForGroup(String groupId);

    List<Balance> findAllBalances();
}

package service;

import dto.SettleUpRequest;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import model.Balance;
import model.Group;
import model.Settlement;
import repository.BalanceLedgerRepository;
import repository.GroupRepository;
import repository.SettlementRepository;
import repository.UserRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class SettlementService {
    private final SettlementRepository settlementRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final BalanceLedgerRepository balanceLedgerRepository;
    private final LockManager lockManager;

    public SettlementService(
            SettlementRepository settlementRepository,
            UserRepository userRepository,
            GroupRepository groupRepository,
            BalanceLedgerRepository balanceLedgerRepository,
            LockManager lockManager
    ) {
        this.settlementRepository = Objects.requireNonNull(settlementRepository, "SettlementRepository cannot be null");
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository cannot be null");
        this.groupRepository = Objects.requireNonNull(groupRepository, "GroupRepository cannot be null");
        this.balanceLedgerRepository = Objects.requireNonNull(balanceLedgerRepository, "BalanceLedgerRepository cannot be null");
        this.lockManager = Objects.requireNonNull(lockManager, "LockManager cannot be null");
    }

    public Settlement settleUp(SettleUpRequest request) {
        validate(request);

        ReentrantLock lock = lockManager.getLock(buildLockKey(request));
        lock.lock();
        try {
            if (settlementRepository.findById(request.settlementId()).isPresent()) {
                throw new DuplicateEntityException("Settlement already exists with id: " + request.settlementId());
            }

            validateUsersExist(request);
            validateGroupRules(request);

            Balance outstanding = balanceLedgerRepository
                    .findBalanceBetween(request.groupId(), request.fromUserId(), request.toUserId())
                    .orElseThrow(() -> new ValidationException("No outstanding balance exists between users"));

            if (!outstanding.debtorUserId().equals(request.fromUserId())
                    || !outstanding.creditorUserId().equals(request.toUserId())) {
                throw new ValidationException(
                        request.fromUserId() + " does not owe " + request.toUserId()
                );
            }

            if (request.amount().compareTo(outstanding.amount()) > 0) {
                throw new ValidationException(
                        "Settlement amount cannot exceed outstanding balance of " + outstanding.amount()
                );
            }

            Settlement settlement = new Settlement(
                    request.settlementId(),
                    request.fromUserId(),
                    request.toUserId(),
                    request.amount(),
                    request.groupId(),
                    Instant.now()
            );

            settlementRepository.save(settlement);

            balanceLedgerRepository.reduceDebt(
                    request.groupId(),
                    request.fromUserId(),
                    request.toUserId(),
                    request.amount()
            );

            return settlement;
        } finally {
            lock.unlock();
        }
    }

    public Settlement getSettlement(String settlementId) {
        if (settlementId == null || settlementId.isBlank()) {
            throw new ValidationException("Settlement id cannot be blank");
        }

        return settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("Settlement not found: " + settlementId));
    }

    public List<Settlement> getAllSettlements() {
        return settlementRepository.findAll();
    }

    public List<Settlement> getSettlementsForGroup(String groupId) {
        if (groupId == null || groupId.isBlank()) {
            throw new ValidationException("Group id cannot be blank");
        }
        return settlementRepository.findByGroupId(groupId);
    }

    private void validate(SettleUpRequest request) {
        if (request == null) {
            throw new ValidationException("SettleUpRequest cannot be null");
        }
        if (request.settlementId() == null || request.settlementId().isBlank()) {
            throw new ValidationException("Settlement id cannot be blank");
        }
        if (request.fromUserId() == null || request.fromUserId().isBlank()) {
            throw new ValidationException("From userId cannot be blank");
        }
        if (request.toUserId() == null || request.toUserId().isBlank()) {
            throw new ValidationException("To userId cannot be blank");
        }
        if (request.fromUserId().equals(request.toUserId())) {
            throw new ValidationException("Settlement users must be different");
        }
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Settlement amount must be positive");
        }
    }

    private void validateUsersExist(SettleUpRequest request) {
        if (!userRepository.existsById(request.fromUserId())) {
            throw new EntityNotFoundException("User not found: " + request.fromUserId());
        }
        if (!userRepository.existsById(request.toUserId())) {
            throw new EntityNotFoundException("User not found: " + request.toUserId());
        }
    }

    private void validateGroupRules(SettleUpRequest request) {
        if (request.groupId() == null) {
            return;
        }

        Group group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new EntityNotFoundException("Group not found: " + request.groupId()));

        if (!group.hasMember(request.fromUserId())) {
            throw new ValidationException("From user is not a member of group " + request.groupId());
        }
        if (!group.hasMember(request.toUserId())) {
            throw new ValidationException("To user is not a member of group " + request.groupId());
        }
    }

    private String buildLockKey(SettleUpRequest request) {
        String first = request.fromUserId().compareTo(request.toUserId()) < 0
                ? request.fromUserId()
                : request.toUserId();
        String second = request.fromUserId().compareTo(request.toUserId()) < 0
                ? request.toUserId()
                : request.fromUserId();

        String scope = request.groupId() == null ? "DIRECT" : "GROUP_" + request.groupId();
        return "SETTLEMENT_" + scope + "_" + first + "_" + second;
    }
}

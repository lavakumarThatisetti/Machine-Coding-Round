package repository.impl;


import model.Settlement;
import repository.SettlementRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemorySettlementRepository implements SettlementRepository {
    private final ConcurrentMap<String, Settlement> storage = new ConcurrentHashMap<>();

    @Override
    public void save(Settlement settlement) {
        if (settlement == null) {
            throw new IllegalArgumentException("Settlement cannot be null");
        }
        storage.put(settlement.getId(), settlement);
    }

    @Override
    public Optional<Settlement> findById(String settlementId) {
        if (settlementId == null || settlementId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(settlementId));
    }

    @Override
    public List<Settlement> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Settlement> findByGroupId(String groupId) {
        List<Settlement> result = new ArrayList<>();
        if (groupId == null || groupId.isBlank()) {
            return result;
        }

        for (Settlement settlement : storage.values()) {
            if (groupId.equals(settlement.getGroupId())) {
                result.add(settlement);
            }
        }
        return result;
    }

    @Override
    public List<Settlement> findByFromUserId(String userId) {
        List<Settlement> result = new ArrayList<>();
        if (userId == null || userId.isBlank()) {
            return result;
        }

        for (Settlement settlement : storage.values()) {
            if (userId.equals(settlement.getFromUserId())) {
                result.add(settlement);
            }
        }
        return result;
    }

    @Override
    public List<Settlement> findByToUserId(String userId) {
        List<Settlement> result = new ArrayList<>();
        if (userId == null || userId.isBlank()) {
            return result;
        }

        for (Settlement settlement : storage.values()) {
            if (userId.equals(settlement.getToUserId())) {
                result.add(settlement);
            }
        }
        return result;
    }
}
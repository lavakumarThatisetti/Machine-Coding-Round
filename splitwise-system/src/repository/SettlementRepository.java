package repository;

import model.Settlement;

import java.util.List;
import java.util.Optional;

public interface SettlementRepository {
    void save(Settlement settlement);

    Optional<Settlement> findById(String settlementId);

    List<Settlement> findAll();

    List<Settlement> findByGroupId(String groupId);

    List<Settlement> findByFromUserId(String userId);

    List<Settlement> findByToUserId(String userId);
}

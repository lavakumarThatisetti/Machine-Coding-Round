package repository;

import domain.Payout;

import java.util.Optional;

public interface PayoutRepository {
    Payout save(Payout payout);
    Optional<Payout> findById(String payoutId);
    Optional<Payout> findByIdempotencyKey(String idempotencyKey);
}

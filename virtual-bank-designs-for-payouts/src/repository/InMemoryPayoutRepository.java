package repository;

import domain.Payout;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPayoutRepository implements PayoutRepository {
    private final ConcurrentHashMap<String, Payout> payoutById = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> payoutIdByIdempotencyKey = new ConcurrentHashMap<>();

    @Override
    public Payout save(Payout payout) {
        payoutById.put(payout.getPayoutId(), payout);
        payoutIdByIdempotencyKey.putIfAbsent(payout.getRequest().idempotencyKey(), payout.getPayoutId());
        return payout;
    }

    @Override
    public Optional<Payout> findById(String payoutId) {
        return Optional.ofNullable(payoutById.get(payoutId));
    }

    @Override
    public Optional<Payout> findByIdempotencyKey(String idempotencyKey) {
        String payoutId = payoutIdByIdempotencyKey.get(idempotencyKey);
        if (payoutId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(payoutById.get(payoutId));
    }
}

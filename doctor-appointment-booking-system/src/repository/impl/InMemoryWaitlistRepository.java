package repository.impl;

import model.SlotKey;
import model.WaitlistEntry;
import repository.WaitlistRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryWaitlistRepository implements WaitlistRepository {
    private final ConcurrentHashMap<SlotKey, WaitlistBucket> waitlists = new ConcurrentHashMap<>();

    @Override
    public boolean enqueueIfAbsent(SlotKey slotKey, WaitlistEntry entry) {
        WaitlistBucket bucket = waitlists.computeIfAbsent(slotKey, key -> new WaitlistBucket());
        return bucket.enqueueIfAbsent(entry);
    }

    @Override
    public Optional<WaitlistEntry> peek(SlotKey slotKey) {
        WaitlistBucket bucket = waitlists.get(slotKey);
        if (bucket == null) {
            return Optional.empty();
        }
        return bucket.peek();
    }

    @Override
    public Optional<WaitlistEntry> poll(SlotKey slotKey) {
        WaitlistBucket bucket = waitlists.get(slotKey);
        if (bucket == null) {
            return Optional.empty();
        }
        return bucket.poll();
    }

    @Override
    public boolean remove(SlotKey slotKey, String patientId) {
        WaitlistBucket bucket = waitlists.get(slotKey);
        if (bucket == null) {
            return false;
        }
        return bucket.remove(patientId);
    }

    @Override
    public boolean contains(SlotKey slotKey, String patientId) {
        WaitlistBucket bucket = waitlists.get(slotKey);
        if (bucket == null) {
            return false;
        }
        return bucket.contains(patientId);
    }

    @Override
    public List<WaitlistEntry> findAll(SlotKey slotKey) {
        WaitlistBucket bucket = waitlists.get(slotKey);
        if (bucket == null) {
            return new ArrayList<>();
        }
        return bucket.snapshot();
    }

    @Override
    public boolean isEmpty(SlotKey slotKey) {
        WaitlistBucket bucket = waitlists.get(slotKey);
        return bucket == null || bucket.isEmpty();
    }
}
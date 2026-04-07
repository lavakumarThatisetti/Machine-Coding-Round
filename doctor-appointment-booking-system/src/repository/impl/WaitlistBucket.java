package repository.impl;

import model.WaitlistEntry;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

class WaitlistBucket {
    private final Queue<WaitlistEntry> queue = new ArrayDeque<>();
    private final Set<String> patientIds = new HashSet<>();

    public boolean enqueueIfAbsent(WaitlistEntry entry) {
        if (patientIds.contains(entry.getPatientId())) {
            return false;
        }
        queue.offer(entry);
        patientIds.add(entry.getPatientId());
        return true;
    }

    public Optional<WaitlistEntry> peek() {
        return Optional.ofNullable(queue.peek());
    }

    public Optional<WaitlistEntry> poll() {
        WaitlistEntry entry = queue.poll();
        if (entry != null) {
            patientIds.remove(entry.getPatientId());
        }
        return Optional.ofNullable(entry);
    }

    public boolean contains(String patientId) {
        return patientIds.contains(patientId);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public boolean remove(String patientId) {
        WaitlistEntry found = null;
        for (WaitlistEntry entry : queue) {
            if (entry.getPatientId().equals(patientId)) {
                found = entry;
                break;
            }
        }

        if (found == null) {
            return false;
        }

        boolean removed = queue.remove(found);
        if (removed) {
            patientIds.remove(patientId);
        }
        return removed;
    }

    public List<WaitlistEntry> snapshot() {
        return new ArrayList<>(queue);
    }
}
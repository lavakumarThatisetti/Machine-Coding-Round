package repository.impl;


import model.Group;
import repository.GroupRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryGroupRepository implements GroupRepository {
    private final ConcurrentMap<String, Group> storage = new ConcurrentHashMap<>();

    @Override
    public void save(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("Group cannot be null");
        }
        storage.put(group.getId(), group);
    }

    @Override
    public Optional<Group> findById(String groupId) {
        if (groupId == null || groupId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(groupId));
    }

    @Override
    public List<Group> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean existsById(String groupId) {
        if (groupId == null || groupId.isBlank()) {
            return false;
        }
        return storage.containsKey(groupId);
    }
}
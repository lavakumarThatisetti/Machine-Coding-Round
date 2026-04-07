package repository.impl;

import model.User;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryUserRepository implements UserRepository {
    private final ConcurrentMap<String, User> storage = new ConcurrentHashMap<>();

    @Override
    public void save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        storage.put(user.id(), user);
    }

    @Override
    public Optional<User> findById(String userId) {
        if (userId == null || userId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean existsById(String userId) {
        if (userId == null || userId.isBlank()) {
            return false;
        }
        return storage.containsKey(userId);
    }
}
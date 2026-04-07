package repository.impl;


import model.User;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryUserRepository implements UserRepository {
    private final ConcurrentMap<String, User> usersById = new ConcurrentHashMap<>();

    @Override
    public void save(User user) {
        usersById.put(user.getId(), user);
    }

    @Override
    public Optional<User> findById(String userId) {
        return Optional.ofNullable(usersById.get(userId));
    }

    @Override
    public boolean existsById(String userId) {
        return usersById.containsKey(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }
}
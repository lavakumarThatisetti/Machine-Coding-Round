package repository.impl;

import model.UserAccount;
import repository.UserRepository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryUserRepository implements UserRepository {
    private final ConcurrentMap<String, UserAccount> users = new ConcurrentHashMap<>();

    @Override
    public void save(UserAccount user) {
        users.put(user.getUserId(), user);
    }

    @Override
    public Optional<UserAccount> findById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public boolean existsById(String userId) {
        return users.containsKey(userId);
    }
}

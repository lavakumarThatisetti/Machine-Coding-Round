package service;

import model.User;
import exception.UserAlreadyExistsException;
import exception.UserNotFoundException;
import repository.UserRepository;

import java.util.List;
import java.util.Objects;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    public User createUser(String userId, String name) {
        if (userRepository.existsById(userId)) {
            throw new UserAlreadyExistsException("User already exists: " + userId);
        }

        User user = new User(userId, name);
        userRepository.save(user);
        return user;
    }

    public User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    }

    public boolean exists(String userId) {
        return userRepository.existsById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
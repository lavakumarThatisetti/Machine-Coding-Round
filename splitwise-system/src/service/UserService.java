package service;


import dto.CreateUserRequest;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import model.User;
import repository.UserRepository;

import java.util.List;
import java.util.Objects;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository cannot be null");
    }

    public User createUser(CreateUserRequest request) {
        validate(request);

        if (userRepository.existsById(request.userId())) {
            throw new DuplicateEntityException("User already exists with id: " + request.userId());
        }

        User user = new User(
                request.userId(),
                request.name(),
                request.email(),
                request.mobile()
        );

        userRepository.save(user);
        return user;
    }

    public User getUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ValidationException("User id cannot be blank");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private void validate(CreateUserRequest request) {
        if (request == null) {
            throw new ValidationException("CreateUserRequest cannot be null");
        }
        if (request.userId() == null || request.userId().isBlank()) {
            throw new ValidationException("User id cannot be blank");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new ValidationException("User name cannot be blank");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new ValidationException("Email cannot be blank");
        }
        if (request.mobile() == null || request.mobile().isBlank()) {
            throw new ValidationException("Mobile cannot be blank");
        }
    }
}

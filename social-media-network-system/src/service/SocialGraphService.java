package service;

import exception.InvalidOperationException;
import exception.UserNotFoundException;
import repository.FollowRepository;
import repository.UserRepository;

import java.util.Objects;
import java.util.Set;

public class SocialGraphService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public SocialGraphService(UserRepository userRepository,
                                     FollowRepository followRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.followRepository = Objects.requireNonNull(followRepository);
    }

   
    public void follow(String followerId, String followeeId) {
        ensureUserExists(followerId);
        ensureUserExists(followeeId);

        if (followerId.equals(followeeId)) {
            throw new InvalidOperationException("User cannot follow self: " + followerId);
        }

        followRepository.follow(followerId, followeeId);
    }

   
    public void unfollow(String followerId, String followeeId) {
        ensureUserExists(followerId);
        ensureUserExists(followeeId);

        if (followerId.equals(followeeId)) {
            throw new InvalidOperationException("User cannot unfollow self: " + followerId);
        }

        followRepository.unfollow(followerId, followeeId);
    }

   
    public Set<String> getFollowings(String userId) {
        ensureUserExists(userId);
        return followRepository.findFollowings(userId);
    }

   
    public Set<String> getFollowers(String userId) {
        ensureUserExists(userId);
        return followRepository.findFollowers(userId);
    }

   
    public boolean isFollowing(String followerId, String followeeId) {
        ensureUserExists(followerId);
        ensureUserExists(followeeId);
        return followRepository.isFollowing(followerId, followeeId);
    }

    private void ensureUserExists(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found: " + userId);
        }
    }
}
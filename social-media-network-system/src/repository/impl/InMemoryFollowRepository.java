package repository.impl;

import repository.FollowRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryFollowRepository implements FollowRepository {

    // followerId -> set of followeeIds
    private final ConcurrentMap<String, Set<String>> followingsByUser = new ConcurrentHashMap<>();

    // followeeId -> set of followerIds
    private final ConcurrentMap<String, Set<String>> followersByUser = new ConcurrentHashMap<>();

    @Override
    public boolean follow(String followerId, String followeeId) {
        Set<String> followings =
                followingsByUser.computeIfAbsent(followerId, key -> ConcurrentHashMap.newKeySet());
        Set<String> followers =
                followersByUser.computeIfAbsent(followeeId, key -> ConcurrentHashMap.newKeySet());

        boolean added = followings.add(followeeId);
        if (added) {
            followers.add(followerId);
        }
        return added;
    }

    @Override
    public boolean unfollow(String followerId, String followeeId) {
        Set<String> followings = followingsByUser.get(followerId);
        if (followings == null) {
            return false;
        }

        boolean removed = followings.remove(followeeId);
        if (removed) {
            Set<String> followers = followersByUser.get(followeeId);
            if (followers != null) {
                followers.remove(followerId);
            }
        }
        return removed;
    }

    @Override
    public Set<String> findFollowings(String userId) {
        Set<String> followings = followingsByUser.get(userId);
        if (followings == null) {
            return Collections.emptySet();
        }
        return Set.copyOf(followings);
    }

    @Override
    public Set<String> findFollowers(String userId) {
        Set<String> followers = followersByUser.get(userId);
        if (followers == null) {
            return Collections.emptySet();
        }
        return Set.copyOf(followers);
    }

    @Override
    public boolean isFollowing(String followerId, String followeeId) {
        Set<String> followings = followingsByUser.get(followerId);
        return followings != null && followings.contains(followeeId);
    }
}
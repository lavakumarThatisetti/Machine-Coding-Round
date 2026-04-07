package repository;

import java.util.Set;

public interface FollowRepository {
    boolean follow(String followerId, String followeeId);

    boolean unfollow(String followerId, String followeeId);

    Set<String> findFollowings(String userId);

    Set<String> findFollowers(String userId);

    boolean isFollowing(String followerId, String followeeId);
}

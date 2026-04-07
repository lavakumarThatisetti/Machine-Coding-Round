package service;

import model.FeedItem;
import model.Post;
import service.strategy.FeedFetchStrategy;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FeedService {
    private static final int DEFAULT_LIMIT = 10;

    private final UserService userService;
    private final SocialGraphService socialGraphService;
    private final FeedFetchStrategy feedFetchStrategy;

    public FeedService(UserService userService,
                       SocialGraphService socialGraphService,
                       FeedFetchStrategy feedFetchStrategy) {
        this.userService = Objects.requireNonNull(userService);
        this.socialGraphService = Objects.requireNonNull(socialGraphService);
        this.feedFetchStrategy = Objects.requireNonNull(feedFetchStrategy);
    }

    public List<FeedItem> getFeed(String userId) {
        return getFeed(userId, DEFAULT_LIMIT);
    }

    public List<FeedItem> getFeed(String userId, int limit) {
        userService.getUser(userId);

        if (limit <= 0) {
            return List.of();
        }

        Set<String> authorIds = new HashSet<>(socialGraphService.getFollowings(userId));
        authorIds.add(userId);

        List<Post> posts = feedFetchStrategy.fetchRecentPosts(authorIds, limit);

        return posts.stream()
                .map(post -> new FeedItem(
                        post.getId(),
                        post.getAuthorId(),
                        post.getContent(),
                        post.getCreatedAt()
                ))
                .toList();
    }
}
package service.strategy;

import model.Post;
import service.PostService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SimpleFeedFetchStrategy implements FeedFetchStrategy {
    private final PostService postService;

    public SimpleFeedFetchStrategy(PostService postService) {
        this.postService = Objects.requireNonNull(postService);
    }

    @Override
    public List<Post> fetchRecentPosts(Set<String> authorIds, int limit) {
        int perUserFetchLimit = Math.max(limit, 20);
        List<Post> candidates = new ArrayList<>();

        for (String authorId : authorIds) {
            candidates.addAll(postService.getRecentPostsByUser(authorId, perUserFetchLimit));
        }

        return candidates.stream()
                .filter(Post::isActive)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed()
                        .thenComparing(Post::getSequence, Comparator.reverseOrder()))
                .limit(limit)
                .toList();
    }
}

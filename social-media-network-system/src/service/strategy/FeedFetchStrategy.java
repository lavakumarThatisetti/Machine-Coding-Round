package service.strategy;

import model.Post;

import java.util.List;
import java.util.Set;

public interface FeedFetchStrategy {
    List<Post> fetchRecentPosts(Set<String> authorIds, int limit);
}

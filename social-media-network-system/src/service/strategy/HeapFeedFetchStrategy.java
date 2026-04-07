package service.strategy;


import model.Post;
import service.PostService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

public class HeapFeedFetchStrategy implements FeedFetchStrategy {
    private static final Comparator<Post> NEWEST_FIRST =
            Comparator.comparing(Post::getCreatedAt).reversed()
                    .thenComparing(Post::getSequence, Comparator.reverseOrder());

    private final PostService postService;

    public HeapFeedFetchStrategy(PostService postService) {
        this.postService = Objects.requireNonNull(postService);
    }

    @Override
    public List<Post> fetchRecentPosts(Set<String> authorIds, int limit) {
        int perUserFetchLimit = Math.max(limit, 20);

        PriorityQueue<PostCursor> heap = new PriorityQueue<>(
                (a, b) -> NEWEST_FIRST.compare(a.current(), b.current())
        );

        for (String authorId : authorIds) {
            List<Post> posts = postService.getRecentPostsByUser(authorId, perUserFetchLimit);
            if (posts.isEmpty()) {
                continue;
            }

            Iterator<Post> iterator = posts.iterator();
            heap.offer(new PostCursor(iterator.next(), iterator));
        }

        List<Post> result = new ArrayList<>();

        while (!heap.isEmpty() && result.size() < limit) {
            PostCursor cursor = heap.poll();
            Post current = cursor.current();

            if (current.isActive()) {
                result.add(current);
            }

            if (cursor.hasNext()) {
                heap.offer(cursor.next());
            }
        }

        return result;
    }

    private record PostCursor(Post current, Iterator<Post> iterator) {

        private boolean hasNext() {
            return iterator.hasNext();
        }
        private PostCursor next() {
            return new PostCursor(iterator.next(), iterator);
        }
    }
}

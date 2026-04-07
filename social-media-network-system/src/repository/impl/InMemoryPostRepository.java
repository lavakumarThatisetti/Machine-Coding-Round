package repository.impl;


import model.Post;
import repository.PostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;

public class InMemoryPostRepository implements PostRepository {
    private final ConcurrentMap<String, Post> postsById = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, ConcurrentLinkedDeque<Post>> postsByAuthor = new ConcurrentHashMap<>();

    @Override
    public void save(Post post) {
        postsById.put(post.getId(), post);
        postsByAuthor
                .computeIfAbsent(post.getAuthorId(), key -> new ConcurrentLinkedDeque<>())
                .addFirst(post);
    }

    @Override
    public Optional<Post> findById(String postId) {
        return Optional.ofNullable(postsById.get(postId));
    }

    @Override
    public List<Post> findRecentPostsByAuthor(String authorId, int limit) {
        ConcurrentLinkedDeque<Post> posts = postsByAuthor.get(authorId);
        if (posts == null || limit <= 0) {
            return List.of();
        }

        List<Post> result = new ArrayList<>(limit);
        int count = 0;
        for (Post post : posts) {
            if (count >= limit) {
                break;
            }
            result.add(post);
            count++;
        }
        return result;
    }

    @Override
    public List<Post> findAllPostsByAuthor(String authorId) {
        ConcurrentLinkedDeque<Post> posts = postsByAuthor.get(authorId);
        if (posts == null) {
            return List.of();
        }
        return new ArrayList<>(posts);
    }
}
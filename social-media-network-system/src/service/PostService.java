package service;

import model.Post;
import exception.PostNotFoundException;
import exception.UnauthorizedPostAccessException;
import exception.UserNotFoundException;
import repository.PostRepository;
import repository.UserRepository;
import util.IdGenerator;
import util.SequenceGenerator;
import util.TimeProvider;

import java.util.List;
import java.util.Objects;

public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final IdGenerator idGenerator;
    private final TimeProvider timeProvider;
    private final SequenceGenerator sequenceGenerator;

    public PostService(UserRepository userRepository,
                              PostRepository postRepository,
                              IdGenerator idGenerator,
                              TimeProvider timeProvider,
                              SequenceGenerator sequenceGenerator) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.postRepository = Objects.requireNonNull(postRepository);
        this.idGenerator = Objects.requireNonNull(idGenerator);
        this.timeProvider = Objects.requireNonNull(timeProvider);
        this.sequenceGenerator = Objects.requireNonNull(sequenceGenerator);
    }

    
    public Post createPost(String authorId, String content) {
        ensureUserExists(authorId);

        Post post = new Post(
                idGenerator.newId(),
                authorId,
                content,
                timeProvider.now(),
                sequenceGenerator.next()
        );

        postRepository.save(post);
        return post;
    }

    
    public void deletePost(String authorId, String postId) {
        ensureUserExists(authorId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));

        if (!post.getAuthorId().equals(authorId)) {
            throw new UnauthorizedPostAccessException(
                    "User " + authorId + " is not allowed to delete post " + postId
            );
        }

        post.markDeleted();
    }

    
    public Post getPost(String postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found: " + postId));
    }

    
    public List<Post> getRecentPostsByUser(String userId, int limit) {
        ensureUserExists(userId);
        return postRepository.findRecentPostsByAuthor(userId, limit);
    }

    
    public List<Post> getAllPostsByUser(String userId) {
        ensureUserExists(userId);
        return postRepository.findAllPostsByAuthor(userId);
    }

    private void ensureUserExists(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found: " + userId);
        }
    }
}
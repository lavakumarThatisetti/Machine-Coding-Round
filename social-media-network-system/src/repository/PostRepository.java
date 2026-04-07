package repository;

import model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    void save(Post post);

    Optional<Post> findById(String postId);

    List<Post> findRecentPostsByAuthor(String authorId, int limit);

    List<Post> findAllPostsByAuthor(String authorId);

}

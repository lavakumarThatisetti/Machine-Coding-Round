package model;

import java.time.Instant;
import java.util.Objects;

public final class Post {
    private final String id;
    private final String authorId;
    private final String content;
    private final Instant createdAt;
    private final long sequence;

    private volatile PostStatus status;

    public Post(String id, String authorId, String content, Instant createdAt, long sequence) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Post id cannot be null or blank");
        }
        if (authorId == null || authorId.isBlank()) {
            throw new IllegalArgumentException("Author id cannot be null or blank");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Post content cannot be null or blank");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Post createdAt cannot be null");
        }

        this.id = id;
        this.authorId = authorId;
        this.content = content;
        this.createdAt = createdAt;
        this.sequence = sequence;
        this.status = PostStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getSequence() {
        return sequence;
    }

    public PostStatus getStatus() {
        return status;
    }

    public boolean isActive() {
        return status == PostStatus.ACTIVE;
    }

    public boolean isDeleted() {
        return status == PostStatus.DELETED;
    }

    public synchronized void markDeleted() {
        if (status == PostStatus.DELETED) {
            return;
        }
        this.status = PostStatus.DELETED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return id.equals(post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", authorId='" + authorId + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", sequence=" + sequence +
                ", status=" + status +
                '}';
    }
}

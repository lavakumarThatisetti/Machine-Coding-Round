import exception.UnauthorizedPostAccessException;
import model.FeedItem;
import model.Post;
import repository.FollowRepository;
import repository.PostRepository;
import repository.UserRepository;
import repository.impl.InMemoryFollowRepository;
import repository.impl.InMemoryPostRepository;
import repository.impl.InMemoryUserRepository;
import service.FeedService;
import service.PostService;
import service.SocialGraphService;
import service.UserService;
import service.strategy.FeedFetchStrategy;
import service.strategy.SimpleFeedFetchStrategy;
import util.AtomicSequenceGenerator;
import util.IdGenerator;
import util.SequenceGenerator;
import util.SystemTimeProvider;
import util.TimeProvider;
import util.UuidGenerator;

import java.util.List;

public class Main {

    private final UserService userService;
    private final SocialGraphService socialGraphService;
    private final PostService postService;
    private final FeedService feedService;

    public Main() {
        UserRepository userRepository = new InMemoryUserRepository();
        FollowRepository followRepository = new InMemoryFollowRepository();
        PostRepository postRepository = new InMemoryPostRepository();

        IdGenerator idGenerator = new UuidGenerator();
        TimeProvider timeProvider = new SystemTimeProvider();
        SequenceGenerator sequenceGenerator = new AtomicSequenceGenerator();

        this.userService = new UserService(userRepository);
        this.socialGraphService = new SocialGraphService(userRepository, followRepository);
        this.postService = new PostService(
                userRepository,
                postRepository,
                idGenerator,
                timeProvider,
                sequenceGenerator
        );

        FeedFetchStrategy feedFetchStrategy = new SimpleFeedFetchStrategy(postService);
        this.feedService = new FeedService(userService, socialGraphService, feedFetchStrategy);
    }

    public static void main(String[] args) {
        Main runner = new Main();
        runner.runAllTests();
    }

    private void runAllTests() {
        System.out.println("==================================");
        System.out.println("SOCIAL MEDIA MACHINE CODING TESTS");
        System.out.println("==================================");

        testUserCreation();
        testFollowAndUnfollow();
        testCreatePostsAndOwnFeed();
        testFeedContainsFollowingPosts();
        testDeletedPostsDoNotAppearInFeed();
        testUnfollowRemovesPostsFromFeed();
        testOnlyOwnerCanDeletePost();
        testFeedOrderingMostRecentFirst();
        testFeedReturnsOnlyTop10Posts();

        System.out.println("\n✅ All tests passed successfully.");
    }

    private void testUserCreation() {
        System.out.println("\nRunning testUserCreation...");

        userService.createUser("U1", "Alice");
        userService.createUser("U2", "Bob");
        userService.createUser("U3", "Charlie");

        assertTrue(userService.exists("U1"), "U1 should exist");
        assertTrue(userService.exists("U2"), "U2 should exist");
        assertTrue(userService.exists("U3"), "U3 should exist");

        System.out.println("Passed testUserCreation");
    }

    private void testFollowAndUnfollow() {
        System.out.println("\nRunning testFollowAndUnfollow...");

        socialGraphService.follow("U1", "U2");
        socialGraphService.follow("U1", "U3");

        assertTrue(socialGraphService.isFollowing("U1", "U2"), "U1 should follow U2");
        assertTrue(socialGraphService.isFollowing("U1", "U3"), "U1 should follow U3");

        socialGraphService.unfollow("U1", "U3");

        assertFalse(socialGraphService.isFollowing("U1", "U3"), "U1 should not follow U3 after unfollow");

        System.out.println("Passed testFollowAndUnfollow");
    }

    private void testCreatePostsAndOwnFeed() {
        System.out.println("\nRunning testCreatePostsAndOwnFeed...");

        Post p1 = postService.createPost("U1", "Alice post 1");
        Post p2 = postService.createPost("U1", "Alice post 2");

        List<FeedItem> feed = feedService.getFeed("U1");

        assertEquals(2, feed.size(), "U1 feed should contain 2 posts");
        assertEquals(p2.getId(), feed.get(0).getPostId(), "Most recent post should appear first");
        assertEquals(p1.getId(), feed.get(1).getPostId(), "Older post should appear second");

        System.out.println("Passed testCreatePostsAndOwnFeed");
    }

    private void testFeedContainsFollowingPosts() {
        System.out.println("\nRunning testFeedContainsFollowingPosts...");

        socialGraphService.follow("U1", "U2");

        Post bobPost1 = postService.createPost("U2", "Bob post 1");
        Post bobPost2 = postService.createPost("U2", "Bob post 2");

        List<FeedItem> feed = feedService.getFeed("U1");

        boolean containsBobPost1 = feed.stream().anyMatch(item -> item.getPostId().equals(bobPost1.getId()));
        boolean containsBobPost2 = feed.stream().anyMatch(item -> item.getPostId().equals(bobPost2.getId()));

        assertTrue(containsBobPost1, "Feed should contain Bob post 1");
        assertTrue(containsBobPost2, "Feed should contain Bob post 2");

        System.out.println("Passed testFeedContainsFollowingPosts");
    }

    private void testDeletedPostsDoNotAppearInFeed() {
        System.out.println("\nRunning testDeletedPostsDoNotAppearInFeed...");

        Post post = postService.createPost("U2", "Bob temporary post");
        postService.deletePost("U2", post.getId());

        List<FeedItem> feed = feedService.getFeed("U1");

        boolean containsDeletedPost = feed.stream()
                .anyMatch(item -> item.getPostId().equals(post.getId()));

        assertFalse(containsDeletedPost, "Deleted post should not appear in feed");

        System.out.println("Passed testDeletedPostsDoNotAppearInFeed");
    }

    private void testUnfollowRemovesPostsFromFeed() {
        System.out.println("\nRunning testUnfollowRemovesPostsFromFeed...");

        Post charliePost = postService.createPost("U3", "Charlie visible post");
        socialGraphService.follow("U1", "U3");

        List<FeedItem> feedBeforeUnfollow = feedService.getFeed("U1");
        boolean presentBefore = feedBeforeUnfollow.stream()
                .anyMatch(item -> item.getPostId().equals(charliePost.getId()));
        assertTrue(presentBefore, "Charlie post should appear before unfollow");

        socialGraphService.unfollow("U1", "U3");

        List<FeedItem> feedAfterUnfollow = feedService.getFeed("U1");
        boolean presentAfter = feedAfterUnfollow.stream()
                .anyMatch(item -> item.getPostId().equals(charliePost.getId()));
        assertFalse(presentAfter, "Charlie post should not appear after unfollow");

        System.out.println("Passed testUnfollowRemovesPostsFromFeed");
    }

    private void testOnlyOwnerCanDeletePost() {
        System.out.println("\nRunning testOnlyOwnerCanDeletePost...");

        Post post = postService.createPost("U2", "Bob protected post");

        assertThrows(
                UnauthorizedPostAccessException.class,
                () -> postService.deletePost("U1", post.getId()),
                "Non-owner should not be able to delete someone else's post"
        );

        System.out.println("Passed testOnlyOwnerCanDeletePost");
    }

    private void testFeedOrderingMostRecentFirst() {
        System.out.println("\nRunning testFeedOrderingMostRecentFirst...");

        Post aliceLatest = postService.createPost("U1", "Alice latest");
        Post bobLatest = postService.createPost("U2", "Bob latest after Alice");

        List<FeedItem> feed = feedService.getFeed("U1");

        assertEquals(bobLatest.getId(), feed.getFirst().getPostId(),
                "Latest post overall should be first in feed");

        boolean aliceExists = feed.stream().anyMatch(item -> item.getPostId().equals(aliceLatest.getId()));
        assertTrue(aliceExists, "Alice latest post should still be present in feed");

        System.out.println("Passed testFeedOrderingMostRecentFirst");
    }

    private void testFeedReturnsOnlyTop10Posts() {
        System.out.println("\nRunning testFeedReturnsOnlyTop10Posts...");

        userService.createUser("U4", "David");
        socialGraphService.follow("U1", "U4");

        for (int i = 1; i <= 15; i++) {
            postService.createPost("U4", "David post " + i);
        }

        List<FeedItem> feed = feedService.getFeed("U1");

        assertEquals(10, feed.size(), "Feed should return only top 10 posts");

        System.out.println("Passed testFeedReturnsOnlyTop10Posts");
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    private void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        }
        throw new AssertionError(
                message + " | expected=" + expected + ", actual=" + actual
        );
    }

    private <T extends Throwable> void assertThrows(Class<T> expectedException,
                                                    Runnable runnable,
                                                    String message) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            if (expectedException.isInstance(ex)) {
                return;
            }
            throw new AssertionError(
                    message + " | expected exception=" + expectedException.getSimpleName()
                            + ", but got=" + ex.getClass().getSimpleName()
            );
        }
        throw new AssertionError(
                message + " | expected exception=" + expectedException.getSimpleName()
                        + ", but nothing was thrown"
        );
    }
}
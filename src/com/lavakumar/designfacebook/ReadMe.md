Design Facebook

Question Description

Design a simplified version of Facebook where users can create/delete posts, follow/unfollow another user and are able to see the most recent posts in the user's news feed. Following methods to be implemented:



1) createPost(userId, postId): Compose a new post.

2) getNewsFeed(userId): Retrieve the 10 most recent post ids in the user's news feed. Each item in the news feed must be posted by users who the user followed or by the user herself (Order -> most to least recent)

3) follow(followerId, followeeId): Follower follows a followee.

4) unfollow(followerId, followeeId): Follower unfollows a followee.

5) deletePost(userId, postId): Delete an existing post.

6) getNewsFeedPaginated(userId, pageNumber): Retrieve the most recent post ids in the user's news feed in a paginated manner. Each item in the news feed must be posted by users who the user followed or by the user herself (Order -> most to least recent) Assume pageSize= 2.



Evaluation points :

1) Test cases passed

2) Code structuring and cleanliness

3) Scale and concurrency
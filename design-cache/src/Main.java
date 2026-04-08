import distributed.CacheNode;
import distributed.DistributedCache;
import distributed.HashRing;
import eviction.LFUEvictionPolicy;
import eviction.LRUEvictionPolicy;
import service.ActiveExpiryScheduler;
import segment.SegmentedCache;

import java.time.Duration;

public class Main {

    public static void main(String[] args) throws Exception {
        testLocalLRU();
        testLocalLFU();
        testExpiration();
        testDistributedCache();
    }

    private static void testLocalLRU() {
        System.out.println("==================================");
        System.out.println("TEST 1: Local Segmented LRU Cache");
        System.out.println("==================================");

        SegmentedCache<Integer, String> cache =
                new SegmentedCache<>(4, 2, LRUEvictionPolicy::new);

        cache.put(1, "A", Duration.ofSeconds(30));
        cache.put(2, "B", Duration.ofSeconds(30));
        cache.put(3, "C", Duration.ofSeconds(30));
        cache.put(4, "D", Duration.ofSeconds(30));

        System.out.println("get(1) = " + cache.get(1));
        System.out.println("get(2) = " + cache.get(2));

        cache.put(5, "E", Duration.ofSeconds(30));

        System.out.println("size = " + cache.size());
        System.out.println("get(1) = " + cache.get(1));
        System.out.println("get(2) = " + cache.get(2));
        System.out.println("get(3) = " + cache.get(3));
        System.out.println("get(4) = " + cache.get(4));
        System.out.println("get(5) = " + cache.get(5));
        System.out.println();
    }

    private static void testLocalLFU() {
        System.out.println("==================================");
        System.out.println("TEST 2: Local Segmented LFU Cache");
        System.out.println("==================================");

        SegmentedCache<Integer, String> cache =
                new SegmentedCache<>(3, 1, LFUEvictionPolicy::new);

        cache.put(1, "A", Duration.ofSeconds(30));
        cache.put(2, "B", Duration.ofSeconds(30));
        cache.put(3, "C", Duration.ofSeconds(30));

        cache.get(1);
        cache.get(1);
        cache.get(2);

        cache.put(4, "D", Duration.ofSeconds(30));

        System.out.println("get(1) = " + cache.get(1)); // should survive
        System.out.println("get(2) = " + cache.get(2)); // likely survives
        System.out.println("get(3) = " + cache.get(3)); // likely evicted
        System.out.println("get(4) = " + cache.get(4));
        System.out.println();
    }

    private static void testExpiration() throws InterruptedException {
        System.out.println("==================================");
        System.out.println("TEST 3: TTL / Expiration");
        System.out.println("==================================");

        SegmentedCache<Integer, String> cache =
                new SegmentedCache<>(2, 1, LRUEvictionPolicy::new);

        ActiveExpiryScheduler scheduler = new ActiveExpiryScheduler();
        scheduler.start(cache, 10, 3, 100);

        cache.put(10, "short-lived", Duration.ofMillis(300));
        cache.put(20, "long-lived", Duration.ofSeconds(5));

        System.out.println("Immediately get(10) = " + cache.get(10));
        Thread.sleep(500);
        System.out.println("After 500ms get(10) = " + cache.get(10));
        System.out.println("After 500ms get(20) = " + cache.get(20));

        scheduler.shutdown();
        System.out.println();
    }

    private static void testDistributedCache() {
        System.out.println("==================================");
        System.out.println("TEST 4: Distributed Cache Skeleton");
        System.out.println("==================================");

        CacheNode<Integer, String> node1 = new CacheNode<>(
                "node-1",
                new SegmentedCache<>(4, 2, LRUEvictionPolicy::new)
        );

        CacheNode<Integer, String> node2 = new CacheNode<>(
                "node-2",
                new SegmentedCache<>(4, 2, LRUEvictionPolicy::new)
        );

        CacheNode<Integer, String> node3 = new CacheNode<>(
                "node-3",
                new SegmentedCache<>(4, 2, LRUEvictionPolicy::new)
        );

        HashRing<Integer, String> hashRing = new HashRing<>(10);
        hashRing.addNode(node1);
        hashRing.addNode(node2);
        hashRing.addNode(node3);

        DistributedCache<Integer, String> distributedCache = new DistributedCache<>(hashRing);

        distributedCache.put(100, "User-100", Duration.ofSeconds(60));
        distributedCache.put(200, "User-200", Duration.ofSeconds(60));
        distributedCache.put(300, "User-300", Duration.ofSeconds(60));
        distributedCache.put(400, "User-400", Duration.ofSeconds(60));

        System.out.println("get(100) = " + distributedCache.get(100));
        System.out.println("get(200) = " + distributedCache.get(200));
        System.out.println("get(300) = " + distributedCache.get(300));
        System.out.println("get(400) = " + distributedCache.get(400));

        System.out.println("node1 = " + node1);
        System.out.println("node2 = " + node2);
        System.out.println("node3 = " + node3);
        System.out.println("distributed size = " + distributedCache.size());
        System.out.println();
    }
}
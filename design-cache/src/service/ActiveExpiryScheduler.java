package service;

import segment.SegmentedCache;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ActiveExpiryScheduler {

    private final ScheduledExecutorService executorService;

    public ActiveExpiryScheduler() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public <K, V> void start(SegmentedCache<K, V> cache,
                             int sampleSize,
                             int maxRounds,
                             long periodMillis) {
        executorService.scheduleAtFixedRate(
                () -> {
                    try {
                        cache.cleanUpAllSegments(sampleSize, maxRounds);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                periodMillis,
                periodMillis,
                TimeUnit.MILLISECONDS
        );
    }

    public void shutdown() {
        executorService.shutdown();
    }
}

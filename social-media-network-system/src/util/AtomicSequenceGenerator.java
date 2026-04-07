package util;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicSequenceGenerator implements SequenceGenerator {
    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public long next() {
        return counter.incrementAndGet();
    }
}

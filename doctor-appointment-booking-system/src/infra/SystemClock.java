package infra;

import java.time.Instant;
import java.time.LocalDateTime;

public class SystemClock {
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    public Instant instant() {
        return Instant.now();
    }
}
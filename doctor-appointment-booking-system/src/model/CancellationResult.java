package model;

public class CancellationResult {
    private final CancellationStatus status;
    private final String message;

    public CancellationResult(CancellationStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public static CancellationResult cancelled(String message) {
        return new CancellationResult(CancellationStatus.CANCELLED, message);
    }

    public static CancellationResult alreadyCancelled(String message) {
        return new CancellationResult(CancellationStatus.ALREADY_CANCELLED, message);
    }

    public static CancellationResult notFound(String message) {
        return new CancellationResult(CancellationStatus.NOT_FOUND, message);
    }

    public CancellationStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

package model;

public class MoveResult {
    private final boolean success;
    private final String message;

    private MoveResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static MoveResult success(String message) {
        return new MoveResult(true, message);
    }

    public static MoveResult failure(String message) {
        return new MoveResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}

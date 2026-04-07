package exception;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
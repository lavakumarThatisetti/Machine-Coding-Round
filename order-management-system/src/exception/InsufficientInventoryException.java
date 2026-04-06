package exception;

public class InsufficientInventoryException extends RuntimeException{
    public InsufficientInventoryException(String productId) {
        System.out.println("Insufficient Inventory Exception for the product: "+ productId);
    }
}

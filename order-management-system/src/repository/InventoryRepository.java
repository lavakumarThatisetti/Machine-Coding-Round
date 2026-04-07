package repository;

import model.ProductInventory;

import java.util.concurrent.ConcurrentHashMap;

public class InventoryRepository {
    ConcurrentHashMap<String, ProductInventory> inventoryMap;

    public InventoryRepository() {
        this.inventoryMap = new ConcurrentHashMap<>();
    }


    public void save(ProductInventory productInventory) {
        inventoryMap.put(productInventory.getProductId(), productInventory);
    }


    public ProductInventory get(String productId) {
        return inventoryMap.get(productId);
    }
}

package service;

import model.ProductInventory;
import repository.InventoryRepository;

import java.util.concurrent.locks.ReentrantLock;

public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final LockManager lockManager;

    public InventoryService(InventoryRepository inventoryRepository, LockManager lockManager) {
        this.inventoryRepository = inventoryRepository;
        this.lockManager = lockManager;
    }

    public void createProduct(String productId, int initialStock) {
        inventoryRepository.save(new ProductInventory(productId, initialStock));
    }

    public int getAvailableInventory(String productId) {
        ProductInventory inventory = inventoryRepository.get(productId);
        return inventory.getAvailableStock();
    }

    public void reserve(String productId, int qty) {
        ReentrantLock lock = lockManager.getLock(productId);
        lock.lock();
        try {
            ProductInventory inventory = inventoryRepository.get(productId);
            inventory.reserve(qty);
        } finally {
            lock.unlock();
        }
    }

    public void confirm(String productId, int qty) {
        ReentrantLock lock = lockManager.getLock(productId);
        lock.lock();
        try {
            ProductInventory inventory = inventoryRepository.get(productId);
            inventory.confirm(qty);
        } finally {
            lock.unlock();
        }
    }

    public void release(String productId, int qty) {
        ReentrantLock lock = lockManager.getLock(productId);
        lock.lock();
        try {
            ProductInventory inventory = inventoryRepository.get(productId);
            inventory.release(qty);
        } finally {
            lock.unlock();
        }
    }

    public String getInventory(String productId) {
        return inventoryRepository.get(productId).getProductId();
    }
}
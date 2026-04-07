package service;

import entity.Order;
import entity.UserAccount;
import lock.LockManager;
import repository.UserRepository;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

public class SettlementService {
    private final UserRepository userRepository;
    private final LockManager lockManager;

    public SettlementService(UserRepository userRepository, LockManager lockManager) {
        this.userRepository = userRepository;
        this.lockManager = lockManager;
    }

    public void settle(Order buyOrder, Order sellOrder, int quantity, BigDecimal executionPrice) {
        UserAccount buyer = userRepository.findById(buyOrder.getUserId())
                .orElseThrow(() -> new IllegalStateException("Buyer not found"));
        UserAccount seller = userRepository.findById(sellOrder.getUserId())
                .orElseThrow(() -> new IllegalStateException("Seller not found"));

        // deterministic order:
        //       smaller userId first
        //       larger userId second
        String firstUserId = buyer.getUserId().compareTo(seller.getUserId()) < 0
                ? buyer.getUserId() : seller.getUserId();
        String secondUserId = buyer.getUserId().compareTo(seller.getUserId()) < 0
                ? seller.getUserId() : buyer.getUserId();

        ReentrantLock firstLock = lockManager.getUserLock(firstUserId);
        ReentrantLock secondLock = lockManager.getUserLock(secondUserId);

        firstLock.lock();
        secondLock.lock();
        try {
            BigDecimal buyerReserved = buyOrder.getPrice().multiply(BigDecimal.valueOf(quantity));
            BigDecimal actualCost = executionPrice.multiply(BigDecimal.valueOf(quantity));
            BigDecimal refund = buyerReserved.subtract(actualCost);

            buyer.consumeReservedCash(buyerReserved);
            if (refund.compareTo(BigDecimal.ZERO) > 0) {
                buyer.addCash(refund);
            }
            buyer.addHolding(buyOrder.getStockId(), quantity);

            seller.consumeReservedHolding(sellOrder.getStockId(), quantity);
            seller.addCash(actualCost);

        } finally {
            secondLock.unlock();
            firstLock.unlock();
        }
    }
}
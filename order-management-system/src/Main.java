//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import repository.InventoryRepository;
import repository.OrderRepository;
import repository.ReservationRepository;
import service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws Exception {
        InventoryRepository inventoryRepository = new InventoryRepository();
        ReservationRepository reservationRepository = new ReservationRepository();
        OrderRepository orderRepository = new OrderRepository();
        LockManager lockManager = new LockManager();

        InventoryService inventoryService = new InventoryService(inventoryRepository, lockManager);
        ReservationService reservationService = new ReservationService(reservationRepository, inventoryService, lockManager);
        ExpiryScheduler expiryScheduler = new ExpiryScheduler(2, reservationService, orderRepository);
        OrderService orderService = new OrderService(orderRepository, reservationService, expiryScheduler);

        String productId = "IPHONE-15";

        System.out.println("========== CREATE PRODUCT ==========");
        inventoryService.createProduct(productId, 5);
        printInventory(inventoryService, productId);

        System.out.println("\n========== CASE 1: NORMAL RESERVE + CONFIRM ==========");
        String order1 = orderService.initiateOrder(productId, 2, 5000);
        System.out.println("Order1 created: " + order1);
        printOrder(orderService, order1);
        printInventory(inventoryService, productId);

        boolean confirmed = orderService.confirmOrder(order1);
        System.out.println("Order1 confirm result = " + confirmed);
        printOrder(orderService, order1);
        printInventory(inventoryService, productId);

        System.out.println("\n========== CASE 2: RESERVE + AUTO EXPIRE ==========");
        String order2 = orderService.initiateOrder(productId, 2, 3000);
        System.out.println("Order2 created: " + order2);
        printOrder(orderService, order2);
        printInventory(inventoryService, productId);

        System.out.println("Sleeping 4 seconds to allow expiry...");
        Thread.sleep(4000);

        printOrder(orderService, order2);
        printInventory(inventoryService, productId);

        System.out.println("\n========== CASE 3: CONCURRENT RESERVATION, NO OVERSELL ==========");
        String product2 = "PS5";
        inventoryService.createProduct(product2, 3);
        printInventory(inventoryService, product2);

        ExecutorService testExecutor = Executors.newFixedThreadPool(5);
        CountDownLatch startLatch = new CountDownLatch(1);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            final int userId = i;
            futures.add(testExecutor.submit(() -> {
                startLatch.await();
                try {
                    String orderId = orderService.initiateOrder(product2, 1, 10000);
                    return "User-" + userId + " SUCCESS, orderId=" + orderId;
                } catch (Exception e) {
                    return "User-" + userId + " FAILED, reason=" + e.getMessage();
                }
            }));
        }

        startLatch.countDown();

        for (Future<String> future : futures) {
            System.out.println(future.get());
        }

        printInventory(inventoryService, product2);

        System.out.println("\n========== CASE 4: CONFIRM VS EXPIRY RACE ==========");
        String product3 = "MACBOOK";
        inventoryService.createProduct(product3, 1);
        String raceOrderId = orderService.initiateOrder(product3, 1, 2000);

        System.out.println("Race order created: " + raceOrderId);
        printOrder(orderService, raceOrderId);
        printInventory(inventoryService, product3);

        Thread.sleep(1900);

        ExecutorService raceExecutor = Executors.newFixedThreadPool(2);
        Future<Boolean> confirmFuture = raceExecutor.submit(() -> {
            try {
                return orderService.confirmOrder(raceOrderId);
            } catch (Exception e) {
                return false;
            }
        });

        Thread.sleep(300);

        Boolean raceConfirmResult = confirmFuture.get();
        System.out.println("Confirm race result = " + raceConfirmResult);

        Thread.sleep(1000);

        printOrder(orderService, raceOrderId);
        printInventory(inventoryService, product3);


        System.out.println("CASE: TEST OVERSELL");
        testOversell(orderService, inventoryService);


        System.out.println("CASE: confirm vs cancel race");
        String productIdShoe = "SHOE";
        System.out.println("========== CREATE PRODUCT ==========");
        inventoryService.createProduct(productIdShoe, 5);
        String order4 = orderService.initiateOrder(productId, 2, 5000);
        System.out.println("Order4 created: " + order4);
        testConfirmVsCancel(orderService, order4);

        raceExecutor.shutdown();
        testExecutor.shutdown();
        expiryScheduler.shutdown();
    }

    private static void printInventory(InventoryService inventoryService, String productId) {
        System.out.println("Inventory => " + inventoryService.getInventory(productId));
    }

    private static void printOrder(OrderService orderService, String orderId) {
        System.out.println("Order => " + orderService.getOrder(orderId));
    }

    public static void testOversell(OrderService orderService, InventoryService inventoryService) throws Exception {
        String productId = "TEST-PRODUCT";
        inventoryService.createProduct(productId, 3);

        int users = 10;
        CountDownLatch readyLatch = new CountDownLatch(users);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(users);

        ExecutorService executor = Executors.newFixedThreadPool(users);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 1; i <= users; i++) {
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    try {
                        orderService.initiateOrder(productId, 1, 10000);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();

        System.out.println("Success = " + successCount.get());
        System.out.println("Failure = " + failureCount.get());
        System.out.println("Available = " + inventoryService.getAvailableInventory(productId));
        System.out.println("Inventory = " + inventoryService.getInventory(productId));

        executor.shutdown();
    }

    public static void testConfirmVsCancel(OrderService orderService, String orderId) throws Exception {
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<Boolean> confirmFuture = executor.submit(() -> {
            readyLatch.countDown();
            startLatch.await();
            return orderService.confirmOrder(orderId);
        });

        Future<Boolean> cancelFuture = executor.submit(() -> {
            readyLatch.countDown();
            startLatch.await();
            return orderService.cancelOrder(orderId);
        });

        readyLatch.await();
        startLatch.countDown();

        Boolean confirmResult = confirmFuture.get();
        Boolean cancelResult = cancelFuture.get();

        System.out.println("confirmResult = " + confirmResult);
        System.out.println("cancelResult = " + cancelResult);

        executor.shutdown();
    }
}
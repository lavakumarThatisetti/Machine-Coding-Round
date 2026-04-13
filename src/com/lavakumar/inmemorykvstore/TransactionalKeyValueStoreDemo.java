package com.lavakumar.inmemorykvstore;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TransactionalKeyValueStoreDemo {

    public static void main(String[] args) {
        TransactionalKeyValueStore store = new TransactionalKeyValueStore();

        testBasicSetGetDelete(store);
        testSingleTransaction(store);
        testRollback(store);
        testNestedTransactions(store);
        testNestedRollback(store);
        testDeleteInsideTransaction(store);
        testCommitWithoutTransaction(store);
        testRollbackWithoutTransaction(store);

        System.out.println();
        System.out.println("All demo tests passed.");
    }

    private static void testBasicSetGetDelete(TransactionalKeyValueStore store) {
        System.out.println("========== BASIC SET / GET / DELETE ==========");

        store.set("name", "lava");
        assertOptionalEquals("lava", store.get("name"), "name should be lava");

        store.delete("name");
        assertOptionalEmpty(store.get("name"), "name should be deleted");

        System.out.println(store);
        System.out.println();
    }

    private static void testSingleTransaction(TransactionalKeyValueStore store) {
        System.out.println("========== SINGLE TRANSACTION ==========");

        store.set("a", "10");
        store.begin();

        store.set("a", "20");
        store.set("b", "30");

        assertOptionalEquals("20", store.get("a"), "inside tx, a should be 20");
        assertOptionalEquals("30", store.get("b"), "inside tx, b should be 30");
        assertEquals(1, store.getActiveTransactionDepth(), "tx depth should be 1");

        store.commit();

        assertOptionalEquals("20", store.get("a"), "after commit, a should be 20");
        assertOptionalEquals("30", store.get("b"), "after commit, b should be 30");
        assertEquals(0, store.getActiveTransactionDepth(), "tx depth should be 0");

        System.out.println(store);
        System.out.println();
    }

    private static void testRollback(TransactionalKeyValueStore store) {
        System.out.println("========== ROLLBACK ==========");

        store.set("x", "1");
        store.begin();
        store.set("x", "2");
        store.set("y", "3");

        assertOptionalEquals("2", store.get("x"), "inside tx, x should be 2");
        assertOptionalEquals("3", store.get("y"), "inside tx, y should be 3");

        store.rollback();

        assertOptionalEquals("1", store.get("x"), "after rollback, x should be restored to 1");
        assertOptionalEmpty(store.get("y"), "after rollback, y should not exist");

        System.out.println(store);
        System.out.println();
    }

    private static void testNestedTransactions(TransactionalKeyValueStore store) {
        System.out.println("========== NESTED TRANSACTIONS ==========");

        store.set("k1", "base");
        store.begin();                  // T1
        store.set("k1", "t1");

        store.begin();                  // T2
        store.set("k1", "t2");
        store.set("k2", "inner");

        assertOptionalEquals("t2", store.get("k1"), "inside T2, k1 should be t2");
        assertOptionalEquals("inner", store.get("k2"), "inside T2, k2 should be inner");
        assertEquals(2, store.getActiveTransactionDepth(), "tx depth should be 2");

        store.commit();                // T2 -> T1

        assertOptionalEquals("t2", store.get("k1"), "after inner commit, k1 should still be t2");
        assertOptionalEquals("inner", store.get("k2"), "after inner commit, k2 should still be inner");
        assertEquals(1, store.getActiveTransactionDepth(), "tx depth should be 1");

        store.commit();                // T1 -> base

        assertOptionalEquals("t2", store.get("k1"), "after outer commit, k1 should be t2");
        assertOptionalEquals("inner", store.get("k2"), "after outer commit, k2 should be inner");
        assertEquals(0, store.getActiveTransactionDepth(), "tx depth should be 0");

        System.out.println(store);
        System.out.println();
    }

    private static void testNestedRollback(TransactionalKeyValueStore store) {
        System.out.println("========== NESTED ROLLBACK ==========");

        store.set("p", "100");
        store.begin();                 // T1
        store.set("p", "200");

        store.begin();                 // T2
        store.set("p", "300");
        store.set("q", "400");

        assertOptionalEquals("300", store.get("p"), "inside T2, p should be 300");
        assertOptionalEquals("400", store.get("q"), "inside T2, q should be 400");

        store.rollback();              // discard T2

        assertOptionalEquals("200", store.get("p"), "after inner rollback, p should be 200");
        assertOptionalEmpty(store.get("q"), "after inner rollback, q should not exist");

        store.commit();                // apply T1

        assertOptionalEquals("200", store.get("p"), "after outer commit, p should be 200");
        assertOptionalEmpty(store.get("q"), "after outer commit, q should not exist");

        System.out.println(store);
        System.out.println();
    }

    private static void testDeleteInsideTransaction(TransactionalKeyValueStore store) {
        System.out.println("========== DELETE INSIDE TRANSACTION ==========");

        store.set("delKey", "alive");
        assertOptionalEquals("alive", store.get("delKey"), "delKey should exist before tx");

        store.begin();
        store.delete("delKey");
        assertOptionalEmpty(store.get("delKey"), "delKey should look deleted inside tx");

        store.rollback();
        assertOptionalEquals("alive", store.get("delKey"), "delKey should be restored after rollback");

        store.begin();
        store.delete("delKey");
        store.commit();
        assertOptionalEmpty(store.get("delKey"), "delKey should be deleted after commit");

        System.out.println(store);
        System.out.println();
    }

    private static void testCommitWithoutTransaction(TransactionalKeyValueStore store) {
        System.out.println("========== COMMIT WITHOUT TRANSACTION ==========");

        try {
            store.commit();
            throw new AssertionError("Expected exception for commit without transaction");
        } catch (NoActiveTransactionException expected) {
            System.out.println("Caught expected exception: " + expected.getMessage());
        }

        System.out.println();
    }

    private static void testRollbackWithoutTransaction(TransactionalKeyValueStore store) {
        System.out.println("========== ROLLBACK WITHOUT TRANSACTION ==========");

        try {
            store.rollback();
            throw new AssertionError("Expected exception for rollback without transaction");
        } catch (NoActiveTransactionException expected) {
            System.out.println("Caught expected exception: " + expected.getMessage());
        }

        System.out.println();
    }

    private static void assertOptionalEquals(String expected, Optional<String> actual, String message) {
        if (actual.isEmpty() || !Objects.equals(expected, actual.get())) {
            throw new AssertionError(message + " | expected=" + expected + ", actual=" + actual);
        }
    }

    private static void assertOptionalEmpty(Optional<String> actual, String message) {
        if (actual.isPresent()) {
            throw new AssertionError(message + " | expected empty, actual=" + actual.get());
        }
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " | expected=" + expected + ", actual=" + actual);
        }
    }

    // =========================================================
    // Core Store
    // =========================================================

    static class TransactionalKeyValueStore {
        private final Map<String, String> baseStore = new HashMap<>();
        private final Deque<TransactionContext> transactionStack = new ArrayDeque<>();
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        public Optional<String> get(String key) {
            validateKey(key);

            lock.readLock().lock();
            try {
                for (TransactionContext context : transactionStack) {
                    Optional<Mutation> mutation = context.getMutation(key);
                    if (mutation.isPresent()) {
                        return mutation.get().resolveValue();
                    }
                }

                return Optional.ofNullable(baseStore.get(key));
            } finally {
                lock.readLock().unlock();
            }
        }

        public void set(String key, String value) {
            validateKey(key);
            validateValue(value);

            lock.writeLock().lock();
            try {
                if (!hasActiveTransaction()) {
                    baseStore.put(key, value);
                    return;
                }

                currentTransaction().putSet(key, value);
            } finally {
                lock.writeLock().unlock();
            }
        }

        public void delete(String key) {
            validateKey(key);

            lock.writeLock().lock();
            try {
                if (!hasActiveTransaction()) {
                    baseStore.remove(key);
                    return;
                }

                currentTransaction().putDelete(key);
            } finally {
                lock.writeLock().unlock();
            }
        }

        public void begin() {
            lock.writeLock().lock();
            try {
                transactionStack.push(new TransactionContext());
            } finally {
                lock.writeLock().unlock();
            }
        }

        public void commit() {
            lock.writeLock().lock();
            try {
                TransactionContext current = popRequiredTransaction();

                if (hasActiveTransaction()) {
                    TransactionContext parent = currentTransaction();
                    mergeIntoParent(parent, current);
                } else {
                    applyToBaseStore(current);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        public void rollback() {
            lock.writeLock().lock();
            try {
                popRequiredTransaction();
            } finally {
                lock.writeLock().unlock();
            }
        }

        public int getActiveTransactionDepth() {
            lock.readLock().lock();
            try {
                return transactionStack.size();
            } finally {
                lock.readLock().unlock();
            }
        }

        public Map<String, String> getCommittedSnapshot() {
            lock.readLock().lock();
            try {
                return Collections.unmodifiableMap(new LinkedHashMap<>(baseStore));
            } finally {
                lock.readLock().unlock();
            }
        }

        public Map<String, String> getEffectiveSnapshot() {
            lock.readLock().lock();
            try {
                Map<String, String> snapshot = new LinkedHashMap<>(baseStore);

                List<TransactionContext> contexts = new ArrayList<>(transactionStack);
                Collections.reverse(contexts); // apply oldest first, newest last

                for (TransactionContext context : contexts) {
                    for (Map.Entry<String, Mutation> entry : context.getAllMutations().entrySet()) {
                        Mutation mutation = entry.getValue();
                        if (mutation.type() == MutationType.SET) {
                            snapshot.put(entry.getKey(), mutation.value());
                        } else {
                            snapshot.remove(entry.getKey());
                        }
                    }
                }

                return Collections.unmodifiableMap(snapshot);
            } finally {
                lock.readLock().unlock();
            }
        }

        private boolean hasActiveTransaction() {
            return !transactionStack.isEmpty();
        }

        private TransactionContext currentTransaction() {
            TransactionContext context = transactionStack.peek();
            if (context == null) {
                throw new NoActiveTransactionException("No active transaction");
            }
            return context;
        }

        private TransactionContext popRequiredTransaction() {
            TransactionContext context = transactionStack.poll();
            if (context == null) {
                throw new NoActiveTransactionException("No active transaction to close");
            }
            return context;
        }

        private void mergeIntoParent(TransactionContext parent, TransactionContext child) {
            for (Map.Entry<String, Mutation> entry : child.getAllMutations().entrySet()) {
                parent.putMutation(entry.getKey(), entry.getValue());
            }
        }

        private void applyToBaseStore(TransactionContext context) {
            for (Map.Entry<String, Mutation> entry : context.getAllMutations().entrySet()) {
                Mutation mutation = entry.getValue();
                if (mutation.type() == MutationType.SET) {
                    baseStore.put(entry.getKey(), mutation.value());
                } else {
                    baseStore.remove(entry.getKey());
                }
            }
        }

        private void validateKey(String key) {
            if (key == null || key.isBlank()) {
                throw new IllegalArgumentException("key must not be blank");
            }
        }

        private void validateValue(String value) {
            if (value == null) {
                throw new IllegalArgumentException("value must not be null");
            }
        }

        @Override
        public String toString() {
            return "TransactionalKeyValueStore{" +
                    "committed=" + getCommittedSnapshot() +
                    ", effective=" + getEffectiveSnapshot() +
                    ", activeTransactionDepth=" + getActiveTransactionDepth() +
                    '}';
        }
    }

    // =========================================================
    // Transaction Internals
    // =========================================================

    static class TransactionContext {
        private final Map<String, Mutation> mutations = new LinkedHashMap<>();

        public Optional<Mutation> getMutation(String key) {
            return Optional.ofNullable(mutations.get(key));
        }

        public void putSet(String key, String value) {
            mutations.put(key, Mutation.set(value));
        }

        public void putDelete(String key) {
            mutations.put(key, Mutation.delete());
        }

        public void putMutation(String key, Mutation mutation) {
            mutations.put(key, mutation);
        }

        public Map<String, Mutation> getAllMutations() {
            return mutations;
        }
    }

    record Mutation(MutationType type, String value) {
        static Mutation set(String value) {
            return new Mutation(MutationType.SET, value);
        }

        static Mutation delete() {
            return new Mutation(MutationType.DELETE, null);
        }

        Optional<String> resolveValue() {
            return type == MutationType.SET ? Optional.of(value) : Optional.empty();
        }
    }

    enum MutationType {
        SET,
        DELETE
    }

    static class NoActiveTransactionException extends RuntimeException {
        NoActiveTransactionException(String message) {
            super(message);
        }
    }
}

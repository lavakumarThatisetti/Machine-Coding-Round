package model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class Group {
    private final String id;
    private final String name;
    private final Set<String> memberIds;
    private final Set<String> expenseIds;

    public Group(String id, String name, Set<String> initialMemberIds) {
        this.id = requireNonBlank(id, "Group id cannot be blank");
        this.name = requireNonBlank(name, "Group name cannot be blank");
        this.memberIds = new LinkedHashSet<>(Objects.requireNonNull(initialMemberIds, "Members cannot be null"));
        this.expenseIds = new LinkedHashSet<>();

        if (this.memberIds.isEmpty()) {
            throw new IllegalArgumentException("Group must have at least one member");
        }
    }

    public synchronized void addMember(String userId) {
        memberIds.add(requireNonBlank(userId, "User id cannot be blank"));
    }

    public synchronized boolean hasMember(String userId) {
        return memberIds.contains(userId);
    }

    public synchronized Set<String> getMemberIds() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(memberIds));
    }

    public synchronized void addExpense(String expenseId) {
        expenseIds.add(requireNonBlank(expenseId, "Expense id cannot be blank"));
    }

    public synchronized Set<String> getExpenseIds() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(expenseIds));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
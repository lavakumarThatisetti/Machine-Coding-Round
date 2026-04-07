package model;

import java.util.Objects;

public final class UserPair {
    private final String firstUserId;
    private final String secondUserId;

    public UserPair(String userA, String userB) {
        if (userA == null || userA.isBlank() || userB == null || userB.isBlank()) {
            throw new IllegalArgumentException("User ids cannot be blank");
        }
        if (userA.equals(userB)) {
            throw new IllegalArgumentException("Pair users must be different");
        }

        if (userA.compareTo(userB) < 0) {
            this.firstUserId = userA;
            this.secondUserId = userB;
        } else {
            this.firstUserId = userB;
            this.secondUserId = userA;
        }
    }

    public String getFirstUserId() {
        return firstUserId;
    }

    public String getSecondUserId() {
        return secondUserId;
    }

    public boolean contains(String userId) {
        return firstUserId.equals(userId) || secondUserId.equals(userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPair userPair)) return false;
        return firstUserId.equals(userPair.firstUserId) &&
                secondUserId.equals(userPair.secondUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstUserId, secondUserId);
    }

    @Override
    public String toString() {
        return "UserPair{" +
                "firstUserId='" + firstUserId + '\'' +
                ", secondUserId='" + secondUserId + '\'' +
                '}';
    }
}
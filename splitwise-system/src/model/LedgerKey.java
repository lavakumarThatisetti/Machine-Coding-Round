package model;

import java.util.Objects;

/**
 * @param groupId nullable
 */
public record LedgerKey(String groupId, UserPair userPair) {
    public LedgerKey(String groupId, UserPair userPair) {
        this.groupId = groupId;
        this.userPair = Objects.requireNonNull(userPair, "UserPair cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LedgerKey ledgerKey)) return false;
        return Objects.equals(groupId, ledgerKey.groupId) &&
                userPair.equals(ledgerKey.userPair);
    }

    @Override
    public String toString() {
        return "LedgerKey{" +
                "groupId='" + groupId + '\'' +
                ", userPair=" + userPair +
                '}';
    }
}

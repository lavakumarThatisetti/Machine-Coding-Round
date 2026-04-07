package model;

import java.util.Objects;

public record User(String id, String name, String email, String mobile) {
    public User(String id, String name, String email, String mobile) {
        this.id = requireNonBlank(id, "User id cannot be blank");
        this.name = requireNonBlank(name, "User name cannot be blank");
        this.email = requireNonBlank(email, "Email cannot be blank");
        this.mobile = requireNonBlank(mobile, "Mobile cannot be blank");
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
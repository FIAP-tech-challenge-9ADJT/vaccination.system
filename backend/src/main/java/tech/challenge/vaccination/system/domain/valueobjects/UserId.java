package tech.challenge.vaccination.system.domain.valueobjects;

import java.util.Objects;

public record UserId(Long value) {
    
    public UserId {
        Objects.requireNonNull(value, "User ID cannot be null");
        if (value <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
    }
    
    public static UserId of(Long value) {
        return new UserId(value);
    }
}
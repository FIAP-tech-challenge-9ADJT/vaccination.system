package tech.challenge.vaccination.system.domain.valueobjects;

import java.util.Objects;

public record Password(String value) {
    
    public Password {
        Objects.requireNonNull(value, "Password cannot be null");
        if (value.length() < 6) {
            throw new IllegalArgumentException("Password must have at least 6 characters");
        }
    }
    
    public static Password of(String value) {
        return new Password(value);
    }
    
    public static Password encoded(String encodedValue) {
        Objects.requireNonNull(encodedValue, "Encoded password cannot be null");
        return new Password(encodedValue);
    }
}
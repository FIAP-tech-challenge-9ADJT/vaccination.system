package tech.challenge.vaccination.system.domain.valueobjects;

import java.util.Objects;

public record Login(String value) {
    
    public Login {
        Objects.requireNonNull(value, "Login cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("Login cannot be empty");
        }
        if (value.length() < 4 || value.length() > 50) {
            throw new IllegalArgumentException("Login must have between 4 and 50 characters");
        }
    }
    
    public static Login of(String value) {
        return new Login(value);
    }
}
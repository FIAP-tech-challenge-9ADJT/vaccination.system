package tech.challenge.vaccination.system.domain.valueobjects;

import java.util.Objects;

public record Name(String value) {
    
    public Name {
        Objects.requireNonNull(value, "Name cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Name must have at most 100 characters");
        }
    }
    
    public static Name of(String value) {
        return new Name(value.trim());
    }
}
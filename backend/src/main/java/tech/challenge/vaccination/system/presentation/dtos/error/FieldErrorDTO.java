package tech.challenge.vaccination.system.presentation.dtos.error;

public record FieldErrorDTO(
        String field,
        Object rejectedValue,
        String message
) {
    public static FieldErrorDTO of(String field, Object rejectedValue, String message) {
        return new FieldErrorDTO(field, rejectedValue, message);
    }
}
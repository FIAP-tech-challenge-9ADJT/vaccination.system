package tech.challenge.vaccination.system.domain.exceptions;

public class AccessDeniedException extends DomainException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
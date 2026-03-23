package tech.challenge.vaccination.system.domain.exceptions;

public class UserAlreadyExistsException extends DomainException {
    
    public UserAlreadyExistsException(String field, String value) {
        super("User already exists with " + field + ": " + value);
    }
}
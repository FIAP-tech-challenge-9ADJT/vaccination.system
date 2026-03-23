package tech.challenge.vaccination.system.domain.exceptions;

public class InvalidCredentialsException extends DomainException {
    
    public InvalidCredentialsException() {
        super("Invalid credentials provided");
    }
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
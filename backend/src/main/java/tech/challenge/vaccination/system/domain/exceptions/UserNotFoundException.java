package tech.challenge.vaccination.system.domain.exceptions;

import tech.challenge.vaccination.system.domain.valueobjects.UserId;

public class UserNotFoundException extends DomainException {
    
    public UserNotFoundException(UserId userId) {
        super("User not found with ID: " + userId.value());
    }
    
    public UserNotFoundException(String login) {
        super("User not found with login: " + login);
    }
}
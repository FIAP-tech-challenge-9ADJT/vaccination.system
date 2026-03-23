package tech.challenge.vaccination.system.domain.usecases.user;

import tech.challenge.vaccination.system.domain.entities.User;
import tech.challenge.vaccination.system.domain.exceptions.UserNotFoundException;
import tech.challenge.vaccination.system.domain.repositories.UserRepository;
import tech.challenge.vaccination.system.domain.valueobjects.UserId;

public class FindUserUseCase {
    
    private final UserRepository userRepository;
    
    public FindUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User execute(UserId userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
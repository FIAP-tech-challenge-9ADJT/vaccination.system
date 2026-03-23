package tech.challenge.vaccination.system.domain.usecases.user;

import tech.challenge.vaccination.system.domain.entities.User;
import tech.challenge.vaccination.system.domain.exceptions.UserNotFoundException;
import tech.challenge.vaccination.system.domain.repositories.UserRepository;
import tech.challenge.vaccination.system.domain.valueobjects.UserId;

public class UpdateUserUseCase {
    
    private final UserRepository userRepository;
    
    public UpdateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User execute(UserId userId, String name, String email) {
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        User updatedUser = existingUser.updateProfile(name, email);
        
        return userRepository.save(updatedUser);
    }
}
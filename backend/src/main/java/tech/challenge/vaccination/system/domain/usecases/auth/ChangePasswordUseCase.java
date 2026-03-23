package tech.challenge.vaccination.system.domain.usecases.auth;

import tech.challenge.vaccination.system.domain.entities.User;
import tech.challenge.vaccination.system.domain.exceptions.UserNotFoundException;
import tech.challenge.vaccination.system.domain.repositories.UserRepository;
import tech.challenge.vaccination.system.domain.valueobjects.UserId;

public class ChangePasswordUseCase {
    
    private final UserRepository userRepository;
    
    public ChangePasswordUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public void execute(UserId userId, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        User updatedUser = user.changePassword(newPassword);
        
        userRepository.save(updatedUser);
    }
}
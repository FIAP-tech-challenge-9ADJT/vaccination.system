package tech.challenge.vaccination.system.domain.usecases.admin;

import org.springframework.stereotype.Service;
import tech.challenge.vaccination.system.domain.entities.User;
import tech.challenge.vaccination.system.domain.exceptions.UserNotFoundException;
import tech.challenge.vaccination.system.domain.repositories.UserRepository;
import tech.challenge.vaccination.system.domain.valueobjects.UserId;

@Service
public class DeleteUserUseCase {
    private final UserRepository userRepository;

    public DeleteUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(UserId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (user.isAdmin()) {
        }

        userRepository.delete(userId);
    }
}

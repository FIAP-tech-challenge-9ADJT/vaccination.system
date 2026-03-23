package tech.challenge.vaccination.system.application.services;

import org.springframework.stereotype.Service;

import tech.challenge.vaccination.system.application.usecases.CreateUserUseCaseImpl;
import tech.challenge.vaccination.system.domain.entities.Role;
import tech.challenge.vaccination.system.domain.entities.User;
import tech.challenge.vaccination.system.domain.usecases.user.FindUserUseCase;
import tech.challenge.vaccination.system.domain.usecases.user.UpdateUserUseCase;
import tech.challenge.vaccination.system.domain.valueobjects.UserId;

@Service
public class UserApplicationService {

    private final CreateUserUseCaseImpl createUserUseCase;
    private final FindUserUseCase findUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    public UserApplicationService(CreateUserUseCaseImpl createUserUseCase,
                                  FindUserUseCase findUserUseCase,
                                  UpdateUserUseCase updateUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.findUserUseCase = findUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
    }

    public User createUser(String name, String email, String login, String password) {
        return createUserUseCase.execute(name, email, login, password, Role.RoleName.USER);
    }

    public User findUser(UserId userId) {
        return findUserUseCase.execute(userId);
    }

    public User updateUser(UserId userId, String name, String email) {
        return updateUserUseCase.execute(userId, name, email);
    }
}
package tech.challenge.vaccination.system.application.services;

import org.springframework.stereotype.Service;

import tech.challenge.vaccination.system.application.usecases.CreateUserUseCaseImpl;
import tech.challenge.vaccination.system.domain.entities.Role;
import tech.challenge.vaccination.system.domain.entities.User;
import tech.challenge.vaccination.system.domain.usecases.user.FindUserUseCase;
import tech.challenge.vaccination.system.domain.usecases.user.ListUsersUseCase;
import tech.challenge.vaccination.system.domain.usecases.user.UpdateUserUseCase;
import tech.challenge.vaccination.system.domain.valueobjects.UserId;

import java.util.List;

@Service
public class UserApplicationService {

    private final CreateUserUseCaseImpl createUserUseCase;
    private final FindUserUseCase findUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    public UserApplicationService(CreateUserUseCaseImpl createUserUseCase,
                                  FindUserUseCase findUserUseCase,
                                  ListUsersUseCase listUsersUseCase,
                                  UpdateUserUseCase updateUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.findUserUseCase = findUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.updateUserUseCase = updateUserUseCase;
    }

    public User createUser(String name, String email, String login, String password) {
        return createUserUseCase.execute(name, email, login, password, Role.RoleName.PACIENTE);
    }

    public User findUser(UserId userId) {
        return findUserUseCase.execute(userId);
    }

    public List<User> listUsers(int page, int size) {
        return listUsersUseCase.execute(page, size);
    }

    public long countUsers() {
        return listUsersUseCase.count();
    }

    public User updateUser(UserId userId, String name, String email) {
        return updateUserUseCase.execute(userId, name, email);
    }
}
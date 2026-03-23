package tech.challenge.vaccination.system.domain.usecases.user;

import tech.challenge.vaccination.system.domain.entities.User;
import tech.challenge.vaccination.system.domain.repositories.UserRepository;

import java.util.List;

public class ListUsersUseCase {

    private final UserRepository userRepository;

    public ListUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> execute(int page, int size) {
        return userRepository.findAll(page, size);
    }

    public long count() {
        return userRepository.count();
    }
}

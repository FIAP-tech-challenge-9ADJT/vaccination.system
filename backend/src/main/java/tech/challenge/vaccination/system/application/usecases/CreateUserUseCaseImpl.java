package tech.challenge.vaccination.system.application.usecases;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tech.challenge.vaccination.system.domain.entities.Role;
import tech.challenge.vaccination.system.domain.entities.User;
import tech.challenge.vaccination.system.domain.exceptions.UserAlreadyExistsException;
import tech.challenge.vaccination.system.domain.repositories.RoleRepository;
import tech.challenge.vaccination.system.domain.repositories.UserRepository;
import tech.challenge.vaccination.system.domain.usecases.user.CreateUserUseCase;
import tech.challenge.vaccination.system.domain.valueobjects.Email;
import tech.challenge.vaccination.system.domain.valueobjects.Login;

@Service
public class CreateUserUseCaseImpl extends CreateUserUseCase {

    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCaseImpl(UserRepository userRepository, 
                                 RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder) {
        super(userRepository, roleRepository);
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(String name, String email, String login, String password, Role.RoleName roleName) {
        if (super.userRepository.existsByEmail(Email.of(email))) {
            throw new UserAlreadyExistsException("email", email);
        }
        if (super.userRepository.existsByLogin(Login.of(login))) {
            throw new UserAlreadyExistsException("login", login);
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user = User.create(name, email, login, encodedPassword);

        Role role = super.roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role '" + roleName + "' not found in DB. Check RoleRepository."));
        user = user.addRole(role);
        return super.userRepository.save(user);
    }
}
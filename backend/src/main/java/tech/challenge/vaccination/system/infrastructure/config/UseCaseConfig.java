package tech.challenge.vaccination.system.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import tech.challenge.vaccination.system.application.usecases.CreateUserUseCaseImpl;
import tech.challenge.vaccination.system.domain.repositories.RoleRepository;
import tech.challenge.vaccination.system.domain.repositories.UserRepository;
import tech.challenge.vaccination.system.domain.usecases.auth.AuthenticateUserUseCase;
import tech.challenge.vaccination.system.domain.usecases.auth.ChangePasswordUseCase;
import tech.challenge.vaccination.system.domain.usecases.user.FindUserUseCase;
import tech.challenge.vaccination.system.domain.usecases.user.ListUsersUseCase;
import tech.challenge.vaccination.system.domain.usecases.user.UpdateUserUseCase;

@Configuration
public class UseCaseConfig {
    
    @Bean
    public CreateUserUseCaseImpl createUserUseCase(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        return new CreateUserUseCaseImpl(userRepository, roleRepository, passwordEncoder);
    }
    
    @Bean
    public FindUserUseCase findUserUseCase(UserRepository userRepository) {
        return new FindUserUseCase(userRepository);
    }

    @Bean
    public ListUsersUseCase listUsersUseCase(UserRepository userRepository) {
        return new ListUsersUseCase(userRepository);
    }
    
    @Bean
    public UpdateUserUseCase updateUserUseCase(UserRepository userRepository) {
        return new UpdateUserUseCase(userRepository);
    }
    
    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(UserRepository userRepository) {
        return new AuthenticateUserUseCase(userRepository);
    }
    
    @Bean
    public ChangePasswordUseCase changePasswordUseCase(UserRepository userRepository) {
        return new ChangePasswordUseCase(userRepository);
    }
}
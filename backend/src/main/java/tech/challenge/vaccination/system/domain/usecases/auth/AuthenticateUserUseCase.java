package tech.challenge.vaccination.system.domain.usecases.auth;

import tech.challenge.vaccination.system.domain.entities.User;
import tech.challenge.vaccination.system.domain.exceptions.InvalidCredentialsException;
import tech.challenge.vaccination.system.domain.repositories.UserRepository;
import tech.challenge.vaccination.system.domain.valueobjects.Login;

public class AuthenticateUserUseCase {
    
    private final UserRepository userRepository;
    
    public AuthenticateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User execute(String login, String password) {
        User user = userRepository.findByLogin(Login.of(login))
            .orElseThrow(() -> new InvalidCredentialsException());
        
        return user;
    }
}
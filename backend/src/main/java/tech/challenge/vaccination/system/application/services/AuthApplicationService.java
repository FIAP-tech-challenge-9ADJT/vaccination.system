package tech.challenge.vaccination.system.application.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tech.challenge.vaccination.system.domain.entities.User;
import tech.challenge.vaccination.system.domain.exceptions.InvalidCredentialsException;
import tech.challenge.vaccination.system.domain.usecases.auth.AuthenticateUserUseCase;
import tech.challenge.vaccination.system.domain.usecases.auth.ChangePasswordUseCase;
import tech.challenge.vaccination.system.domain.valueobjects.UserId;

@Service
public class AuthApplicationService {
    
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final PasswordEncoder passwordEncoder;
    private final UserApplicationService userApplicationService;
    
    public AuthApplicationService(AuthenticateUserUseCase authenticateUserUseCase,
                                 ChangePasswordUseCase changePasswordUseCase,
                                 PasswordEncoder passwordEncoder,
                                 UserApplicationService userApplicationService) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.passwordEncoder = passwordEncoder;
        this.userApplicationService = userApplicationService;
    }
    
    public User authenticate(String login, String password) {
        User user = authenticateUserUseCase.execute(login, password);
        
        // Validar senha
        if (!passwordEncoder.matches(password, user.getPassword().value())) {
            throw new InvalidCredentialsException();
        }
        
        return user;
    }
    
    public void changePassword(UserId userId, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        changePasswordUseCase.execute(userId, encodedPassword);
    }
    
    public void updateUserProfile(UserId userId, String name, String email) {
        userApplicationService.updateUser(userId, name, email);
    }
}
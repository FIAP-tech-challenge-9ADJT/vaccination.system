package tech.challenge.vaccination.system.validations.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import tech.challenge.vaccination.system.infrastructure.persistence.repositories.UserJpaRepository;
import tech.challenge.vaccination.system.validations.UniqueLogin;

import org.springframework.stereotype.Component;

@Component
public class UniqueLoginValidator implements ConstraintValidator<UniqueLogin, String> {

    private final UserJpaRepository userRepository;

    public UniqueLoginValidator(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        if (login == null || login.trim().isEmpty()) {
            return true; // Deixa a validação @NotBlank cuidar disso
        }
        return !userRepository.existsByLogin(login);
    }
}
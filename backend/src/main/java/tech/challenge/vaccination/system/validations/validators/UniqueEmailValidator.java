package tech.challenge.vaccination.system.validations.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import tech.challenge.vaccination.system.infrastructure.persistence.repositories.UserJpaRepository;
import tech.challenge.vaccination.system.validations.UniqueEmail;

import org.springframework.stereotype.Component;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserJpaRepository userRepository;

    public UniqueEmailValidator(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Deixa a validação @NotBlank cuidar disso
        }
        return !userRepository.existsByEmail(email);
    }
}
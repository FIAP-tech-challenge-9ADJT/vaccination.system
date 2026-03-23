package tech.challenge.vaccination.system.validations.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import tech.challenge.vaccination.system.validations.PasswordMatches;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    private String passwordField;
    private String confirmPasswordField;

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        this.passwordField = constraintAnnotation.password();
        this.confirmPasswordField = constraintAnnotation.confirmPassword();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true;
        }

        try {
            // Tratamento genérico usando reflection
            Field passwordFieldObj = obj.getClass().getDeclaredField(passwordField);
            Field confirmPasswordFieldObj = obj.getClass().getDeclaredField(confirmPasswordField);
            
            passwordFieldObj.setAccessible(true);
            confirmPasswordFieldObj.setAccessible(true);
            
            Object password = passwordFieldObj.get(obj);
            Object confirmPassword = confirmPasswordFieldObj.get(obj);
            
            if (password == null && confirmPassword == null) {
                return true;
            }
            
            return password != null && password.equals(confirmPassword);
            
        } catch (Exception e) {
            return false;
        }
    }
}
package tech.challenge.vaccination.system.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import tech.challenge.vaccination.system.validations.validators.UniqueLoginValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueLoginValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueLogin {
    String message() default "Login já está em uso";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
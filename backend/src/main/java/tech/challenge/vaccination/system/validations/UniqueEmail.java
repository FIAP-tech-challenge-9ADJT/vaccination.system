package tech.challenge.vaccination.system.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import tech.challenge.vaccination.system.validations.validators.UniqueEmailValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {
    String message() default "Email já está em uso";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
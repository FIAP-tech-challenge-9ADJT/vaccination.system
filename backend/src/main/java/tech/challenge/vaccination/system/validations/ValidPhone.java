package tech.challenge.vaccination.system.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import tech.challenge.vaccination.system.validations.validators.ValidPhoneValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidPhoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {
    String message() default "Telefone deve estar no formato (XX) XXXXX-XXXX ou (XX) XXXX-XXXX";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
package tech.challenge.vaccination.system.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import tech.challenge.vaccination.system.validations.validators.ValidCEPValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidCEPValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCEP {
    String message() default "CEP deve estar no formato 00000000 (8 dígitos)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
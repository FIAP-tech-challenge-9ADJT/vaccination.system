package tech.challenge.vaccination.system.validations.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import tech.challenge.vaccination.system.validations.ValidCEP;

import org.springframework.stereotype.Component;

@Component
public class ValidCEPValidator implements ConstraintValidator<ValidCEP, String> {

    @Override
    public void initialize(ValidCEP constraintAnnotation) {
        // Inicialização se necessária
    }

    @Override
    public boolean isValid(String cep, ConstraintValidatorContext context) {
        if (cep == null || cep.trim().isEmpty()) {
            return true; // Deixa a validação @NotBlank cuidar disso
        }
        
        // Remove espaços e hífens
        String cleanCep = cep.replaceAll("[\\s-]", "");
        
        // Verifica se tem exatamente 8 dígitos
        if (cleanCep.length() != 8) {
            return false;
        }
        
        // Verifica se todos os caracteres são dígitos
        return cleanCep.matches("\\d{8}");
    }
}
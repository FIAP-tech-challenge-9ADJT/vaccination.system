package tech.challenge.vaccination.system.validations.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import tech.challenge.vaccination.system.validations.ValidCPF;

import org.springframework.stereotype.Component;

@Component
public class ValidCPFValidator implements ConstraintValidator<ValidCPF, String> {

    @Override
    public void initialize(ValidCPF constraintAnnotation) {
        // Inicialização se necessária
    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return true; // Deixa a validação @NotBlank cuidar disso
        }
        
        // Remove caracteres não numéricos
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        
        // Verifica se tem 11 dígitos
        if (cleanCpf.length() != 11) {
            return false;
        }
        
        // Verifica se todos os dígitos são iguais (CPF inválido)
        if (cleanCpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        // Validação do primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cleanCpf.charAt(i)) * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) {
            firstDigit = 0;
        }
        
        if (Character.getNumericValue(cleanCpf.charAt(9)) != firstDigit) {
            return false;
        }
        
        // Validação do segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cleanCpf.charAt(i)) * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) {
            secondDigit = 0;
        }
        
        return Character.getNumericValue(cleanCpf.charAt(10)) == secondDigit;
    }
}
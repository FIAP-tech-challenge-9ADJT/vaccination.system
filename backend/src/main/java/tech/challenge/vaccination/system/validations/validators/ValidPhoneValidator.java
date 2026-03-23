package tech.challenge.vaccination.system.validations.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import tech.challenge.vaccination.system.validations.ValidPhone;

import org.springframework.stereotype.Component;

@Component
public class ValidPhoneValidator implements ConstraintValidator<ValidPhone, String> {

    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        // Inicialização se necessária
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Deixa a validação @NotBlank cuidar disso
        }
        
        // Remove caracteres não numéricos
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        
        // Verifica se tem 10 ou 11 dígitos (telefone fixo ou celular)
        if (cleanPhone.length() != 10 && cleanPhone.length() != 11) {
            return false;
        }
        
        // Verifica se começa com código de área válido (11-99)
        if (cleanPhone.length() >= 2) {
            int areaCode = Integer.parseInt(cleanPhone.substring(0, 2));
            if (areaCode < 11 || areaCode > 99) {
                return false;
            }
        }
        
        // Para celular (11 dígitos), o terceiro dígito deve ser 9
        if (cleanPhone.length() == 11) {
            return cleanPhone.charAt(2) == '9';
        }
        
        // Para telefone fixo (10 dígitos), o terceiro dígito deve estar entre 2-5
        if (cleanPhone.length() == 10) {
            char thirdDigit = cleanPhone.charAt(2);
            return thirdDigit >= '2' && thirdDigit <= '5';
        }
        
        return false;
    }
}
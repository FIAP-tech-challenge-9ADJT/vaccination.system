package tech.challenge.vaccination.system.presentation.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDTO(
        @Size(max = 100, message = "Name must have at most 100 characters")
        String name,

        @Email(message = "Email must be valid")
        String email,

        @Size(min = 4, max = 50, message = "Login must have between 4 and 50 characters")
        String login,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must have at least 6 characters")
        String password
) {}

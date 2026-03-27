package tech.challenge.vaccination.system.presentation.dtos.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import tech.challenge.vaccination.system.presentation.dtos.role.RoleResponseDTO;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        String login,
        String cpf,
        LocalDate birthDate,
        String sex,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<RoleResponseDTO> roles
) {
}

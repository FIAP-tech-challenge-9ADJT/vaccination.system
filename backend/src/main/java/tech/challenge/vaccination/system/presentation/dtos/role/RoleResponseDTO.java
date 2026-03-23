package tech.challenge.vaccination.system.presentation.dtos.role;

import tech.challenge.vaccination.system.infrastructure.persistence.entities.RoleName;

public record RoleResponseDTO(
        Long id,
        RoleName name
) {
}
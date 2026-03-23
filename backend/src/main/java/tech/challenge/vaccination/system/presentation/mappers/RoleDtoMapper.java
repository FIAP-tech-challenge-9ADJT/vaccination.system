package tech.challenge.vaccination.system.presentation.mappers;

import tech.challenge.vaccination.system.domain.entities.Role;
import tech.challenge.vaccination.system.presentation.dtos.role.RoleResponseDTO;

public class RoleDtoMapper {
    
    public static RoleResponseDTO toResponseDto(Role role) {
        return new RoleResponseDTO(
            role.getId(),
            tech.challenge.vaccination.system.infrastructure.persistence.entities.RoleName.valueOf(role.getName().name())
        );
    }
}
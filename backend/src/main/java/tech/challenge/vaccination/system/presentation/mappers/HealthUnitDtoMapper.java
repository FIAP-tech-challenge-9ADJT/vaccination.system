package tech.challenge.vaccination.system.presentation.mappers;

import tech.challenge.vaccination.system.domain.entities.HealthUnit;
import tech.challenge.vaccination.system.presentation.dtos.healthunit.HealthUnitResponseDTO;

public class HealthUnitDtoMapper {

    public static HealthUnitResponseDTO toResponseDto(HealthUnit unit) {
        return new HealthUnitResponseDTO(
            unit.getId(), unit.getName(), unit.getCnes(),
            unit.getAddress(), unit.getCity(), unit.getState(),
            unit.getPhone(), unit.isActive(),
            unit.getCreatedAt(), unit.getUpdatedAt()
        );
    }
}

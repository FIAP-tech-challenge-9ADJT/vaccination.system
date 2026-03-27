package tech.challenge.vaccination.system.presentation.mappers;

import tech.challenge.vaccination.system.domain.entities.Vaccine;
import tech.challenge.vaccination.system.presentation.dtos.vaccine.VaccineResponseDTO;

public class VaccineDtoMapper {

    public static VaccineResponseDTO toResponseDto(Vaccine vaccine) {
        return new VaccineResponseDTO(
            vaccine.getId(),
            vaccine.getName(),
            vaccine.getManufacturer(),
            vaccine.getType().name(),
            vaccine.getRequiredDoses(),
            vaccine.getDoseIntervalDays(),
            vaccine.getMinAgeMonths(),
            vaccine.getMaxAgeMonths(),
            vaccine.getContraindications(),
            vaccine.getDescription(),
            vaccine.isActive(),
            vaccine.getCreatedAt(),
            vaccine.getUpdatedAt()
        );
    }
}

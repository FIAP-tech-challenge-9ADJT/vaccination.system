package tech.challenge.vaccination.system.presentation.mappers;

import tech.challenge.vaccination.system.domain.entities.VaccinationRecord;
import tech.challenge.vaccination.system.presentation.dtos.vaccination.VaccinationRecordResponseDTO;

public class VaccinationRecordDtoMapper {

    public static VaccinationRecordResponseDTO toResponseDto(VaccinationRecord record) {
        return new VaccinationRecordResponseDTO(
            record.getId(),
            record.getPatientId(),
            record.getPatientName(),
            record.getVaccineId(),
            record.getVaccineName(),
            record.getProfessionalId(),
            record.getProfessionalName(),
            record.getHealthUnitId(),
            record.getHealthUnitName(),
            record.getDoseNumber(),
            record.getLotNumber(),
            record.getApplicationDate(),
            record.getNotes(),
            record.getCreatedAt(),
            record.getUpdatedAt()
        );
    }
}

package tech.challenge.vaccination.system.infrastructure.persistence.mappers;

import tech.challenge.vaccination.system.domain.entities.VaccinationRecord;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.VaccinationRecordJpaEntity;

public class VaccinationRecordJpaMapper {

    public static VaccinationRecord toDomainEntity(VaccinationRecordJpaEntity entity) {
        if (entity == null) return null;
        return new VaccinationRecord(
            entity.getId(),
            entity.getPatient().getId(),
            entity.getVaccine().getId(),
            entity.getProfessional().getId(),
            entity.getHealthUnit() != null ? entity.getHealthUnit().getId() : null,
            entity.getDoseNumber(),
            entity.getLotNumber(),
            entity.getApplicationDate(),
            entity.getNotes(),
            entity.getPatient().getName(),
            entity.getVaccine().getName(),
            entity.getProfessional().getName(),
            entity.getHealthUnit() != null ? entity.getHealthUnit().getName() : null,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}

package tech.challenge.vaccination.system.infrastructure.persistence.mappers;

import tech.challenge.vaccination.system.domain.entities.Vaccine;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.VaccineJpaEntity;

public class VaccineJpaMapper {

    public static VaccineJpaEntity toJpaEntity(Vaccine vaccine) {
        if (vaccine == null) return null;
        VaccineJpaEntity entity = new VaccineJpaEntity();
        entity.setId(vaccine.getId());
        entity.setName(vaccine.getName());
        entity.setManufacturer(vaccine.getManufacturer());
        entity.setType(vaccine.getType().name());
        entity.setRequiredDoses(vaccine.getRequiredDoses());
        entity.setDoseIntervalDays(vaccine.getDoseIntervalDays());
        entity.setMinAgeMonths(vaccine.getMinAgeMonths());
        entity.setMaxAgeMonths(vaccine.getMaxAgeMonths());
        entity.setContraindications(vaccine.getContraindications());
        entity.setDescription(vaccine.getDescription());
        entity.setActive(vaccine.isActive());
        entity.setCreatedAt(vaccine.getCreatedAt());
        entity.setUpdatedAt(vaccine.getUpdatedAt());
        return entity;
    }

    public static Vaccine toDomainEntity(VaccineJpaEntity entity) {
        if (entity == null) return null;
        return new Vaccine(
            entity.getId(),
            entity.getName(),
            entity.getManufacturer(),
            Vaccine.VaccineType.valueOf(entity.getType()),
            entity.getRequiredDoses(),
            entity.getDoseIntervalDays(),
            entity.getMinAgeMonths(),
            entity.getMaxAgeMonths(),
            entity.getContraindications(),
            entity.getDescription(),
            entity.isActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}

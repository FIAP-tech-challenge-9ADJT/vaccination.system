package tech.challenge.vaccination.system.infrastructure.persistence.mappers;

import tech.challenge.vaccination.system.domain.entities.Campaign;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.CampaignJpaEntity;

public class CampaignJpaMapper {

    public static Campaign toDomainEntity(CampaignJpaEntity entity, long appliedDoses) {
        if (entity == null) return null;
        return new Campaign(
            entity.getId(), entity.getName(),
            entity.getVaccine().getId(),
            entity.getDescription(), entity.getTargetAudience(),
            entity.getDoseGoal(), entity.getStartDate(), entity.getEndDate(),
            entity.isActive(), entity.getVaccine().getName(), appliedDoses,
            entity.getCreatedAt(), entity.getUpdatedAt()
        );
    }
}

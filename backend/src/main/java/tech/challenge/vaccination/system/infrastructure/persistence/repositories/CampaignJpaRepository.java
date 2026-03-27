package tech.challenge.vaccination.system.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.CampaignJpaEntity;

import java.time.LocalDate;
import java.util.List;

public interface CampaignJpaRepository extends JpaRepository<CampaignJpaEntity, Long> {
    List<CampaignJpaEntity> findByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate today1, LocalDate today2);
}

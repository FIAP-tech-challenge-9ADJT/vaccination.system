package tech.challenge.vaccination.system.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.HealthUnitJpaEntity;

import java.util.List;
import java.util.Optional;

public interface HealthUnitJpaRepository extends JpaRepository<HealthUnitJpaEntity, Long> {
    List<HealthUnitJpaEntity> findByActiveTrue();
    Optional<HealthUnitJpaEntity> findByCnes(String cnes);
    boolean existsByCnes(String cnes);
}

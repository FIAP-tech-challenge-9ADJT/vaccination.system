package tech.challenge.vaccination.system.infrastructure.persistence.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.VaccineJpaEntity;

import java.util.List;

public interface VaccineJpaRepository extends JpaRepository<VaccineJpaEntity, Long> {
    Page<VaccineJpaEntity> findByActiveTrue(Pageable pageable);
    List<VaccineJpaEntity> findByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}

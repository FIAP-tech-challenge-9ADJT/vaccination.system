package tech.challenge.vaccination.system.domain.repositories;

import tech.challenge.vaccination.system.domain.entities.HealthUnit;

import java.util.List;
import java.util.Optional;

public interface HealthUnitRepository {
    HealthUnit save(HealthUnit healthUnit);
    Optional<HealthUnit> findById(Long id);
    List<HealthUnit> findAll(int page, int size);
    List<HealthUnit> findAllActive();
    long count();
    void deleteById(Long id);
}

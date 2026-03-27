package tech.challenge.vaccination.system.infrastructure.persistence.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import tech.challenge.vaccination.system.domain.entities.HealthUnit;
import tech.challenge.vaccination.system.domain.repositories.HealthUnitRepository;
import tech.challenge.vaccination.system.infrastructure.persistence.mappers.HealthUnitJpaMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class HealthUnitRepositoryImpl implements HealthUnitRepository {

    private final HealthUnitJpaRepository jpaRepository;

    public HealthUnitRepositoryImpl(HealthUnitJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public HealthUnit save(HealthUnit healthUnit) {
        var entity = HealthUnitJpaMapper.toJpaEntity(healthUnit);
        var saved = jpaRepository.save(entity);
        return HealthUnitJpaMapper.toDomainEntity(saved);
    }

    @Override
    public Optional<HealthUnit> findById(Long id) {
        return jpaRepository.findById(id).map(HealthUnitJpaMapper::toDomainEntity);
    }

    @Override
    public List<HealthUnit> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return jpaRepository.findAll(pageable).getContent().stream()
                .map(HealthUnitJpaMapper::toDomainEntity).toList();
    }

    @Override
    public List<HealthUnit> findAllActive() {
        return jpaRepository.findByActiveTrue().stream()
                .map(HealthUnitJpaMapper::toDomainEntity).toList();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}

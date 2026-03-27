package tech.challenge.vaccination.system.infrastructure.persistence.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import tech.challenge.vaccination.system.domain.entities.Vaccine;
import tech.challenge.vaccination.system.domain.repositories.VaccineRepository;
import tech.challenge.vaccination.system.infrastructure.persistence.mappers.VaccineJpaMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class VaccineRepositoryImpl implements VaccineRepository {

    private final VaccineJpaRepository jpaRepository;

    public VaccineRepositoryImpl(VaccineJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Vaccine save(Vaccine vaccine) {
        var entity = VaccineJpaMapper.toJpaEntity(vaccine);
        var saved = jpaRepository.save(entity);
        return VaccineJpaMapper.toDomainEntity(saved);
    }

    @Override
    public Optional<Vaccine> findById(Long id) {
        return jpaRepository.findById(id).map(VaccineJpaMapper::toDomainEntity);
    }

    @Override
    public List<Vaccine> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return jpaRepository.findAll(pageable).getContent().stream()
                .map(VaccineJpaMapper::toDomainEntity).toList();
    }

    @Override
    public List<Vaccine> findAllActive() {
        return jpaRepository.findByActiveTrue().stream()
                .map(VaccineJpaMapper::toDomainEntity).toList();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }
}

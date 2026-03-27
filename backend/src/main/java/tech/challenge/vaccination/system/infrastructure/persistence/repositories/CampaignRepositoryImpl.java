package tech.challenge.vaccination.system.infrastructure.persistence.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.challenge.vaccination.system.domain.entities.Campaign;
import tech.challenge.vaccination.system.domain.repositories.CampaignRepository;
import tech.challenge.vaccination.system.infrastructure.persistence.mappers.CampaignJpaMapper;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.CampaignJpaEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CampaignRepositoryImpl implements CampaignRepository {

    private final CampaignJpaRepository jpaRepository;
    private final VaccineJpaRepository vaccineJpaRepository;
    private final VaccinationRecordJpaRepository vaccinationRecordJpaRepository;

    public CampaignRepositoryImpl(CampaignJpaRepository jpaRepository,
                                   VaccineJpaRepository vaccineJpaRepository,
                                   VaccinationRecordJpaRepository vaccinationRecordJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.vaccineJpaRepository = vaccineJpaRepository;
        this.vaccinationRecordJpaRepository = vaccinationRecordJpaRepository;
    }

    @Override
    public Campaign save(Long vaccineId, String name, String description,
                         String targetAudience, long doseGoal,
                         LocalDate startDate, LocalDate endDate) {
        CampaignJpaEntity entity = new CampaignJpaEntity();
        entity.setVaccine(vaccineJpaRepository.getReferenceById(vaccineId));
        entity.setName(name);
        entity.setDescription(description);
        entity.setTargetAudience(targetAudience);
        entity.setDoseGoal(doseGoal);
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);
        var saved = jpaRepository.save(entity);
        return mapWithDoses(jpaRepository.findById(saved.getId()).orElseThrow());
    }

    @Override
    public Campaign update(Long id, String name, String description,
                           String targetAudience, long doseGoal,
                           LocalDate startDate, LocalDate endDate, Boolean active) {
        var entity = jpaRepository.findById(id).orElseThrow();
        entity.setName(name);
        entity.setDescription(description);
        entity.setTargetAudience(targetAudience);
        entity.setDoseGoal(doseGoal);
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);
        if (active != null) entity.setActive(active);
        jpaRepository.save(entity);
        return mapWithDoses(entity);
    }

    @Override
    public Optional<Campaign> findById(Long id) {
        return jpaRepository.findById(id).map(this::mapWithDoses);
    }

    @Override
    public List<Campaign> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
        return jpaRepository.findAll(pageable).getContent().stream()
                .map(this::mapWithDoses).toList();
    }

    @Override
    public List<Campaign> findActive() {
        LocalDate today = LocalDate.now();
        return jpaRepository.findByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today)
                .stream().map(this::mapWithDoses).toList();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Campaign mapWithDoses(CampaignJpaEntity entity) {
        long applied = vaccinationRecordJpaRepository.countByVaccineIdAndDateRange(
                entity.getVaccine().getId(), entity.getStartDate(), entity.getEndDate());
        return CampaignJpaMapper.toDomainEntity(entity, applied);
    }
}

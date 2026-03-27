package tech.challenge.vaccination.system.infrastructure.persistence.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.challenge.vaccination.system.domain.entities.VaccinationRecord;
import tech.challenge.vaccination.system.domain.repositories.VaccinationRecordRepository;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.*;
import tech.challenge.vaccination.system.infrastructure.persistence.mappers.VaccinationRecordJpaMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class VaccinationRecordRepositoryImpl implements VaccinationRecordRepository {

    private final VaccinationRecordJpaRepository jpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final VaccineJpaRepository vaccineJpaRepository;
    private final HealthUnitJpaRepository healthUnitJpaRepository;

    public VaccinationRecordRepositoryImpl(VaccinationRecordJpaRepository jpaRepository,
                                            UserJpaRepository userJpaRepository,
                                            VaccineJpaRepository vaccineJpaRepository,
                                            HealthUnitJpaRepository healthUnitJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.vaccineJpaRepository = vaccineJpaRepository;
        this.healthUnitJpaRepository = healthUnitJpaRepository;
    }

    @Override
    public VaccinationRecord save(Long patientId, Long vaccineId, Long professionalId,
                                   Long healthUnitId, int doseNumber, String lotNumber,
                                   LocalDate applicationDate, String notes) {
        VaccinationRecordJpaEntity entity = new VaccinationRecordJpaEntity();
        entity.setPatient(userJpaRepository.getReferenceById(patientId));
        entity.setVaccine(vaccineJpaRepository.getReferenceById(vaccineId));
        entity.setProfessional(userJpaRepository.getReferenceById(professionalId));
        if (healthUnitId != null) {
            entity.setHealthUnit(healthUnitJpaRepository.getReferenceById(healthUnitId));
        }
        entity.setDoseNumber(doseNumber);
        entity.setLotNumber(lotNumber);
        entity.setApplicationDate(applicationDate);
        entity.setNotes(notes);
        var saved = jpaRepository.save(entity);
        return VaccinationRecordJpaMapper.toDomainEntity(jpaRepository.findById(saved.getId()).orElseThrow());
    }

    @Override
    public VaccinationRecord update(Long id, int doseNumber, String lotNumber,
                                     LocalDate applicationDate, String notes) {
        var entity = jpaRepository.findById(id).orElseThrow();
        entity.setDoseNumber(doseNumber);
        entity.setLotNumber(lotNumber);
        entity.setApplicationDate(applicationDate);
        entity.setNotes(notes);
        jpaRepository.save(entity);
        return VaccinationRecordJpaMapper.toDomainEntity(entity);
    }

    @Override
    public Optional<VaccinationRecord> findById(Long id) {
        return jpaRepository.findById(id).map(VaccinationRecordJpaMapper::toDomainEntity);
    }

    @Override
    public List<VaccinationRecord> findByPatientId(Long patientId) {
        return jpaRepository.findByPatientIdOrderByApplicationDateDesc(patientId).stream()
                .map(VaccinationRecordJpaMapper::toDomainEntity).toList();
    }

    @Override
    public List<VaccinationRecord> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("applicationDate").descending());
        return jpaRepository.findAllByOrderByApplicationDateDesc(pageable).getContent().stream()
                .map(VaccinationRecordJpaMapper::toDomainEntity).toList();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public boolean existsByPatientAndVaccineAndDose(Long patientId, Long vaccineId, int doseNumber) {
        return jpaRepository.existsByPatientIdAndVaccineIdAndDoseNumber(patientId, vaccineId, doseNumber);
    }

    @Override
    public List<VaccinationRecord> findByPatientIdAndVaccineId(Long patientId, Long vaccineId) {
        return jpaRepository.findByPatientIdAndVaccineId(patientId, vaccineId).stream()
                .map(VaccinationRecordJpaMapper::toDomainEntity).toList();
    }

    @Override
    public long countByVaccineIdAndDateRange(Long vaccineId, LocalDate startDate, LocalDate endDate) {
        return jpaRepository.countByVaccineIdAndDateRange(vaccineId, startDate, endDate);
    }

    @Override
    public long countDistinctPatients() {
        return jpaRepository.countDistinctPatients();
    }

    @Override
    public List<VaccinationRecord> findRecent(int limit) {
        return jpaRepository.findRecentVaccinations(PageRequest.of(0, limit)).stream()
                .map(VaccinationRecordJpaMapper::toDomainEntity).toList();
    }
}

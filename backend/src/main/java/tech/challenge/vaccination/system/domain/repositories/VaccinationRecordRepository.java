package tech.challenge.vaccination.system.domain.repositories;

import tech.challenge.vaccination.system.domain.entities.VaccinationRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VaccinationRecordRepository {
    VaccinationRecord save(Long patientId, Long vaccineId, Long professionalId,
                           Long healthUnitId, int doseNumber, String lotNumber,
                           LocalDate applicationDate, String notes);
    VaccinationRecord update(Long id, int doseNumber, String lotNumber,
                             LocalDate applicationDate, String notes);
    Optional<VaccinationRecord> findById(Long id);
    List<VaccinationRecord> findByPatientId(Long patientId);
    List<VaccinationRecord> findAll(int page, int size);
    long count();
    boolean existsByPatientAndVaccineAndDose(Long patientId, Long vaccineId, int doseNumber);
    List<VaccinationRecord> findByPatientIdAndVaccineId(Long patientId, Long vaccineId);
    long countByVaccineIdAndDateRange(Long vaccineId, LocalDate startDate, LocalDate endDate);
    long countDistinctPatients();
    List<VaccinationRecord> findRecent(int limit);
}

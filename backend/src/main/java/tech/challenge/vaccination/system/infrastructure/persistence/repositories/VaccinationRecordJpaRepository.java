package tech.challenge.vaccination.system.infrastructure.persistence.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.VaccinationRecordJpaEntity;

import java.time.LocalDate;
import java.util.List;

public interface VaccinationRecordJpaRepository extends JpaRepository<VaccinationRecordJpaEntity, Long> {
    List<VaccinationRecordJpaEntity> findByPatientIdOrderByApplicationDateDesc(Long patientId);
    Page<VaccinationRecordJpaEntity> findAllByOrderByApplicationDateDesc(Pageable pageable);
    boolean existsByPatientIdAndVaccineIdAndDoseNumber(Long patientId, Long vaccineId, int doseNumber);

    @Query("SELECT vr FROM VaccinationRecordJpaEntity vr WHERE vr.patient.id = :patientId AND vr.vaccine.id = :vaccineId ORDER BY vr.doseNumber DESC")
    List<VaccinationRecordJpaEntity> findByPatientIdAndVaccineId(@Param("patientId") Long patientId, @Param("vaccineId") Long vaccineId);

    @Query("SELECT COUNT(vr) FROM VaccinationRecordJpaEntity vr WHERE vr.vaccine.id = :vaccineId AND vr.applicationDate BETWEEN :startDate AND :endDate")
    long countByVaccineIdAndDateRange(@Param("vaccineId") Long vaccineId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(DISTINCT vr.patient.id) FROM VaccinationRecordJpaEntity vr")
    long countDistinctPatients();

    @Query("SELECT vr FROM VaccinationRecordJpaEntity vr ORDER BY vr.createdAt DESC")
    List<VaccinationRecordJpaEntity> findRecentVaccinations(Pageable pageable);
}

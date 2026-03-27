package tech.challenge.vaccination.system.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.PatientConsentJpaEntity;

import java.util.List;
import java.util.Optional;

public interface PatientConsentJpaRepository extends JpaRepository<PatientConsentJpaEntity, Long> {
    Optional<PatientConsentJpaEntity> findByPatientIdAndCompanyId(Long patientId, Long companyId);
    List<PatientConsentJpaEntity> findByPatientIdAndGrantedTrue(Long patientId);
    boolean existsByPatientIdAndCompanyIdAndGrantedTrue(Long patientId, Long companyId);
}

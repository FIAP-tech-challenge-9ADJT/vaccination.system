package tech.challenge.vaccination.system.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.challenge.vaccination.system.application.services.VaccinationApplicationService;
import tech.challenge.vaccination.system.application.services.VaccineApplicationService;
import tech.challenge.vaccination.system.domain.entities.VaccinationRecord;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.UserJpaEntity;
import tech.challenge.vaccination.system.infrastructure.persistence.repositories.PatientConsentJpaRepository;
import tech.challenge.vaccination.system.infrastructure.persistence.repositories.UserJpaRepository;
import tech.challenge.vaccination.system.exceptions.BusinessRuleException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/company")
@PreAuthorize("hasRole('EMPRESA')")
public class CompanyController {

    private final VaccinationApplicationService vaccinationService;
    private final VaccineApplicationService vaccineService;
    private final PatientConsentJpaRepository consentRepository;
    private final UserJpaRepository userJpaRepository;

    public CompanyController(VaccinationApplicationService vaccinationService,
                              VaccineApplicationService vaccineService,
                              PatientConsentJpaRepository consentRepository,
                              UserJpaRepository userJpaRepository) {
        this.vaccinationService = vaccinationService;
        this.vaccineService = vaccineService;
        this.consentRepository = consentRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @GetMapping("/patients/{patientId}/status")
    public ResponseEntity<?> checkVaccinationStatus(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserJpaEntity company) {
        // Check consent
        boolean hasConsent = consentRepository.existsByPatientIdAndCompanyIdAndGrantedTrue(
                patientId, company.getId());
        if (!hasConsent) {
            throw new BusinessRuleException("Paciente não autorizou consulta por esta empresa.");
        }

        var user = userJpaRepository.findById(patientId)
                .orElseThrow(() -> new tech.challenge.vaccination.system.exceptions.ResourceNotFoundException("Paciente não encontrado"));

        var records = vaccinationService.findByPatientId(patientId);
        var allVaccines = vaccineService.findAllActive();
        Set<Long> takenVaccineIds = records.stream()
                .map(VaccinationRecord::getVaccineId).collect(Collectors.toSet());

        long pending = allVaccines.stream()
                .filter(v -> !takenVaccineIds.contains(v.getId())).count();

        Map<String, Object> status = new LinkedHashMap<>();
        status.put("patientName", user.getName());
        status.put("totalVaccinesApplied", records.size());
        status.put("pendingVaccines", pending);
        status.put("status", pending == 0 ? "EM_DIA" : "PENDENTE");
        status.put("consultedAt", LocalDateTime.now());
        return ResponseEntity.ok(status);
    }
}

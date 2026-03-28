package tech.challenge.vaccination.system.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.challenge.vaccination.system.application.services.VaccinationApplicationService;
import tech.challenge.vaccination.system.application.services.VaccineApplicationService;
import tech.challenge.vaccination.system.domain.entities.Vaccine;
import tech.challenge.vaccination.system.domain.entities.VaccinationRecord;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.UserJpaEntity;
import tech.challenge.vaccination.system.infrastructure.persistence.repositories.PatientConsentJpaRepository;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.PatientConsentJpaEntity;
import tech.challenge.vaccination.system.presentation.dtos.vaccination.VaccinationRecordResponseDTO;
import tech.challenge.vaccination.system.presentation.mappers.VaccinationRecordDtoMapper;

import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final VaccinationApplicationService vaccinationService;
    private final VaccineApplicationService vaccineService;
    private final PatientConsentJpaRepository consentRepository;
    private final tech.challenge.vaccination.system.infrastructure.persistence.repositories.UserJpaRepository userJpaRepository;

    public PatientController(VaccinationApplicationService vaccinationService,
                              VaccineApplicationService vaccineService,
                              PatientConsentJpaRepository consentRepository,
                              tech.challenge.vaccination.system.infrastructure.persistence.repositories.UserJpaRepository userJpaRepository) {
        this.vaccinationService = vaccinationService;
        this.vaccineService = vaccineService;
        this.consentRepository = consentRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ENFERMEIRO', 'MEDICO', 'ADMIN')")
    public ResponseEntity<?> searchPatients(@RequestParam String q) {
        String normalized = q.replaceAll("[^\\d\\p{L} ]", "").trim();
        if (normalized.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        // If query is all digits, search by CPF
        String digitsOnly = q.replaceAll("\\D", "");
        if (digitsOnly.length() >= 11) {
            var patient = userJpaRepository.findPatientByCpf(digitsOnly);
            if (patient.isPresent()) {
                var u = patient.get();
                return ResponseEntity.ok(List.of(Map.of(
                    "id", u.getId(),
                    "name", u.getName(),
                    "cpf", u.getCpf() != null ? u.getCpf() : "",
                    "birthDate", u.getBirthDate() != null ? u.getBirthDate().toString() : "",
                    "sex", u.getSex() != null ? u.getSex() : ""
                )));
            }
            return ResponseEntity.ok(List.of());
        }

        // Otherwise search by name
        var patients = userJpaRepository.searchPatientsByName(normalized, PageRequest.of(0, 20));
        var result = patients.stream().map(u -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("cpf", u.getCpf() != null ? u.getCpf() : "");
            map.put("birthDate", u.getBirthDate() != null ? u.getBirthDate().toString() : "");
            map.put("sex", u.getSex() != null ? u.getSex() : "");
            return map;
        }).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me/vaccination-card")
    @PreAuthorize("hasAnyRole('PACIENTE', 'USER')")
    public ResponseEntity<List<VaccinationRecordResponseDTO>> myVaccinationCard(
            @AuthenticationPrincipal UserJpaEntity patient) {
        var records = vaccinationService.findByPatientId(patient.getId()).stream()
                .map(VaccinationRecordDtoMapper::toResponseDto).toList();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/me/pending-vaccines")
    @PreAuthorize("hasAnyRole('PACIENTE', 'USER')")
    public ResponseEntity<?> myPendingVaccines(@AuthenticationPrincipal UserJpaEntity patient) {
        return ResponseEntity.ok(getPendingVaccines(patient.getId()));
    }

    @GetMapping("/me/alerts")
    @PreAuthorize("hasAnyRole('PACIENTE', 'USER')")
    public ResponseEntity<?> myAlerts(@AuthenticationPrincipal UserJpaEntity patient) {
        return ResponseEntity.ok(getAlerts(patient.getId()));
    }

    @GetMapping("/{patientId}/vaccination-card")
    @PreAuthorize("hasAnyRole('ENFERMEIRO', 'MEDICO', 'ADMIN')")
    public ResponseEntity<List<VaccinationRecordResponseDTO>> patientVaccinationCard(@PathVariable Long patientId) {
        var records = vaccinationService.findByPatientId(patientId).stream()
                .map(VaccinationRecordDtoMapper::toResponseDto).toList();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{patientId}/vaccination-card/validate")
    public ResponseEntity<?> validateVaccinationCard(@PathVariable Long patientId) {
        var user = userJpaRepository.findById(patientId);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var records = vaccinationService.findByPatientId(patientId);
        var allVaccines = vaccineService.findAllActive();
        Set<Long> takenVaccineIds = records.stream().map(VaccinationRecord::getVaccineId).collect(Collectors.toSet());

        long pending = allVaccines.stream()
                .filter(v -> !takenVaccineIds.contains(v.getId()))
                .count();

        Map<String, Object> status = new LinkedHashMap<>();
        status.put("patientName", user.get().getName());
        status.put("totalVaccinesApplied", records.size());
        status.put("pendingVaccines", pending);
        status.put("status", pending == 0 ? "EM_DIA" : "PENDENTE");
        status.put("validatedAt", LocalDateTime.now());
        return ResponseEntity.ok(status);
    }

    // Consent endpoints
    @PostMapping("/me/consents/{companyId}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'USER')")
    public ResponseEntity<?> grantConsent(@AuthenticationPrincipal UserJpaEntity patient,
                                           @PathVariable Long companyId) {
        var existing = consentRepository.findByPatientIdAndCompanyId(patient.getId(), companyId);
        PatientConsentJpaEntity consent;
        if (existing.isPresent()) {
            consent = existing.get();
            consent.setGranted(true);
            consent.setGrantedAt(LocalDateTime.now());
            consent.setRevokedAt(null);
        } else {
            consent = new PatientConsentJpaEntity();
            consent.setPatient(patient);
            consent.setCompany(userJpaRepository.getReferenceById(companyId));
            consent.setGranted(true);
            consent.setGrantedAt(LocalDateTime.now());
        }
        consentRepository.save(consent);
        return ResponseEntity.ok(Map.of("message", "Consentimento concedido com sucesso"));
    }

    @DeleteMapping("/me/consents/{companyId}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'USER')")
    public ResponseEntity<?> revokeConsent(@AuthenticationPrincipal UserJpaEntity patient,
                                            @PathVariable Long companyId) {
        var existing = consentRepository.findByPatientIdAndCompanyId(patient.getId(), companyId);
        if (existing.isPresent()) {
            var consent = existing.get();
            consent.setGranted(false);
            consent.setRevokedAt(LocalDateTime.now());
            consentRepository.save(consent);
        }
        return ResponseEntity.ok(Map.of("message", "Consentimento revogado com sucesso"));
    }

    @GetMapping("/me/consents")
    @PreAuthorize("hasAnyRole('PACIENTE', 'USER')")
    public ResponseEntity<?> myConsents(@AuthenticationPrincipal UserJpaEntity patient) {
        var consents = consentRepository.findByPatientIdAndGrantedTrue(patient.getId());
        var result = consents.stream().map(c -> Map.of(
            "companyId", c.getCompany().getId(),
            "companyName", c.getCompany().getName(),
            "grantedAt", c.getGrantedAt()
        )).toList();
        return ResponseEntity.ok(result);
    }

    private List<Map<String, Object>> getPendingVaccines(Long patientId) {
        var records = vaccinationService.findByPatientId(patientId);
        var allVaccines = vaccineService.findAllActive();

        Map<Long, List<VaccinationRecord>> recordsByVaccine = records.stream()
                .collect(Collectors.groupingBy(VaccinationRecord::getVaccineId));

        List<Map<String, Object>> pending = new ArrayList<>();
        for (Vaccine vaccine : allVaccines) {
            List<VaccinationRecord> taken = recordsByVaccine.getOrDefault(vaccine.getId(), List.of());
            int dosesTaken = taken.size();
            if (dosesTaken < vaccine.getRequiredDoses()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("vaccineId", vaccine.getId());
                item.put("vaccineName", vaccine.getName());
                item.put("requiredDoses", vaccine.getRequiredDoses());
                item.put("dosesTaken", dosesTaken);
                item.put("nextDose", dosesTaken + 1);
                pending.add(item);
            }
        }
        return pending;
    }

    private List<Map<String, Object>> getAlerts(Long patientId) {
        var records = vaccinationService.findByPatientId(patientId);
        var allVaccines = vaccineService.findAllActive();

        Map<Long, List<VaccinationRecord>> recordsByVaccine = records.stream()
                .collect(Collectors.groupingBy(VaccinationRecord::getVaccineId));

        List<Map<String, Object>> alerts = new ArrayList<>();
        for (Vaccine vaccine : allVaccines) {
            List<VaccinationRecord> taken = recordsByVaccine.getOrDefault(vaccine.getId(), List.of());
            int dosesTaken = taken.size();
            if (dosesTaken < vaccine.getRequiredDoses()) {
                Map<String, Object> alert = new LinkedHashMap<>();
                alert.put("vaccineId", vaccine.getId());
                alert.put("vaccineName", vaccine.getName());
                if (dosesTaken == 0) {
                    alert.put("type", "ATRASADA");
                    alert.put("message", "Vacina " + vaccine.getName() + " ainda não foi aplicada.");
                } else if (vaccine.getDoseIntervalDays() != null) {
                    VaccinationRecord last = taken.get(0);
                    java.time.LocalDate nextDate = last.getApplicationDate().plusDays(vaccine.getDoseIntervalDays());
                    if (nextDate.isBefore(java.time.LocalDate.now())) {
                        alert.put("type", "ATRASADA");
                        alert.put("message", "Dose " + (dosesTaken + 1) + " da vacina " + vaccine.getName() +
                                " está atrasada. Deveria ter sido aplicada em " + nextDate + ".");
                    } else {
                        alert.put("type", "PROXIMA");
                        alert.put("message", "Próxima dose da vacina " + vaccine.getName() +
                                " deve ser aplicada a partir de " + nextDate + ".");
                    }
                } else {
                    alert.put("type", "PENDENTE");
                    alert.put("message", "Dose " + (dosesTaken + 1) + " da vacina " + vaccine.getName() + " está pendente.");
                }
                alerts.add(alert);
            }
        }
        return alerts;
    }
}

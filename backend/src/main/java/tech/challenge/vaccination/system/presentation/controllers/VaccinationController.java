package tech.challenge.vaccination.system.presentation.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.challenge.vaccination.system.application.services.VaccinationApplicationService;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.UserJpaEntity;
import tech.challenge.vaccination.system.presentation.dtos.user.PageResponseDTO;
import tech.challenge.vaccination.system.presentation.dtos.vaccination.CreateVaccinationRecordDTO;
import tech.challenge.vaccination.system.presentation.dtos.vaccination.UpdateVaccinationRecordDTO;
import tech.challenge.vaccination.system.presentation.dtos.vaccination.VaccinationRecordResponseDTO;
import tech.challenge.vaccination.system.presentation.mappers.VaccinationRecordDtoMapper;

@RestController
@RequestMapping("/vaccinations")
public class VaccinationController {

    private final VaccinationApplicationService vaccinationService;

    public VaccinationController(VaccinationApplicationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ENFERMEIRO', 'MEDICO')")
    public ResponseEntity<VaccinationRecordResponseDTO> create(
            @Valid @RequestBody CreateVaccinationRecordDTO dto,
            @AuthenticationPrincipal UserJpaEntity professional) {
        var record = vaccinationService.register(
            dto.patientId(), dto.vaccineId(), professional.getId(),
            dto.healthUnitId(), dto.doseNumber(), dto.lotNumber(),
            dto.applicationDate(), dto.notes()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(VaccinationRecordDtoMapper.toResponseDto(record));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<VaccinationRecordResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVaccinationRecordDTO dto) {
        var record = vaccinationService.update(
            id, dto.doseNumber(), dto.lotNumber(),
            dto.applicationDate(), dto.notes()
        );
        return ResponseEntity.ok(VaccinationRecordDtoMapper.toResponseDto(record));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENFERMEIRO', 'MEDICO')")
    public ResponseEntity<VaccinationRecordResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(VaccinationRecordDtoMapper.toResponseDto(vaccinationService.findById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENFERMEIRO', 'MEDICO')")
    public ResponseEntity<PageResponseDTO<VaccinationRecordResponseDTO>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var records = vaccinationService.findAll(page, size).stream()
                .map(VaccinationRecordDtoMapper::toResponseDto).toList();
        long total = vaccinationService.count();
        return ResponseEntity.ok(PageResponseDTO.of(records, page, size, total));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENFERMEIRO', 'MEDICO')")
    public ResponseEntity<?> findByPatient(@PathVariable Long patientId) {
        var records = vaccinationService.findByPatientId(patientId).stream()
                .map(VaccinationRecordDtoMapper::toResponseDto).toList();
        return ResponseEntity.ok(records);
    }
}

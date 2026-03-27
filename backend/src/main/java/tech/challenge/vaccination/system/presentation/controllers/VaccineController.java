package tech.challenge.vaccination.system.presentation.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.challenge.vaccination.system.application.services.VaccineApplicationService;
import tech.challenge.vaccination.system.domain.entities.Vaccine;
import tech.challenge.vaccination.system.presentation.dtos.user.PageResponseDTO;
import tech.challenge.vaccination.system.presentation.dtos.vaccine.CreateVaccineDTO;
import tech.challenge.vaccination.system.presentation.dtos.vaccine.UpdateVaccineDTO;
import tech.challenge.vaccination.system.presentation.dtos.vaccine.VaccineResponseDTO;
import tech.challenge.vaccination.system.presentation.mappers.VaccineDtoMapper;

import java.util.List;

@RestController
@RequestMapping("/vaccines")
public class VaccineController {

    private final VaccineApplicationService vaccineService;

    public VaccineController(VaccineApplicationService vaccineService) {
        this.vaccineService = vaccineService;
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<VaccineResponseDTO>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var vaccines = vaccineService.findAll(page, size).stream()
                .map(VaccineDtoMapper::toResponseDto).toList();
        long total = vaccineService.count();
        return ResponseEntity.ok(PageResponseDTO.of(vaccines, page, size, total));
    }

    @GetMapping("/active")
    public ResponseEntity<List<VaccineResponseDTO>> listActive() {
        var vaccines = vaccineService.findAllActive().stream()
                .map(VaccineDtoMapper::toResponseDto).toList();
        return ResponseEntity.ok(vaccines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VaccineResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(VaccineDtoMapper.toResponseDto(vaccineService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VaccineResponseDTO> create(@Valid @RequestBody CreateVaccineDTO dto) {
        var vaccine = vaccineService.create(
            dto.name(), dto.manufacturer(),
            Vaccine.VaccineType.valueOf(dto.type()),
            dto.requiredDoses(), dto.doseIntervalDays(),
            dto.minAgeMonths(), dto.maxAgeMonths(),
            dto.contraindications(), dto.description()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(VaccineDtoMapper.toResponseDto(vaccine));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VaccineResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateVaccineDTO dto) {
        var vaccine = vaccineService.update(
            id, dto.name(), dto.manufacturer(),
            Vaccine.VaccineType.valueOf(dto.type()),
            dto.requiredDoses(), dto.doseIntervalDays(),
            dto.minAgeMonths(), dto.maxAgeMonths(),
            dto.contraindications(), dto.description(), dto.active()
        );
        return ResponseEntity.ok(VaccineDtoMapper.toResponseDto(vaccine));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vaccineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

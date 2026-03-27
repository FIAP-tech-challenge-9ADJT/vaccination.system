package tech.challenge.vaccination.system.presentation.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.challenge.vaccination.system.application.services.HealthUnitApplicationService;
import tech.challenge.vaccination.system.presentation.dtos.healthunit.CreateHealthUnitDTO;
import tech.challenge.vaccination.system.presentation.dtos.healthunit.HealthUnitResponseDTO;
import tech.challenge.vaccination.system.presentation.dtos.healthunit.UpdateHealthUnitDTO;
import tech.challenge.vaccination.system.presentation.dtos.user.PageResponseDTO;
import tech.challenge.vaccination.system.presentation.mappers.HealthUnitDtoMapper;

import java.util.List;

@RestController
@RequestMapping("/health-units")
public class HealthUnitController {

    private final HealthUnitApplicationService healthUnitService;

    public HealthUnitController(HealthUnitApplicationService healthUnitService) {
        this.healthUnitService = healthUnitService;
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<HealthUnitResponseDTO>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var units = healthUnitService.findAll(page, size).stream()
                .map(HealthUnitDtoMapper::toResponseDto).toList();
        long total = healthUnitService.count();
        return ResponseEntity.ok(PageResponseDTO.of(units, page, size, total));
    }

    @GetMapping("/active")
    public ResponseEntity<List<HealthUnitResponseDTO>> listActive() {
        var units = healthUnitService.findAllActive().stream()
                .map(HealthUnitDtoMapper::toResponseDto).toList();
        return ResponseEntity.ok(units);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HealthUnitResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(HealthUnitDtoMapper.toResponseDto(healthUnitService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HealthUnitResponseDTO> create(@Valid @RequestBody CreateHealthUnitDTO dto) {
        var unit = healthUnitService.create(
            dto.name(), dto.cnes(), dto.address(),
            dto.city(), dto.state(), dto.phone()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(HealthUnitDtoMapper.toResponseDto(unit));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HealthUnitResponseDTO> update(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateHealthUnitDTO dto) {
        var unit = healthUnitService.update(
            id, dto.name(), dto.cnes(), dto.address(),
            dto.city(), dto.state(), dto.phone(), dto.active()
        );
        return ResponseEntity.ok(HealthUnitDtoMapper.toResponseDto(unit));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        healthUnitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

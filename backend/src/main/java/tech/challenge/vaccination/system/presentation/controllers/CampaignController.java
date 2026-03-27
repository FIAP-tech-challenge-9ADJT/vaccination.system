package tech.challenge.vaccination.system.presentation.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.challenge.vaccination.system.application.services.CampaignApplicationService;
import tech.challenge.vaccination.system.presentation.dtos.campaign.CampaignResponseDTO;
import tech.challenge.vaccination.system.presentation.dtos.campaign.CreateCampaignDTO;
import tech.challenge.vaccination.system.presentation.dtos.campaign.UpdateCampaignDTO;
import tech.challenge.vaccination.system.presentation.dtos.user.PageResponseDTO;
import tech.challenge.vaccination.system.presentation.mappers.CampaignDtoMapper;

import java.util.List;

@RestController
@RequestMapping("/campaigns")
public class CampaignController {

    private final CampaignApplicationService campaignService;

    public CampaignController(CampaignApplicationService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<CampaignResponseDTO>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var campaigns = campaignService.findAll(page, size).stream()
                .map(CampaignDtoMapper::toResponseDto).toList();
        long total = campaignService.count();
        return ResponseEntity.ok(PageResponseDTO.of(campaigns, page, size, total));
    }

    @GetMapping("/active")
    public ResponseEntity<List<CampaignResponseDTO>> listActive() {
        var campaigns = campaignService.findActive().stream()
                .map(CampaignDtoMapper::toResponseDto).toList();
        return ResponseEntity.ok(campaigns);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(CampaignDtoMapper.toResponseDto(campaignService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CampaignResponseDTO> create(@Valid @RequestBody CreateCampaignDTO dto) {
        var campaign = campaignService.create(
            dto.vaccineId(), dto.name(), dto.description(),
            dto.targetAudience(), dto.doseGoal(),
            dto.startDate(), dto.endDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CampaignDtoMapper.toResponseDto(campaign));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CampaignResponseDTO> update(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateCampaignDTO dto) {
        var campaign = campaignService.update(
            id, dto.name(), dto.description(),
            dto.targetAudience(), dto.doseGoal(),
            dto.startDate(), dto.endDate(), dto.active()
        );
        return ResponseEntity.ok(CampaignDtoMapper.toResponseDto(campaign));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        campaignService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

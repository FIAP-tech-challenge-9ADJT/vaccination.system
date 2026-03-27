package tech.challenge.vaccination.system.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.challenge.vaccination.system.application.services.CampaignApplicationService;
import tech.challenge.vaccination.system.application.services.VaccinationApplicationService;
import tech.challenge.vaccination.system.application.services.VaccineApplicationService;
import tech.challenge.vaccination.system.domain.repositories.UserRepository;
import tech.challenge.vaccination.system.presentation.dtos.campaign.CampaignResponseDTO;
import tech.challenge.vaccination.system.presentation.dtos.vaccination.VaccinationRecordResponseDTO;
import tech.challenge.vaccination.system.presentation.mappers.CampaignDtoMapper;
import tech.challenge.vaccination.system.presentation.mappers.VaccinationRecordDtoMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@PreAuthorize("hasAnyRole('ADMIN', 'ENFERMEIRO', 'MEDICO')")
public class DashboardController {

    private final VaccinationApplicationService vaccinationService;
    private final VaccineApplicationService vaccineService;
    private final CampaignApplicationService campaignService;
    private final UserRepository userRepository;

    public DashboardController(VaccinationApplicationService vaccinationService,
                                VaccineApplicationService vaccineService,
                                CampaignApplicationService campaignService,
                                UserRepository userRepository) {
        this.vaccinationService = vaccinationService;
        this.vaccineService = vaccineService;
        this.campaignService = campaignService;
        this.userRepository = userRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalPatients", vaccinationService.countDistinctPatients());
        stats.put("totalVaccinations", vaccinationService.count());
        stats.put("totalVaccines", vaccineService.count());
        stats.put("activeCampaigns", campaignService.findActive().size());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent-vaccinations")
    public ResponseEntity<List<VaccinationRecordResponseDTO>> getRecentVaccinations() {
        var records = vaccinationService.findRecent(5).stream()
                .map(VaccinationRecordDtoMapper::toResponseDto).toList();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/active-campaigns")
    public ResponseEntity<List<CampaignResponseDTO>> getActiveCampaigns() {
        var campaigns = campaignService.findActive().stream()
                .map(CampaignDtoMapper::toResponseDto).toList();
        return ResponseEntity.ok(campaigns);
    }
}

package tech.challenge.vaccination.system.application.services;

import org.springframework.stereotype.Service;
import tech.challenge.vaccination.system.domain.entities.Campaign;
import tech.challenge.vaccination.system.domain.repositories.CampaignRepository;
import tech.challenge.vaccination.system.exceptions.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;

@Service
public class CampaignApplicationService {

    private final CampaignRepository campaignRepository;

    public CampaignApplicationService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public Campaign create(Long vaccineId, String name, String description,
                           String targetAudience, long doseGoal,
                           LocalDate startDate, LocalDate endDate) {
        return campaignRepository.save(vaccineId, name, description, targetAudience,
                doseGoal, startDate, endDate);
    }

    public Campaign findById(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campanha não encontrada com ID: " + id));
    }

    public List<Campaign> findAll(int page, int size) {
        return campaignRepository.findAll(page, size);
    }

    public List<Campaign> findActive() {
        return campaignRepository.findActive();
    }

    public long count() {
        return campaignRepository.count();
    }

    public Campaign update(Long id, String name, String description,
                           String targetAudience, long doseGoal,
                           LocalDate startDate, LocalDate endDate, Boolean active) {
        findById(id);
        return campaignRepository.update(id, name, description, targetAudience,
                doseGoal, startDate, endDate, active);
    }

    public void delete(Long id) {
        findById(id);
        campaignRepository.deleteById(id);
    }
}

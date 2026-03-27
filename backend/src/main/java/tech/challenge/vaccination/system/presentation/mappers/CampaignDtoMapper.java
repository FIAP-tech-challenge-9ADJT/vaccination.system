package tech.challenge.vaccination.system.presentation.mappers;

import tech.challenge.vaccination.system.domain.entities.Campaign;
import tech.challenge.vaccination.system.presentation.dtos.campaign.CampaignResponseDTO;

public class CampaignDtoMapper {

    public static CampaignResponseDTO toResponseDto(Campaign campaign) {
        return new CampaignResponseDTO(
            campaign.getId(), campaign.getName(),
            campaign.getVaccineId(), campaign.getVaccineName(),
            campaign.getDescription(), campaign.getTargetAudience(),
            campaign.getDoseGoal(), campaign.getAppliedDoses(),
            campaign.getProgressPercentage(),
            campaign.getStartDate(), campaign.getEndDate(),
            campaign.isActive(),
            campaign.getCreatedAt(), campaign.getUpdatedAt()
        );
    }
}

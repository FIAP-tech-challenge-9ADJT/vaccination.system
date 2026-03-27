package tech.challenge.vaccination.system.presentation.dtos.campaign;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CampaignResponseDTO(
    Long id,
    String name,
    Long vaccineId,
    String vaccineName,
    String description,
    String targetAudience,
    long doseGoal,
    long appliedDoses,
    int progressPercentage,
    LocalDate startDate,
    LocalDate endDate,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

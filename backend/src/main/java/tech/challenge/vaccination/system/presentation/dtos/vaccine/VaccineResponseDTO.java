package tech.challenge.vaccination.system.presentation.dtos.vaccine;

import java.time.LocalDateTime;

public record VaccineResponseDTO(
    Long id,
    String name,
    String manufacturer,
    String type,
    int requiredDoses,
    Integer doseIntervalDays,
    Integer minAgeMonths,
    Integer maxAgeMonths,
    String contraindications,
    String description,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

package tech.challenge.vaccination.system.presentation.dtos.healthunit;

import java.time.LocalDateTime;

public record HealthUnitResponseDTO(
    Long id,
    String name,
    String cnes,
    String address,
    String city,
    String state,
    String phone,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

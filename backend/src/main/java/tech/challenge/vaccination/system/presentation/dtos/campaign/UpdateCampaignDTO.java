package tech.challenge.vaccination.system.presentation.dtos.campaign;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateCampaignDTO(
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    String name,

    String description,

    @Size(max = 200, message = "Público-alvo deve ter no máximo 200 caracteres")
    String targetAudience,

    @NotNull(message = "Meta de doses é obrigatória")
    @Min(value = 1, message = "Meta deve ser pelo menos 1")
    Long doseGoal,

    @NotNull(message = "Data de início é obrigatória")
    LocalDate startDate,

    @NotNull(message = "Data de término é obrigatória")
    LocalDate endDate,

    Boolean active
) {}

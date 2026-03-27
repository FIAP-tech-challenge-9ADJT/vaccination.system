package tech.challenge.vaccination.system.presentation.dtos.vaccine;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateVaccineDTO(
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
    String name,

    @Size(max = 150, message = "Fabricante deve ter no máximo 150 caracteres")
    String manufacturer,

    @NotBlank(message = "Tipo é obrigatório")
    String type,

    @NotNull(message = "Número de doses é obrigatório")
    @Min(value = 1, message = "Número de doses deve ser pelo menos 1")
    Integer requiredDoses,

    Integer doseIntervalDays,
    Integer minAgeMonths,
    Integer maxAgeMonths,
    String contraindications,
    String description
) {}

package tech.challenge.vaccination.system.presentation.dtos.vaccination;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateVaccinationRecordDTO(
    @NotNull(message = "Número da dose é obrigatório")
    @Min(value = 1, message = "Número da dose deve ser pelo menos 1")
    Integer doseNumber,

    @Size(max = 50, message = "Lote deve ter no máximo 50 caracteres")
    String lotNumber,

    @NotNull(message = "Data de aplicação é obrigatória")
    LocalDate applicationDate,

    String notes
) {}

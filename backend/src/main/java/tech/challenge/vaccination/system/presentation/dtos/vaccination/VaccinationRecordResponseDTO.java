package tech.challenge.vaccination.system.presentation.dtos.vaccination;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record VaccinationRecordResponseDTO(
    Long id,
    Long patientId,
    String patientName,
    Long vaccineId,
    String vaccineName,
    Long professionalId,
    String professionalName,
    Long healthUnitId,
    String healthUnitName,
    int doseNumber,
    String lotNumber,
    LocalDate applicationDate,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

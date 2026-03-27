package tech.challenge.vaccination.system.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class VaccinationRecord {

    private final Long id;
    private final Long patientId;
    private final Long vaccineId;
    private final Long professionalId;
    private final Long healthUnitId;
    private final int doseNumber;
    private final String lotNumber;
    private final LocalDate applicationDate;
    private final String notes;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // Transient references for display
    private final String patientName;
    private final String vaccineName;
    private final String professionalName;
    private final String healthUnitName;

    public VaccinationRecord(Long id, Long patientId, Long vaccineId, Long professionalId,
                              Long healthUnitId, int doseNumber, String lotNumber,
                              LocalDate applicationDate, String notes,
                              String patientName, String vaccineName,
                              String professionalName, String healthUnitName,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.patientId = Objects.requireNonNull(patientId, "Patient ID cannot be null");
        this.vaccineId = Objects.requireNonNull(vaccineId, "Vaccine ID cannot be null");
        this.professionalId = Objects.requireNonNull(professionalId, "Professional ID cannot be null");
        this.healthUnitId = healthUnitId;
        this.doseNumber = doseNumber;
        this.lotNumber = lotNumber;
        this.applicationDate = Objects.requireNonNull(applicationDate, "Application date cannot be null");
        this.notes = notes;
        this.patientName = patientName;
        this.vaccineName = vaccineName;
        this.professionalName = professionalName;
        this.healthUnitName = healthUnitName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static VaccinationRecord create(Long patientId, Long vaccineId, Long professionalId,
                                            Long healthUnitId, int doseNumber, String lotNumber,
                                            LocalDate applicationDate, String notes) {
        return new VaccinationRecord(null, patientId, vaccineId, professionalId, healthUnitId,
                doseNumber, lotNumber, applicationDate, notes,
                null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now());
    }

    public Long getId() { return id; }
    public Long getPatientId() { return patientId; }
    public Long getVaccineId() { return vaccineId; }
    public Long getProfessionalId() { return professionalId; }
    public Long getHealthUnitId() { return healthUnitId; }
    public int getDoseNumber() { return doseNumber; }
    public String getLotNumber() { return lotNumber; }
    public LocalDate getApplicationDate() { return applicationDate; }
    public String getNotes() { return notes; }
    public String getPatientName() { return patientName; }
    public String getVaccineName() { return vaccineName; }
    public String getProfessionalName() { return professionalName; }
    public String getHealthUnitName() { return healthUnitName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VaccinationRecord that = (VaccinationRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}

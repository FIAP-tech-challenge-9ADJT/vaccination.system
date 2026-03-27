package tech.challenge.vaccination.system.infrastructure.persistence.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vaccination_records",
       uniqueConstraints = @UniqueConstraint(columnNames = {"patient_id", "vaccine_id", "dose_number"}))
public class VaccinationRecordJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private UserJpaEntity patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id", nullable = false)
    private VaccineJpaEntity vaccine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private UserJpaEntity professional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_unit_id")
    private HealthUnitJpaEntity healthUnit;

    @Column(nullable = false)
    private int doseNumber = 1;

    @Column(length = 50)
    private String lotNumber;

    @Column(nullable = false)
    private LocalDate applicationDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public VaccinationRecordJpaEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserJpaEntity getPatient() { return patient; }
    public void setPatient(UserJpaEntity patient) { this.patient = patient; }
    public VaccineJpaEntity getVaccine() { return vaccine; }
    public void setVaccine(VaccineJpaEntity vaccine) { this.vaccine = vaccine; }
    public UserJpaEntity getProfessional() { return professional; }
    public void setProfessional(UserJpaEntity professional) { this.professional = professional; }
    public HealthUnitJpaEntity getHealthUnit() { return healthUnit; }
    public void setHealthUnit(HealthUnitJpaEntity healthUnit) { this.healthUnit = healthUnit; }
    public int getDoseNumber() { return doseNumber; }
    public void setDoseNumber(int doseNumber) { this.doseNumber = doseNumber; }
    public String getLotNumber() { return lotNumber; }
    public void setLotNumber(String lotNumber) { this.lotNumber = lotNumber; }
    public LocalDate getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

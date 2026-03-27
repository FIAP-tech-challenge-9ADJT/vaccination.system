package tech.challenge.vaccination.system.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Campaign {

    private final Long id;
    private final String name;
    private final Long vaccineId;
    private final String description;
    private final String targetAudience;
    private final long doseGoal;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // Transient
    private final String vaccineName;
    private final long appliedDoses;

    public Campaign(Long id, String name, Long vaccineId, String description,
                    String targetAudience, long doseGoal, LocalDate startDate,
                    LocalDate endDate, boolean active,
                    String vaccineName, long appliedDoses,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Campaign name cannot be null");
        this.vaccineId = Objects.requireNonNull(vaccineId, "Vaccine ID cannot be null");
        this.description = description;
        this.targetAudience = targetAudience;
        this.doseGoal = doseGoal;
        this.startDate = Objects.requireNonNull(startDate, "Start date cannot be null");
        this.endDate = Objects.requireNonNull(endDate, "End date cannot be null");
        this.active = active;
        this.vaccineName = vaccineName;
        this.appliedDoses = appliedDoses;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Campaign create(String name, Long vaccineId, String description,
                                   String targetAudience, long doseGoal,
                                   LocalDate startDate, LocalDate endDate) {
        return new Campaign(null, name, vaccineId, description, targetAudience,
                doseGoal, startDate, endDate, true, null, 0,
                LocalDateTime.now(), LocalDateTime.now());
    }

    public int getProgressPercentage() {
        if (doseGoal == 0) return 0;
        return (int) Math.min(100, (appliedDoses * 100) / doseGoal);
    }

    public boolean isOngoing() {
        LocalDate today = LocalDate.now();
        return active && !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Long getVaccineId() { return vaccineId; }
    public String getDescription() { return description; }
    public String getTargetAudience() { return targetAudience; }
    public long getDoseGoal() { return doseGoal; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean isActive() { return active; }
    public String getVaccineName() { return vaccineName; }
    public long getAppliedDoses() { return appliedDoses; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Campaign campaign = (Campaign) o;
        return Objects.equals(id, campaign.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}

package tech.challenge.vaccination.system.domain.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Vaccine {

    private final Long id;
    private final String name;
    private final String manufacturer;
    private final VaccineType type;
    private final int requiredDoses;
    private final Integer doseIntervalDays;
    private final Integer minAgeMonths;
    private final Integer maxAgeMonths;
    private final String contraindications;
    private final String description;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public enum VaccineType {
        ATENUADA, INATIVADA, RNA_MENSAGEIRO, VIRAL_VETOR, SUBUNIDADE, TOXOIDE
    }

    public Vaccine(Long id, String name, String manufacturer, VaccineType type,
                   int requiredDoses, Integer doseIntervalDays,
                   Integer minAgeMonths, Integer maxAgeMonths,
                   String contraindications, String description, boolean active,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Vaccine name cannot be null");
        this.manufacturer = manufacturer;
        this.type = Objects.requireNonNull(type, "Vaccine type cannot be null");
        this.requiredDoses = requiredDoses;
        this.doseIntervalDays = doseIntervalDays;
        this.minAgeMonths = minAgeMonths;
        this.maxAgeMonths = maxAgeMonths;
        this.contraindications = contraindications;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Vaccine create(String name, String manufacturer, VaccineType type,
                                  int requiredDoses, Integer doseIntervalDays,
                                  Integer minAgeMonths, Integer maxAgeMonths,
                                  String contraindications, String description) {
        return new Vaccine(null, name, manufacturer, type, requiredDoses, doseIntervalDays,
                minAgeMonths, maxAgeMonths, contraindications, description, true,
                LocalDateTime.now(), LocalDateTime.now());
    }

    public boolean isAgeAppropriate(int ageInMonths) {
        if (minAgeMonths != null && ageInMonths < minAgeMonths) return false;
        if (maxAgeMonths != null && ageInMonths > maxAgeMonths) return false;
        return true;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getManufacturer() { return manufacturer; }
    public VaccineType getType() { return type; }
    public int getRequiredDoses() { return requiredDoses; }
    public Integer getDoseIntervalDays() { return doseIntervalDays; }
    public Integer getMinAgeMonths() { return minAgeMonths; }
    public Integer getMaxAgeMonths() { return maxAgeMonths; }
    public String getContraindications() { return contraindications; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vaccine vaccine = (Vaccine) o;
        return Objects.equals(id, vaccine.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}

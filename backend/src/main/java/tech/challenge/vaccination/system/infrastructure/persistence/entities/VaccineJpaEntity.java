package tech.challenge.vaccination.system.infrastructure.persistence.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vaccines")
public class VaccineJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 150)
    private String manufacturer;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false)
    private int requiredDoses = 1;

    private Integer doseIntervalDays;
    private Integer minAgeMonths;
    private Integer maxAgeMonths;

    @Column(columnDefinition = "TEXT")
    private String contraindications;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public VaccineJpaEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getRequiredDoses() { return requiredDoses; }
    public void setRequiredDoses(int requiredDoses) { this.requiredDoses = requiredDoses; }
    public Integer getDoseIntervalDays() { return doseIntervalDays; }
    public void setDoseIntervalDays(Integer doseIntervalDays) { this.doseIntervalDays = doseIntervalDays; }
    public Integer getMinAgeMonths() { return minAgeMonths; }
    public void setMinAgeMonths(Integer minAgeMonths) { this.minAgeMonths = minAgeMonths; }
    public Integer getMaxAgeMonths() { return maxAgeMonths; }
    public void setMaxAgeMonths(Integer maxAgeMonths) { this.maxAgeMonths = maxAgeMonths; }
    public String getContraindications() { return contraindications; }
    public void setContraindications(String contraindications) { this.contraindications = contraindications; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

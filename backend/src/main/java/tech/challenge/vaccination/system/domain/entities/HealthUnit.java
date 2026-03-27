package tech.challenge.vaccination.system.domain.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class HealthUnit {

    private final Long id;
    private final String name;
    private final String cnes;
    private final String address;
    private final String city;
    private final String state;
    private final String phone;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public HealthUnit(Long id, String name, String cnes, String address, String city,
                      String state, String phone, boolean active,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Health unit name cannot be null");
        this.cnes = cnes;
        this.address = address;
        this.city = city;
        this.state = state;
        this.phone = phone;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static HealthUnit create(String name, String cnes, String address,
                                     String city, String state, String phone) {
        return new HealthUnit(null, name, cnes, address, city, state, phone, true,
                LocalDateTime.now(), LocalDateTime.now());
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCnes() { return cnes; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPhone() { return phone; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthUnit that = (HealthUnit) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}

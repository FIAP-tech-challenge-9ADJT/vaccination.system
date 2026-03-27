package tech.challenge.vaccination.system.domain.entities;

import java.util.Objects;

public class Role {

    public enum RoleName {
        USER,
        ADMIN,
        PACIENTE,
        ENFERMEIRO,
        MEDICO,
        EMPRESA
    }

    private final Long id;
    private final RoleName name;

    public Role(Long id, RoleName name) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Role name cannot be null");
    }

    public static Role create(RoleName name) {
        return new Role(null, name);
    }

    public static Role of(Long id, RoleName name) {
        return new Role(id, name);
    }

    public Long getId() {
        return id;
    }

    public RoleName getName() {
        return name;
    }

    public String getAuthority() {
        return "ROLE_" + name.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

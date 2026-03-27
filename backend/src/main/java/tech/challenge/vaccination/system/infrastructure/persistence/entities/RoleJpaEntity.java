package tech.challenge.vaccination.system.infrastructure.persistence.entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_role")
public class RoleJpaEntity implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName name;

    @ManyToMany(mappedBy = "roles")
    private Set<UserJpaEntity> users = new HashSet<>();

    public enum RoleName {
        USER,
        ADMIN,
        PACIENTE,
        ENFERMEIRO,
        MEDICO,
        EMPRESA
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public RoleName getName() { return name; }
    public void setName(RoleName name) { this.name = name; }
    public Set<UserJpaEntity> getUsers() { return users; }
    public void setUsers(Set<UserJpaEntity> users) { this.users = users; }
}

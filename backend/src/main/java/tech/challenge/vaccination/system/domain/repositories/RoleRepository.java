package tech.challenge.vaccination.system.domain.repositories;

import java.util.Optional;

import tech.challenge.vaccination.system.domain.entities.Role;

public interface RoleRepository {
    Optional<Role> findByName(Role.RoleName name);
}
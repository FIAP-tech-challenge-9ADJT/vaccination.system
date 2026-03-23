package tech.challenge.vaccination.system.infrastructure.persistence.repositories;

import org.springframework.stereotype.Repository;

import tech.challenge.vaccination.system.domain.entities.Role;
import tech.challenge.vaccination.system.domain.repositories.RoleRepository;
import tech.challenge.vaccination.system.infrastructure.persistence.mappers.RoleJpaMapper;

import java.util.Optional;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;

    public RoleRepositoryImpl(RoleJpaRepository roleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    public Optional<Role> findByName(Role.RoleName name) {
        return roleJpaRepository.findByName(
            tech.challenge.vaccination.system.infrastructure.persistence.entities.RoleJpaEntity.RoleName.valueOf(name.name())
        ).map(RoleJpaMapper::toDomainEntity);
    }
}
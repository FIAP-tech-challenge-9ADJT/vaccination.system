package tech.challenge.vaccination.system.infrastructure.persistence.mappers;

import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

import tech.challenge.vaccination.system.domain.entities.Role;
import tech.challenge.vaccination.system.domain.entities.User;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.UserJpaEntity;

public class UserJpaMapper {

    public static UserJpaEntity toJpaEntity(User user) {
        if (user == null) return null;

        UserJpaEntity jpaEntity = new UserJpaEntity();

        if (user.getId() != null) jpaEntity.setId(user.getId().value());
        jpaEntity.setName(user.getName().value());
        jpaEntity.setEmail(user.getEmail().value());
        jpaEntity.setLogin(user.getLogin().value());
        jpaEntity.setPassword(user.getPassword().value());
        jpaEntity.setCreatedAt(user.getCreatedAt());
        jpaEntity.setUpdatedAt(user.getUpdatedAt());

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            // Criar um novo HashSet para evitar UnsupportedOperationException
            HashSet<tech.challenge.vaccination.system.infrastructure.persistence.entities.RoleJpaEntity> roleSet = 
                user.getRoles().stream()
                    .map(RoleJpaMapper::toJpaEntity)
                    .collect(Collectors.toCollection(HashSet::new));
            jpaEntity.setRoles(roleSet);
        }

        return jpaEntity;
    }

    public static User toDomainEntity(UserJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

        HashSet<Role> rolesHashSet = new HashSet<>();
        if (jpaEntity.getRoles() != null && !jpaEntity.getRoles().isEmpty()) {
            jpaEntity.getRoles().stream()
                    .map(RoleJpaMapper::toDomainEntity)
                    .filter(Objects::nonNull)
                    .forEach(rolesHashSet::add);
        }

        // Removido mapeamento de userType

        return User.of(
                jpaEntity.getId(),
                jpaEntity.getName(),
                jpaEntity.getEmail(),
                jpaEntity.getLogin(),
                jpaEntity.getPassword(),
                rolesHashSet,
                jpaEntity.getCreatedAt(),
                jpaEntity.getUpdatedAt()
        );
    }
}
package tech.challenge.vaccination.system.infrastructure.persistence.mappers;

import tech.challenge.vaccination.system.domain.entities.Role;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.RoleJpaEntity;

public class RoleJpaMapper {
    
    public static RoleJpaEntity toJpaEntity(Role role) {
        if (role == null) return null;
        
        RoleJpaEntity jpaEntity = new RoleJpaEntity();
        jpaEntity.setId(role.getId());
        jpaEntity.setName(RoleJpaEntity.RoleName.valueOf(role.getName().name()));
        
        return jpaEntity;
    }
    
    public static Role toDomainEntity(RoleJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        
        return Role.of(
            jpaEntity.getId(),
            Role.RoleName.valueOf(jpaEntity.getName().name())
        );
    }
}
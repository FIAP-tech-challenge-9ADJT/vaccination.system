package tech.challenge.vaccination.system.infrastructure.persistence.mappers;

import tech.challenge.vaccination.system.domain.entities.HealthUnit;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.HealthUnitJpaEntity;

public class HealthUnitJpaMapper {

    public static HealthUnitJpaEntity toJpaEntity(HealthUnit unit) {
        if (unit == null) return null;
        HealthUnitJpaEntity entity = new HealthUnitJpaEntity();
        entity.setId(unit.getId());
        entity.setName(unit.getName());
        entity.setCnes(unit.getCnes());
        entity.setAddress(unit.getAddress());
        entity.setCity(unit.getCity());
        entity.setState(unit.getState());
        entity.setPhone(unit.getPhone());
        entity.setActive(unit.isActive());
        entity.setCreatedAt(unit.getCreatedAt());
        entity.setUpdatedAt(unit.getUpdatedAt());
        return entity;
    }

    public static HealthUnit toDomainEntity(HealthUnitJpaEntity entity) {
        if (entity == null) return null;
        return new HealthUnit(
            entity.getId(), entity.getName(), entity.getCnes(),
            entity.getAddress(), entity.getCity(), entity.getState(),
            entity.getPhone(), entity.isActive(),
            entity.getCreatedAt(), entity.getUpdatedAt()
        );
    }
}

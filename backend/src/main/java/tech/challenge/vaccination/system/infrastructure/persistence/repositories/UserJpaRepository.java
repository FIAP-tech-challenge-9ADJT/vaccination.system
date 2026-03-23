package tech.challenge.vaccination.system.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import tech.challenge.vaccination.system.infrastructure.persistence.entities.UserJpaEntity;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByLogin(String login);
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);
    boolean existsById(Long id);
}
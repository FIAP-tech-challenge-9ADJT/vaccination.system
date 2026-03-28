package tech.challenge.vaccination.system.infrastructure.persistence.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tech.challenge.vaccination.system.infrastructure.persistence.entities.UserJpaEntity;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByLogin(String login);
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);
    boolean existsById(Long id);

    Optional<UserJpaEntity> findByCpf(String cpf);

    @Query("SELECT u FROM UserJpaEntity u JOIN u.roles r WHERE r.name = 'PACIENTE' AND LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<UserJpaEntity> searchPatientsByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT u FROM UserJpaEntity u JOIN u.roles r WHERE r.name = 'PACIENTE' AND u.cpf = :cpf")
    Optional<UserJpaEntity> findPatientByCpf(@Param("cpf") String cpf);
}
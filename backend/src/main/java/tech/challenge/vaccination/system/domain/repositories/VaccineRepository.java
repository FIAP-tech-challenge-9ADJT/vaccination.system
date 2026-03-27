package tech.challenge.vaccination.system.domain.repositories;

import tech.challenge.vaccination.system.domain.entities.Vaccine;

import java.util.List;
import java.util.Optional;

public interface VaccineRepository {
    Vaccine save(Vaccine vaccine);
    Optional<Vaccine> findById(Long id);
    List<Vaccine> findAll(int page, int size);
    List<Vaccine> findAllActive();
    long count();
    void deleteById(Long id);
    boolean existsByName(String name);
}

package tech.challenge.vaccination.system.application.services;

import org.springframework.stereotype.Service;
import tech.challenge.vaccination.system.domain.entities.Vaccine;
import tech.challenge.vaccination.system.domain.repositories.VaccineRepository;
import tech.challenge.vaccination.system.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
public class VaccineApplicationService {

    private final VaccineRepository vaccineRepository;

    public VaccineApplicationService(VaccineRepository vaccineRepository) {
        this.vaccineRepository = vaccineRepository;
    }

    public Vaccine create(String name, String manufacturer, Vaccine.VaccineType type,
                          int requiredDoses, Integer doseIntervalDays,
                          Integer minAgeMonths, Integer maxAgeMonths,
                          String contraindications, String description) {
        Vaccine vaccine = Vaccine.create(name, manufacturer, type, requiredDoses,
                doseIntervalDays, minAgeMonths, maxAgeMonths, contraindications, description);
        return vaccineRepository.save(vaccine);
    }

    public Vaccine findById(Long id) {
        return vaccineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacina não encontrada com ID: " + id));
    }

    public List<Vaccine> findAll(int page, int size) {
        return vaccineRepository.findAll(page, size);
    }

    public List<Vaccine> findAllActive() {
        return vaccineRepository.findAllActive();
    }

    public long count() {
        return vaccineRepository.count();
    }

    public Vaccine update(Long id, String name, String manufacturer, Vaccine.VaccineType type,
                          int requiredDoses, Integer doseIntervalDays,
                          Integer minAgeMonths, Integer maxAgeMonths,
                          String contraindications, String description, Boolean active) {
        Vaccine existing = findById(id);
        Vaccine updated = new Vaccine(
            existing.getId(), name, manufacturer, type, requiredDoses,
            doseIntervalDays, minAgeMonths, maxAgeMonths,
            contraindications, description,
            active != null ? active : existing.isActive(),
            existing.getCreatedAt(), java.time.LocalDateTime.now()
        );
        return vaccineRepository.save(updated);
    }

    public void delete(Long id) {
        findById(id);
        vaccineRepository.deleteById(id);
    }
}

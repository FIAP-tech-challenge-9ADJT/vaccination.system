package tech.challenge.vaccination.system.application.services;

import org.springframework.stereotype.Service;
import tech.challenge.vaccination.system.domain.entities.HealthUnit;
import tech.challenge.vaccination.system.domain.repositories.HealthUnitRepository;
import tech.challenge.vaccination.system.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
public class HealthUnitApplicationService {

    private final HealthUnitRepository healthUnitRepository;

    public HealthUnitApplicationService(HealthUnitRepository healthUnitRepository) {
        this.healthUnitRepository = healthUnitRepository;
    }

    public HealthUnit create(String name, String cnes, String address,
                             String city, String state, String phone) {
        HealthUnit unit = HealthUnit.create(name, cnes, address, city, state, phone);
        return healthUnitRepository.save(unit);
    }

    public HealthUnit findById(Long id) {
        return healthUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade de saúde não encontrada com ID: " + id));
    }

    public List<HealthUnit> findAll(int page, int size) {
        return healthUnitRepository.findAll(page, size);
    }

    public List<HealthUnit> findAllActive() {
        return healthUnitRepository.findAllActive();
    }

    public long count() {
        return healthUnitRepository.count();
    }

    public HealthUnit update(Long id, String name, String cnes, String address,
                             String city, String state, String phone, Boolean active) {
        HealthUnit existing = findById(id);
        HealthUnit updated = new HealthUnit(
            existing.getId(), name, cnes, address, city, state, phone,
            active != null ? active : existing.isActive(),
            existing.getCreatedAt(), java.time.LocalDateTime.now()
        );
        return healthUnitRepository.save(updated);
    }

    public void delete(Long id) {
        findById(id);
        healthUnitRepository.deleteById(id);
    }
}

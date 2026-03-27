package tech.challenge.vaccination.system.domain.repositories;

import tech.challenge.vaccination.system.domain.entities.Campaign;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository {
    Campaign save(Long vaccineId, String name, String description,
                  String targetAudience, long doseGoal,
                  java.time.LocalDate startDate, java.time.LocalDate endDate);
    Campaign update(Long id, String name, String description,
                    String targetAudience, long doseGoal,
                    java.time.LocalDate startDate, java.time.LocalDate endDate, Boolean active);
    Optional<Campaign> findById(Long id);
    List<Campaign> findAll(int page, int size);
    List<Campaign> findActive();
    long count();
    void deleteById(Long id);
}

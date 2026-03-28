package tech.challenge.vaccination.system.application.services;

import org.springframework.stereotype.Service;
import tech.challenge.vaccination.system.domain.entities.Vaccine;
import tech.challenge.vaccination.system.domain.entities.VaccinationRecord;
import tech.challenge.vaccination.system.domain.repositories.VaccinationRecordRepository;
import tech.challenge.vaccination.system.domain.repositories.VaccineRepository;
import tech.challenge.vaccination.system.exceptions.BusinessRuleException;
import tech.challenge.vaccination.system.exceptions.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class VaccinationApplicationService {

    private final VaccinationRecordRepository vaccinationRecordRepository;
    private final VaccineRepository vaccineRepository;

    public VaccinationApplicationService(VaccinationRecordRepository vaccinationRecordRepository,
                                          VaccineRepository vaccineRepository) {
        this.vaccinationRecordRepository = vaccinationRecordRepository;
        this.vaccineRepository = vaccineRepository;
    }

    public VaccinationRecord register(Long patientId, Long vaccineId, Long professionalId,
                                       Long healthUnitId, int doseNumber, String lotNumber,
                                       LocalDate applicationDate, String notes) {
        // Validate vaccine exists
        Vaccine vaccine = vaccineRepository.findById(vaccineId)
                .orElseThrow(() -> new ResourceNotFoundException("Vacina não encontrada com ID: " + vaccineId));

        // Check duplicate dose
        if (vaccinationRecordRepository.existsByPatientAndVaccineAndDose(patientId, vaccineId, doseNumber)) {
            throw new BusinessRuleException("Esta dose já foi registrada para este paciente e vacina.");
        }

        // Validate dose number
        if (doseNumber > vaccine.getRequiredDoses()) {
            throw new BusinessRuleException(
                "A vacina " + vaccine.getName() + " requer apenas " + vaccine.getRequiredDoses() + " dose(s).");
        }

        // Validate dose interval
        if (doseNumber > 1 && vaccine.getDoseIntervalDays() != null) {
            List<VaccinationRecord> previousDoses = vaccinationRecordRepository
                    .findByPatientIdAndVaccineId(patientId, vaccineId);
            if (!previousDoses.isEmpty()) {
                VaccinationRecord lastDose = previousDoses.get(0); // ordered by dose desc
                long daysBetween = ChronoUnit.DAYS.between(lastDose.getApplicationDate(), applicationDate);
                if (daysBetween < vaccine.getDoseIntervalDays()) {
                    throw new BusinessRuleException(
                        "Intervalo mínimo entre doses é de " + vaccine.getDoseIntervalDays() +
                        " dias. Último registro em " + lastDose.getApplicationDate() + ".");
                }
            }
        }

        return vaccinationRecordRepository.save(patientId, vaccineId, professionalId,
                healthUnitId, doseNumber, lotNumber, applicationDate, notes);
    }

    public VaccinationRecord update(Long id, int doseNumber, String lotNumber,
                                     LocalDate applicationDate, String notes) {
        findById(id);
        return vaccinationRecordRepository.update(id, doseNumber, lotNumber, applicationDate, notes);
    }

    public VaccinationRecord findById(Long id) {
        return vaccinationRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de vacinação não encontrado com ID: " + id));
    }

    public List<VaccinationRecord> findByPatientId(Long patientId) {
        return vaccinationRecordRepository.findByPatientId(patientId);
    }

    public List<VaccinationRecord> findByPatientIdAndVaccineId(Long patientId, Long vaccineId) {
        return vaccinationRecordRepository.findByPatientIdAndVaccineId(patientId, vaccineId);
    }

    public List<VaccinationRecord> findAll(int page, int size) {
        return vaccinationRecordRepository.findAll(page, size);
    }

    public long count() {
        return vaccinationRecordRepository.count();
    }

    public long countDistinctPatients() {
        return vaccinationRecordRepository.countDistinctPatients();
    }

    public List<VaccinationRecord> findRecent(int limit) {
        return vaccinationRecordRepository.findRecent(limit);
    }
}

-- V6__Create_vaccination_record_table.sql

CREATE TABLE IF NOT EXISTS vaccination_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    vaccine_id BIGINT NOT NULL,
    professional_id BIGINT NOT NULL,
    health_unit_id BIGINT,
    dose_number INT NOT NULL DEFAULT 1,
    lot_number VARCHAR(50),
    application_date DATE NOT NULL,
    notes TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_vacc_patient FOREIGN KEY (patient_id) REFERENCES users(id),
    CONSTRAINT fk_vacc_vaccine FOREIGN KEY (vaccine_id) REFERENCES vaccines(id),
    CONSTRAINT fk_vacc_professional FOREIGN KEY (professional_id) REFERENCES users(id),
    CONSTRAINT fk_vacc_health_unit FOREIGN KEY (health_unit_id) REFERENCES health_units(id),
    UNIQUE KEY uk_patient_vaccine_dose (patient_id, vaccine_id, dose_number)
);

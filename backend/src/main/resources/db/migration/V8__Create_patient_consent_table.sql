-- V8__Create_patient_consent_table.sql

CREATE TABLE IF NOT EXISTS patient_consents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    granted BOOLEAN NOT NULL DEFAULT TRUE,
    granted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at DATETIME,
    CONSTRAINT fk_consent_patient FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_consent_company FOREIGN KEY (company_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_patient_company (patient_id, company_id)
);

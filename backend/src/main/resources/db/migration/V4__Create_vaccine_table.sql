-- V4__Create_vaccine_table.sql

CREATE TABLE IF NOT EXISTS vaccines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    manufacturer VARCHAR(150),
    type VARCHAR(50) NOT NULL COMMENT 'ATENUADA, INATIVADA, RNA_MENSAGEIRO, VIRAL_VETOR, SUBUNIDADE, TOXOIDE',
    required_doses INT NOT NULL DEFAULT 1,
    dose_interval_days INT DEFAULT NULL COMMENT 'Minimum days between doses',
    min_age_months INT DEFAULT NULL COMMENT 'Minimum age in months',
    max_age_months INT DEFAULT NULL COMMENT 'Maximum age in months (NULL = no limit)',
    contraindications TEXT,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

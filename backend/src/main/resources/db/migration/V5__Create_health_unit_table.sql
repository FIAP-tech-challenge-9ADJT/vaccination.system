-- V5__Create_health_unit_table.sql

CREATE TABLE IF NOT EXISTS health_units (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    cnes VARCHAR(20) UNIQUE COMMENT 'Cadastro Nacional de Estabelecimentos de Saude',
    address VARCHAR(300),
    city VARCHAR(100),
    state VARCHAR(2),
    phone VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

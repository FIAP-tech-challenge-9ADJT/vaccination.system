-- V3__Add_new_roles_and_patient_fields.sql

-- New roles
INSERT INTO tb_role (name) VALUES ('PACIENTE');
INSERT INTO tb_role (name) VALUES ('ENFERMEIRO');
INSERT INTO tb_role (name) VALUES ('MEDICO');
INSERT INTO tb_role (name) VALUES ('EMPRESA');

-- Patient-specific fields on users table
ALTER TABLE users ADD COLUMN cpf VARCHAR(14) UNIQUE AFTER password;
ALTER TABLE users ADD COLUMN birth_date DATE AFTER cpf;
ALTER TABLE users ADD COLUMN sex ENUM('MASCULINO', 'FEMININO', 'OUTRO') AFTER birth_date;

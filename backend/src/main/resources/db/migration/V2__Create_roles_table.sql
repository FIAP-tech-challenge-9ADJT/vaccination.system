-- V2__Create_roles_table.sql

CREATE TABLE IF NOT EXISTS tb_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS tb_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES tb_role(id) ON DELETE CASCADE
);

INSERT INTO tb_role (name) VALUES ('USER');
INSERT INTO tb_role (name) VALUES ('ADMIN');

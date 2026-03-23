package tech.challenge.vaccination.system.infrastructure.security;

import tech.challenge.vaccination.system.infrastructure.persistence.entities.UserJpaEntity;

public interface TokenService {
    String generateToken(UserJpaEntity user);
    String verifyToken(String token);
}
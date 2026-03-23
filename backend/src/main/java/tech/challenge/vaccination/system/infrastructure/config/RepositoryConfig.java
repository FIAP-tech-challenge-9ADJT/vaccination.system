package tech.challenge.vaccination.system.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tech.challenge.vaccination.system.domain.repositories.*;
import tech.challenge.vaccination.system.infrastructure.persistence.repositories.*;

@Configuration
public class RepositoryConfig {

    @Bean
    public UserRepository userRepository(UserJpaRepository userJpaRepository) {
        return new UserRepositoryImpl(userJpaRepository);
    }

    @Bean
    public RoleRepository roleRepository(RoleJpaRepository roleJpaRepository) {
        return new RoleRepositoryImpl(roleJpaRepository);
    }
}
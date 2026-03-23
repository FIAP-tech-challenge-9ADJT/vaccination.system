package tech.challenge.vaccination.system.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import tech.challenge.vaccination.system.infrastructure.persistence.entities.RoleJpaEntity;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.UserJpaEntity;
import tech.challenge.vaccination.system.infrastructure.persistence.repositories.RoleJpaRepository;
import tech.challenge.vaccination.system.infrastructure.persistence.repositories.UserJpaRepository;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedAdminUser(UserJpaRepository userRepository,
                                           RoleJpaRepository roleRepository,
                                           PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.existsByLogin("admin")) {
                return;
            }

            RoleJpaEntity adminRole = roleRepository.findByName(
                    RoleJpaEntity.RoleName.ADMIN
            ).orElseThrow(() -> new RuntimeException("Role ADMIN not found. Run migrations first."));

            UserJpaEntity admin = new UserJpaEntity();
            admin.setName("Administrador");
            admin.setEmail("admin@vaccination.system");
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.getRoles().add(adminRole);

            userRepository.save(admin);
        };
    }
}

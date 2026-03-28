package tech.challenge.vaccination.system.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import tech.challenge.vaccination.system.infrastructure.persistence.entities.RoleJpaEntity;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.UserJpaEntity;
import tech.challenge.vaccination.system.infrastructure.persistence.repositories.RoleJpaRepository;
import tech.challenge.vaccination.system.infrastructure.persistence.repositories.UserJpaRepository;

import java.time.LocalDate;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    public CommandLineRunner seedInitialUsers(UserJpaRepository userRepository,
                                              RoleJpaRepository roleRepository,
                                              PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed Admin
            if (!userRepository.existsByLogin("admin")) {
                RoleJpaEntity adminRole = roleRepository.findByName(RoleJpaEntity.RoleName.ADMIN)
                        .orElseThrow(() -> new RuntimeException("Role ADMIN not found. Run migrations first."));

                UserJpaEntity admin = new UserJpaEntity();
                admin.setName("Administrador");
                admin.setEmail("admin@vaccination.system");
                admin.setLogin("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.getRoles().add(adminRole);
                userRepository.save(admin);
                log.info("Seed: usuario ADMIN criado (login: admin)");
            }

            RoleJpaEntity enfermeiroRole = roleRepository.findByName(RoleJpaEntity.RoleName.ENFERMEIRO)
                    .orElseThrow(() -> new RuntimeException("Role ENFERMEIRO not found. Run migrations first."));

            // Seed Enfermeiro 1
            if (!userRepository.existsByLogin("enf.carlos")) {
                UserJpaEntity enf1 = new UserJpaEntity();
                enf1.setName("Carlos Souza");
                enf1.setEmail("carlos.souza@vaccination.system");
                enf1.setLogin("enf.carlos");
                enf1.setPassword(passwordEncoder.encode("enf123"));
                enf1.setCpf("11122233344");
                enf1.setBirthDate(LocalDate.of(1988, 3, 15));
                enf1.setSex("MASCULINO");
                enf1.getRoles().add(enfermeiroRole);
                userRepository.save(enf1);
                log.info("Seed: usuario ENFERMEIRO criado (login: enf.carlos)");
            }

            // Seed Enfermeiro 2
            if (!userRepository.existsByLogin("enf.ana")) {
                UserJpaEntity enf2 = new UserJpaEntity();
                enf2.setName("Ana Oliveira");
                enf2.setEmail("ana.oliveira@vaccination.system");
                enf2.setLogin("enf.ana");
                enf2.setPassword(passwordEncoder.encode("enf123"));
                enf2.setCpf("55566677788");
                enf2.setBirthDate(LocalDate.of(1992, 7, 22));
                enf2.setSex("FEMININO");
                enf2.getRoles().add(enfermeiroRole);
                userRepository.save(enf2);
                log.info("Seed: usuario ENFERMEIRO criado (login: enf.ana)");
            }
        };
    }
}

package tech.challenge.vaccination.system.infrastructure.persistence.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import tech.challenge.vaccination.system.domain.entities.User;
import tech.challenge.vaccination.system.domain.repositories.UserRepository;
import tech.challenge.vaccination.system.domain.valueobjects.Email;
import tech.challenge.vaccination.system.domain.valueobjects.Login;
import tech.challenge.vaccination.system.domain.valueobjects.UserId;
import tech.challenge.vaccination.system.infrastructure.persistence.mappers.UserJpaMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    
    private final UserJpaRepository userJpaRepository;
    
    public UserRepositoryImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }
    
    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id.value())
            .map(UserJpaMapper::toDomainEntity);
    }
    
    @Override
    public List<User> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return userJpaRepository.findAll(pageable).getContent().stream()
            .map(UserJpaMapper::toDomainEntity)
            .toList();
    }

    @Override
    public long count() {
        return userJpaRepository.count();
    }

    @Override
    public Optional<User> findByLogin(Login login) {
        return userJpaRepository.findByLogin(login.value())
            .map(UserJpaMapper::toDomainEntity);
    }
    
    @Override
    public User save(User user) {
        var jpaEntity = UserJpaMapper.toJpaEntity(user);
        var savedEntity = userJpaRepository.save(jpaEntity);
        return UserJpaMapper.toDomainEntity(savedEntity);
    }
    
    @Override
    public void delete(UserId id) {
        userJpaRepository.deleteById(id.value());
    }
    
    @Override
    public boolean existsByEmail(Email email) {
        return userJpaRepository.existsByEmail(email.value());
    }
    
    @Override
    public boolean existsByLogin(Login login) {
        return userJpaRepository.existsByLogin(login.value());
    }

    @Override
    public boolean existsById(UserId id) {
        return userJpaRepository.existsById(id.value());
    }
}
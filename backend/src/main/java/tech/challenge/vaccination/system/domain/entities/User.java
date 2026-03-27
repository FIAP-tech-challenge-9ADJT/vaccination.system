package tech.challenge.vaccination.system.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import tech.challenge.vaccination.system.domain.valueobjects.*;

public class User {

    private final UserId id;
    private final Name name;
    private final Email email;
    private final Login login;
    private final Password password;
    private final String cpf;
    private final LocalDate birthDate;
    private final String sex;
    private final Set<Role> roles;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public User(UserId id, Name name, Email email, Login login, Password password,
                String cpf, LocalDate birthDate, String sex,
                Set<Role> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.login = Objects.requireNonNull(login, "Login cannot be null");
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        this.cpf = cpf;
        this.birthDate = birthDate;
        this.sex = sex;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User create(String name, String email, String login, String password) {
        return new User(
            null, Name.of(name), Email.of(email), Login.of(login), Password.of(password),
            null, null, null,
            new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
    }

    public static User createPatient(String name, String email, String login, String password,
                                      String cpf, LocalDate birthDate, String sex) {
        return new User(
            null, Name.of(name), Email.of(email), Login.of(login), Password.of(password),
            cpf, birthDate, sex,
            new HashSet<>(), LocalDateTime.now(), LocalDateTime.now()
        );
    }

    public static User of(Long id, String name, String email, String login, String password,
                          String cpf, LocalDate birthDate, String sex,
                          Set<Role> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new User(
            UserId.of(id), Name.of(name), Email.of(email), Login.of(login), Password.encoded(password),
            cpf, birthDate, sex,
            roles, createdAt, updatedAt
        );
    }

    public User changePassword(String newPassword) {
        return new User(this.id, this.name, this.email, this.login,
                       Password.of(newPassword), this.cpf, this.birthDate, this.sex,
                       this.roles, this.createdAt, LocalDateTime.now());
    }

    public User updateProfile(String name, String email) {
        return new User(this.id, Name.of(name), Email.of(email), this.login,
                       this.password, this.cpf, this.birthDate, this.sex,
                       this.roles, this.createdAt, LocalDateTime.now());
    }

    public User addRole(Role role) {
        Set<Role> newRoles = new HashSet<>(this.roles);
        newRoles.add(role);
        return new User(this.id, this.name, this.email, this.login,
                       this.password, this.cpf, this.birthDate, this.sex,
                       newRoles, this.createdAt, LocalDateTime.now());
    }

    public boolean hasRole(Role.RoleName roleName) {
        return roles.stream().anyMatch(role -> role.getName() == roleName);
    }

    public boolean isAdmin() {
        return hasRole(Role.RoleName.ADMIN);
    }

    public int getAgeInMonths() {
        if (birthDate == null) return -1;
        LocalDate now = LocalDate.now();
        return (int) java.time.temporal.ChronoUnit.MONTHS.between(birthDate, now);
    }

    // Getters
    public UserId getId() { return id; }
    public Name getName() { return name; }
    public Email getEmail() { return email; }
    public Login getLogin() { return login; }
    public Password getPassword() { return password; }
    public String getCpf() { return cpf; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getSex() { return sex; }
    public Set<Role> getRoles() { return new HashSet<>(roles); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

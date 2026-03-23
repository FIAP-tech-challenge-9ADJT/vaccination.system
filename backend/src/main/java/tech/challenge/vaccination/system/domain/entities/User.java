package tech.challenge.vaccination.system.domain.entities;

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
    private final Set<Role> roles;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    public User(UserId id, Name name, Email email, Login login, Password password, 
                 Set<Role> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.login = Objects.requireNonNull(login, "Login cannot be null");
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public static User create(String name, String email, String login, String password) {
        var user = new User(
            null,
            Name.of(name),
            Email.of(email),
            Login.of(login),
            Password.of(password),
            new HashSet<>(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        return user;
    }
    
    public static User of(Long id, String name, String email, String login, String password, 
                          Set<Role> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new User(
            UserId.of(id),
            Name.of(name),
            Email.of(email),
            Login.of(login),
            Password.encoded(password),
            roles,
            createdAt,
            updatedAt
        );
    }
    
    // Métodos de negócio
    public User changePassword(String newPassword) {
        return new User(this.id, this.name, this.email, this.login, 
                       Password.of(newPassword), this.roles, 
                       this.createdAt, LocalDateTime.now());
    }
    
    public User updateProfile(String name, String email) {
        return new User(this.id, Name.of(name), Email.of(email), this.login, 
                       this.password, this.roles, 
                       this.createdAt, LocalDateTime.now());
    }
    
    public User addRole(Role role) {
        Set<Role> newRoles = new HashSet<>(this.roles);
        newRoles.add(role);
        return new User(this.id, this.name, this.email, this.login, 
                       this.password, newRoles, 
                       this.createdAt, LocalDateTime.now());
    }
    
    public boolean hasRole(Role.RoleName roleName) {
        return roles.stream().anyMatch(role -> role.getName() == roleName);
    }

    public boolean isAdmin() {
        return hasRole(Role.RoleName.ADMIN);
    }
    
    // Getters
    public UserId getId() { return id; }
    public Name getName() { return name; }
    public Email getEmail() { return email; }
    public Login getLogin() { return login; }
    public Password getPassword() { return password; }
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
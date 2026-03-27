package tech.challenge.vaccination.system.infrastructure.persistence.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "patient_consents",
       uniqueConstraints = @UniqueConstraint(columnNames = {"patient_id", "company_id"}))
public class PatientConsentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private UserJpaEntity patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private UserJpaEntity company;

    @Column(nullable = false)
    private boolean granted = true;

    private LocalDateTime grantedAt;
    private LocalDateTime revokedAt;

    public PatientConsentJpaEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserJpaEntity getPatient() { return patient; }
    public void setPatient(UserJpaEntity patient) { this.patient = patient; }
    public UserJpaEntity getCompany() { return company; }
    public void setCompany(UserJpaEntity company) { this.company = company; }
    public boolean isGranted() { return granted; }
    public void setGranted(boolean granted) { this.granted = granted; }
    public LocalDateTime getGrantedAt() { return grantedAt; }
    public void setGrantedAt(LocalDateTime grantedAt) { this.grantedAt = grantedAt; }
    public LocalDateTime getRevokedAt() { return revokedAt; }
    public void setRevokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; }
}

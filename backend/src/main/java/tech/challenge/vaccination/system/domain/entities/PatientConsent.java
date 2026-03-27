package tech.challenge.vaccination.system.domain.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class PatientConsent {

    private final Long id;
    private final Long patientId;
    private final Long companyId;
    private final boolean granted;
    private final LocalDateTime grantedAt;
    private final LocalDateTime revokedAt;

    public PatientConsent(Long id, Long patientId, Long companyId, boolean granted,
                          LocalDateTime grantedAt, LocalDateTime revokedAt) {
        this.id = id;
        this.patientId = Objects.requireNonNull(patientId);
        this.companyId = Objects.requireNonNull(companyId);
        this.granted = granted;
        this.grantedAt = grantedAt;
        this.revokedAt = revokedAt;
    }

    public static PatientConsent grant(Long patientId, Long companyId) {
        return new PatientConsent(null, patientId, companyId, true, LocalDateTime.now(), null);
    }

    public PatientConsent revoke() {
        return new PatientConsent(this.id, this.patientId, this.companyId, false,
                this.grantedAt, LocalDateTime.now());
    }

    public Long getId() { return id; }
    public Long getPatientId() { return patientId; }
    public Long getCompanyId() { return companyId; }
    public boolean isGranted() { return granted; }
    public LocalDateTime getGrantedAt() { return grantedAt; }
    public LocalDateTime getRevokedAt() { return revokedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientConsent that = (PatientConsent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}

package es.udc.fi.dc.fd.model.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TrainingReplanningProposal {

    public enum TrainingReplanningProposalStatus {
        PENDING, ACCEPTED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "athleteId")
    private Users athlete;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "coachId")
    private Users coach;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "failedSessionId")
    private TrainingSession failedSession;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String proposalJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainingReplanningProposalStatus status;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    public TrainingReplanningProposal() {}

    public TrainingReplanningProposal(Users athlete, Users coach, TrainingSession failedSession, String proposalJson) {
        this.athlete = athlete;
        this.coach = coach;
        this.failedSession = failedSession;
        this.proposalJson = proposalJson;
        this.status = TrainingReplanningProposalStatus.PENDING;
        this.creationDate = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Users getAthlete() { return athlete; }
    public void setAthlete(Users athlete) { this.athlete = athlete; }

    public Users getCoach() { return coach; }
    public void setCoach(Users coach) { this.coach = coach; }

    public TrainingSession getFailedSession() { return failedSession; }
    public void setFailedSession(TrainingSession failedSession) { this.failedSession = failedSession; }

    public String getProposalJson() { return proposalJson; }
    public void setProposalJson(String proposalJson) { this.proposalJson = proposalJson; }

    public TrainingReplanningProposalStatus getStatus() { return status; }
    public void setStatus(TrainingReplanningProposalStatus status) { this.status = status; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
}
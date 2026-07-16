package es.udc.fi.dc.fd.model.entities;

import jakarta.persistence.*;

@Entity
public class CoachRequest {

    public enum CoachRequestStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    private Long id;
    private Users coach;
    private Users athlete;
    private CoachRequestStatus status;

    public CoachRequest() {}

    public CoachRequest(Users coach, Users athlete, CoachRequestStatus status) {
        this.coach = coach;
        this.athlete = athlete;
        this.status = status;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coachId")
    public Users getCoach() { return coach; }
    public void setCoach(Users coach) { this.coach = coach; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "athleteId")
    public Users getAthlete() { return athlete; }
    public void setAthlete(Users athlete) { this.athlete = athlete; }

    @Enumerated(EnumType.STRING)
    public CoachRequestStatus getStatus() { return status; }
    public void setStatus(CoachRequestStatus status) { this.status = status; }
}
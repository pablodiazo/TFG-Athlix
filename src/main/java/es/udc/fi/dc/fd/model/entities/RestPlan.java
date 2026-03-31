package es.udc.fi.dc.fd.model.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class RestPlan {
    
    private Long id;
    private Users user;
    private Users coach;
    private LocalDate planDate;
    private Double targetSleepHours;
    private String guidelines;

    public RestPlan() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "coachId")
    public Users getCoach() { return coach; }                                           
    public void setCoach(Users coach) { this.coach = coach; }

    public LocalDate getPlanDate() { return planDate; }
    public void setPlanDate(LocalDate planDate) { this.planDate = planDate; }

    public Double getTargetSleepHours() { return targetSleepHours; }
    public void setTargetSleepHours(Double targetSleepHours) { this.targetSleepHours = targetSleepHours; }

    public String getGuidelines() { return guidelines; }                                 
    public void setGuidelines(String guidelines) { this.guidelines = guidelines; }
}

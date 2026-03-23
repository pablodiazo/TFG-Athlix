package es.udc.fi.dc.fd.model.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
public class TrainingSession {

    public enum SportType {
        SWIM, BIKE, RUN, STRENGTH, BRICK, OTHER
    }

    private Long id;
    private Users user;
    private Users coach;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private SportType sport;
    private String objective;
    private String totalDistanceOrDuration;
    private List<TrainingBlock> blocks = new ArrayList<>();

    public TrainingSession() {}

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

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    @Enumerated(EnumType.STRING)
    public SportType getSport() { return sport; }
    public void setSport(SportType sport) { this.sport = sport; }

    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = objective; }

    public String getTotalDistanceOrDuration() { return totalDistanceOrDuration; }
    public void setTotalDistanceOrDuration(String totalDistanceOrDuration) { this.totalDistanceOrDuration = totalDistanceOrDuration; }

    @OneToMany(mappedBy = "trainingSession", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("blockOrder ASC")
    public List<TrainingBlock> getBlocks() { return blocks; }
    public void setBlocks(List<TrainingBlock> blocks) { this.blocks = blocks; }
    
    public void addBlock(TrainingBlock block) {
        blocks.add(block);
        block.setTrainingSession(this);
    }
}
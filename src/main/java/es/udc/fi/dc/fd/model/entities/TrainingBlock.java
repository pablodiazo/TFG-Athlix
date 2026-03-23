package es.udc.fi.dc.fd.model.entities;

import jakarta.persistence.*;

@Entity
public class TrainingBlock {
    private Long id;
    private TrainingSession trainingSession;
    private Integer blockOrder;
    private String name;
    private Integer sets;
    private Integer reps;
    private String distanceOrDuration;
    private String pace;
    private String rest;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trainingSessionId")
    public TrainingSession getTrainingSession() { return trainingSession; }
    public void setTrainingSession(TrainingSession trainingSession) { this.trainingSession = trainingSession; }

    public Integer getBlockOrder() { return blockOrder; }
    public void setBlockOrder(Integer blockOrder) { this.blockOrder = blockOrder; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public String getDistanceOrDuration() { return distanceOrDuration; }
    public void setDistanceOrDuration(String distanceOrDuration) { this.distanceOrDuration = distanceOrDuration; }

    public String getPace() { return pace; }
    public void setPace(String pace) { this.pace = pace; }

    public String getRest() { return rest; }
    public void setRest(String rest) { this.rest = rest; }
}
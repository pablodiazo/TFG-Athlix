package es.udc.fi.dc.fd.rest.dtos;

import es.udc.fi.dc.fd.model.entities.IntensityZone;

public class TrainingBlockDto {
    
    private Long id;
    private Integer blockOrder;
    private String name;
    private Integer sets;
    private Integer reps;
    private String distanceOrDuration;
    private IntensityZone pace;
    private String rest;
    private Double done;

    public TrainingBlockDto() {}

    public TrainingBlockDto(Long id, Integer blockOrder, String name, Integer sets, Integer reps, 
                            String distanceOrDuration, IntensityZone pace, String rest, Double done) {
        this.id = id;
        this.blockOrder = blockOrder;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.distanceOrDuration = distanceOrDuration;
        this.pace = pace;
        this.rest = rest;
        this.done = done;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public IntensityZone getPace() { return pace; }
    public void setPace(IntensityZone pace) { this.pace = pace; }

    public String getRest() { return rest; }                                 
    public void setRest(String rest) { this.rest = rest; }

    public Double getDone() { return done; }
    public void setDone(Double done) { this.done = done; }
}

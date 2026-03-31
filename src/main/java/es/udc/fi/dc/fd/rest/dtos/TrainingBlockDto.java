package es.udc.fi.dc.fd.rest.dtos;

public class TrainingBlockDto {
    
    private Long id;
    private Integer blockOrder;
    private String name;
    private Integer sets;
    private Integer reps;
    private String distanceOrDuration;
    private String pace;
    private String rest;

    public TrainingBlockDto() {}

    public TrainingBlockDto(Long id, Integer blockOrder, String name, Integer sets, Integer reps, 
                            String distanceOrDuration, String pace, String rest) {
        this.id = id;
        this.blockOrder = blockOrder;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.distanceOrDuration = distanceOrDuration;
        this.pace = pace;
        this.rest = rest;
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

    public String getPace() { return pace; }
    public void setPace(String pace) { this.pace = pace; }

    public String getRest() { return rest; }                                 
    public void setRest(String rest) { this.rest = rest; }
}

package es.udc.fi.dc.fd.rest.dtos;

import java.time.LocalDate;

public class RestPlanDto {
    
    private Long id;
    private LocalDate planDate;
    private Double targetSleepHours;
    private String guidelines;
    private Double done;

    public RestPlanDto() {}

    public RestPlanDto(Long id, LocalDate planDate, Double targetSleepHours, String guidelines, Double done) {
        this.id = id;
        this.planDate = planDate;
        this.targetSleepHours = targetSleepHours;
        this.guidelines = guidelines;
        this.done = done;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getPlanDate() { return planDate; }
    public void setPlanDate(LocalDate planDate) { this.planDate = planDate; }

    public Double getTargetSleepHours() { return targetSleepHours; }
    public void setTargetSleepHours(Double targetSleepHours) { this.targetSleepHours = targetSleepHours; }

    public String getGuidelines() { return guidelines; }                                 
    public void setGuidelines(String guidelines) { this.guidelines = guidelines; }

    public Double getDone() { return done; }
    public void setDone(Double done) { this.done = done; }
}

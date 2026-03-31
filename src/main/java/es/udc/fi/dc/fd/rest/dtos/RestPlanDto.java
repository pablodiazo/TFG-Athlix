package es.udc.fi.dc.fd.rest.dtos;

import java.time.LocalDate;

public class RestPlanDto {
    
    private Long id;
    private LocalDate planDate;
    private Double targetSleepHours;
    private String guidelines;

    public RestPlanDto() {}

    public RestPlanDto(Long id, LocalDate planDate, Double targetSleepHours, String guidelines) {
        this.id = id;
        this.planDate = planDate;
        this.targetSleepHours = targetSleepHours;
        this.guidelines = guidelines;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getPlanDate() { return planDate; }
    public void setPlanDate(LocalDate planDate) { this.planDate = planDate; }

    public Double getTargetSleepHours() { return targetSleepHours; }
    public void setTargetSleepHours(Double targetSleepHours) { this.targetSleepHours = targetSleepHours; }

    public String getGuidelines() { return guidelines; }                                 
    public void setGuidelines(String guidelines) { this.guidelines = guidelines; }
}

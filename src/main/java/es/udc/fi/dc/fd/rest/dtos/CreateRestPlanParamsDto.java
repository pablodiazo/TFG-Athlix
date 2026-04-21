package es.udc.fi.dc.fd.rest.dtos;

import java.time.LocalDate;

public class CreateRestPlanParamsDto {

    private Long athleteId;
    private LocalDate planDate;
    private Double targetSleepHours;
    private String guidelines;
    

    public CreateRestPlanParamsDto() {}

    public CreateRestPlanParamsDto(Long athleteId, LocalDate planDate, Double targetSleepHours, String guidelines) {
        this.athleteId = athleteId;
        this.planDate = planDate;
        this.targetSleepHours = targetSleepHours;
        this.guidelines = guidelines;
    }

    public Long getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(Long athleteId) {
        this.athleteId = athleteId;
    }

    public String getGuidelines() {
        return guidelines;
    }

    public void setGuidelines(String guidelines) {
        this.guidelines = guidelines;
    }

    public Double getTargetSleepHours() {
        return targetSleepHours;
    }

    public void setTargetSleepHours(Double targetSleepHours) {
        this.targetSleepHours = targetSleepHours;
    }

    public LocalDate getPlanDate() {
        return planDate;
    }

    public void setPlanDate(LocalDate planDate) {
        this.planDate = planDate;
    }
    
}

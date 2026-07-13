package es.udc.fi.dc.fd.rest.dtos;

import java.time.LocalDate;

public class UpdateRestPlanParamsDto {

    private LocalDate planDate;
    
    private Double targetSleepHours;

    private String guidelines;

    public UpdateRestPlanParamsDto() {}

    public LocalDate getPlanDate() {
        return planDate;
    }

    public void setPlanDate(LocalDate planDate) {
        this.planDate = planDate;
    }

    public Double getTargetSleepHours() {
        return targetSleepHours;
    }

    public void setTargetSleepHours(Double targetSleepHours) {
        this.targetSleepHours = targetSleepHours;
    }

    public String getGuidelines() {
        return guidelines;
    }

    public void setGuidelines(String guidelines) {
        this.guidelines = guidelines;
    }
    
}

package es.udc.fi.dc.fd.rest.dtos;

public class UpdatePlanDoneParamsDto {

    private Long planId;
    private Double done;

    public UpdatePlanDoneParamsDto() {
    }

    public UpdatePlanDoneParamsDto(Long planId, Double done) {
        this.planId = planId;
        this.done = done;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Double getDone() {
        return done;
    }

    public void setDone(Double done) {
        this.done = done;
    }
    
}

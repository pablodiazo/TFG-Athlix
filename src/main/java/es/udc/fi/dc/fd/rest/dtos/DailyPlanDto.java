package es.udc.fi.dc.fd.rest.dtos;

import java.util.List;

public class DailyPlanDto {
    private String date;
    private List<TrainingSessionDto> sessions;
    private NutritionPlanDto nutrition;
    private RestPlanDto rest;

    public DailyPlanDto() {}

    public DailyPlanDto(String date, List<TrainingSessionDto> sessions, 
                           NutritionPlanDto nutrition, RestPlanDto rest) {
        this.date = date;
        this.sessions = sessions;
        this.nutrition = nutrition;
        this.rest = rest;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public List<TrainingSessionDto> getSessions() { return sessions; }
    public void setSessions(List<TrainingSessionDto> sessions) { this.sessions = sessions; }
    public NutritionPlanDto getNutrition() { return nutrition; }
    public void setNutrition(NutritionPlanDto nutrition) { this.nutrition = nutrition; }
    public RestPlanDto getRest() { return rest; }
    public void setRest(RestPlanDto rest) { this.rest = rest; }
}
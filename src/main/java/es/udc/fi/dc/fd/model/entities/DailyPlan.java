package es.udc.fi.dc.fd.model.entities;

import java.util.List;
import java.util.Optional;

public class DailyPlan {
    
    private List<TrainingSession> sessions;
    private Optional<NutritionPlan> nutrition;
    private Optional<RestPlan> rest;

    public DailyPlan(List<TrainingSession> sessions, Optional<NutritionPlan> nutrition, Optional<RestPlan> rest) {
        this.sessions = sessions;
        this.nutrition = nutrition;
        this.rest = rest;
    }

    public List<TrainingSession> getSessions() { return sessions; }
    public Optional<NutritionPlan> getNutrition() { return nutrition; }
    public Optional<RestPlan> getRest() { return rest; }
}
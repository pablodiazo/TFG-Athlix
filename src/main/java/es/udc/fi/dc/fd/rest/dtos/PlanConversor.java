package es.udc.fi.dc.fd.rest.dtos;

import es.udc.fi.dc.fd.model.entities.NutritionPlan;
import es.udc.fi.dc.fd.model.entities.RestPlan;

public class PlanConversor {

    public static NutritionPlanDto toNutritionPlanDto(NutritionPlan nutritionPlan) {
        return new NutritionPlanDto(nutritionPlan.getId(), nutritionPlan.getPlanDate(), nutritionPlan.getTargetCalories(), 
                                    nutritionPlan.getProteinGrams(), nutritionPlan.getCarbsGrams(), nutritionPlan.getFatGrams(), 
                                    nutritionPlan.getHydrationLiters(), nutritionPlan.getGuidelines());
    }

    public static RestPlanDto toRestPlanDto(RestPlan restPlan) {
        return new RestPlanDto(restPlan.getId(), restPlan.getPlanDate(), restPlan.getTargetSleepHours(), restPlan.getGuidelines());
    }
    
}

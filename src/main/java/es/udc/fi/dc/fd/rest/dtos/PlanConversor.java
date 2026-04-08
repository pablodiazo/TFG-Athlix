package es.udc.fi.dc.fd.rest.dtos;

import es.udc.fi.dc.fd.model.entities.NutritionPlan;

public class PlanConversor {

    public static NutritionPlanDto toNutritionPlanDto(NutritionPlan nutritionPlan) {
        return new NutritionPlanDto(nutritionPlan.getId(), nutritionPlan.getPlanDate(), nutritionPlan.getTargetCalories(), 
                                    nutritionPlan.getProteinGrams(), nutritionPlan.getCarbsGrams(), nutritionPlan.getFatGrams(), 
                                    nutritionPlan.getHydrationLiters(), nutritionPlan.getGuidelines());
    }
    
}

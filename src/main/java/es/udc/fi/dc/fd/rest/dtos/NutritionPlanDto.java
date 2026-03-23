package es.udc.fi.dc.fd.rest.dtos;

import java.time.LocalDate;

public class NutritionPlanDto {
    
    private Long id;
    private LocalDate planDate;
    private Integer targetCalories;
    private Integer proteinGrams;
    private Integer carbsGrams;
    private Integer fatGrams;
    private Double hydrationLiters;
    private String guidelines;

    public NutritionPlanDto() {}

    public NutritionPlanDto(Long id, LocalDate planDate, Integer targetCalories, Integer proteinGrams, 
                            Integer carbsGrams, Integer fatGrams, Double hydrationLiters, String guidelines) {
        this.id = id;
        this.planDate = planDate;
        this.targetCalories = targetCalories;
        this.proteinGrams = proteinGrams;
        this.carbsGrams = carbsGrams;
        this.fatGrams = fatGrams;
        this.hydrationLiters = hydrationLiters;
        this.guidelines = guidelines;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getPlanDate() { return planDate; }
    public void setPlanDate(LocalDate planDate) { this.planDate = planDate; }

    public Integer getTargetCalories() { return targetCalories; }
    public void setTargetCalories(Integer targetCalories) { this.targetCalories = targetCalories; }

    public Integer getProteinGrams() { return proteinGrams; }
    public void setProteinGrams(Integer proteinGrams) { this.proteinGrams = proteinGrams; }

    public Integer getCarbsGrams() { return carbsGrams; }
    public void setCarbsGrams(Integer carbsGrams) { this.carbsGrams = carbsGrams; }

    public Integer getFatGrams() { return fatGrams; }
    public void setFatGrams(Integer fatGrams) { this.fatGrams = fatGrams; }

    public Double getHydrationLiters() { return hydrationLiters; }
    public void setHydrationLiters(Double hydrationLiters) { this.hydrationLiters = hydrationLiters; }

    public String getGuidelines() { return guidelines; }
    public void setGuidelines(String guidelines) { this.guidelines = guidelines; }
}

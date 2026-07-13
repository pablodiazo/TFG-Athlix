package es.udc.fi.dc.fd.rest.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateNutritionPlanParamsDto {

    @NotNull
    private LocalDate planDate;

    @NotNull @Min(0)
    private Integer targetCalories;

    @NotNull @Min(0)
    private Integer proteinGrams;

    @NotNull @Min(0)
    private Integer carbsGrams;

    @NotNull @Min(0)
    private Integer fatGrams;

    @NotNull @Min(0)
    private Double hydrationLiters;

    private String guidelines;

    public UpdateNutritionPlanParamsDto() {}

    public LocalDate getPlanDate() {
        return planDate;
    }

    public void setPlanDate(LocalDate planDate) {
        this.planDate = planDate;
    }

    public Integer getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(Integer targetCalories) {
        this.targetCalories = targetCalories;
    }

    public Integer getProteinGrams() {
        return proteinGrams;
    }

    public void setProteinGrams(Integer proteinGrams) {
        this.proteinGrams = proteinGrams;
    }

    public Integer getCarbsGrams() {
        return carbsGrams;
    }

    public void setCarbsGrams(Integer carbsGrams) {
        this.carbsGrams = carbsGrams;
    }

    public Integer getFatGrams() {
        return fatGrams;
    }

    public void setFatGrams(Integer fatGrams) {
        this.fatGrams = fatGrams;
    }

    public Double getHydrationLiters() {
        return hydrationLiters;
    }

    public void setHydrationLiters(Double hydrationLiters) {
        this.hydrationLiters = hydrationLiters;
    }

    public String getGuidelines() {
        return guidelines;
    }

    public void setGuidelines(String guidelines) {
        this.guidelines = guidelines;
    }
    
}

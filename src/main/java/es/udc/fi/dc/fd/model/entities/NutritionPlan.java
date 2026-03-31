package es.udc.fi.dc.fd.model.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class NutritionPlan {
    
    private Long id;
    private Users user;
    private Users coach;
    private LocalDate planDate;
    private Integer targetCalories;
    private Integer proteinGrams;
    private Integer carbsGrams;
    private Integer fatGrams;
    private Double hydrationLiters;
    private String guidelines;

    public NutritionPlan() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "coachId")
    public Users getCoach() { return coach; }                                           
    public void setCoach(Users coach) { this.coach = coach; }

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

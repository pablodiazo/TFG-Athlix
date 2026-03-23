package es.udc.fi.dc.fd.model.entities;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface NutritionPlanDao extends CrudRepository<NutritionPlan, Long> {
    Optional<NutritionPlan> findByUserIdAndPlanDate(Long userId, LocalDate planDate);
}
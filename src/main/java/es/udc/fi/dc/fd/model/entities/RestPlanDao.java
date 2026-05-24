package es.udc.fi.dc.fd.model.entities;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface RestPlanDao extends CrudRepository<RestPlan, Long> {
    Optional<RestPlan> findByUserIdAndPlanDate(Long userId, LocalDate planDate);

    List<RestPlan> findByUserIdAndPlanDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
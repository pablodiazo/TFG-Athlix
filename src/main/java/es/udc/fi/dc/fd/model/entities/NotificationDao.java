package es.udc.fi.dc.fd.model.entities;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface NotificationDao extends CrudRepository<Notification, Long> {
    
    boolean existsByUserIdAndAthleteIdAndPlanDate(Long userId, Long athleteId, LocalDate planDate);

    List<Notification> findByUserIdOrderByIdDesc(Long userId);
}
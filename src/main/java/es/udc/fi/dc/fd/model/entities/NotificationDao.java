package es.udc.fi.dc.fd.model.entities;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface NotificationDao extends CrudRepository<Notification, Long> {
    
    boolean existsByUserIdAndAthleteIdAndPlanDateAndType(Long userId, Long athleteId, LocalDate planDate, String type);

    List<Notification> findByUserIdOrderByIdDesc(Long userId);
}
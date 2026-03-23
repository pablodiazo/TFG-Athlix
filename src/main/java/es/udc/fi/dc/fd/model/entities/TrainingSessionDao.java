package es.udc.fi.dc.fd.model.entities;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface TrainingSessionDao extends CrudRepository<TrainingSession, Long> {
    List<TrainingSession> findByUserIdAndSessionDateOrderByStartTimeAsc(Long userId, LocalDate sessionDate);
}
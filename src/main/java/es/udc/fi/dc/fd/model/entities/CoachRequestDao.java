package es.udc.fi.dc.fd.model.entities;

import org.springframework.data.repository.CrudRepository;

import es.udc.fi.dc.fd.model.entities.CoachRequest.CoachRequestStatus;

import java.util.List;

public interface CoachRequestDao extends CrudRepository<CoachRequest, Long> {
    
    List<CoachRequest> findByAthleteIdAndStatus(Long athleteId, CoachRequest.CoachRequestStatus status);
    
    boolean existsByCoachIdAndAthleteIdAndStatus(Long coachId, Long athleteId, CoachRequest.CoachRequestStatus status);

    List<CoachRequest> findByCoachIdAndStatus(Long coachId, CoachRequestStatus status);
}
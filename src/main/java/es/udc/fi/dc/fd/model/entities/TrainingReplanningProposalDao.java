package es.udc.fi.dc.fd.model.entities;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;

public interface TrainingReplanningProposalDao extends CrudRepository<TrainingReplanningProposal, Long>, PagingAndSortingRepository<TrainingReplanningProposal, Long> {
    
    List<TrainingReplanningProposal> findByCoachIdAndStatusOrderByCreationDateDesc(Long coachId, TrainingReplanningProposal.TrainingReplanningProposalStatus status);
    
    List<TrainingReplanningProposal> findByAthleteIdOrderByCreationDateDesc(Long athleteId);
    
    boolean existsByFailedSessionIdAndStatus(Long failedSessionId, TrainingReplanningProposal.TrainingReplanningProposalStatus status);

    List<TrainingReplanningProposal> findByFailedSessionIdAndStatusOrderByCreationDateDesc(Long failedSessionId, TrainingReplanningProposal.TrainingReplanningProposalStatus status);
}
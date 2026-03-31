package es.udc.fi.dc.fd.model.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.common.exceptions.*;
import es.udc.fi.dc.fd.model.services.exceptions.*;
import es.udc.fi.dc.fd.model.entities.*;

@Service
@Transactional
public class PlanServiceImpl implements PlanService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private TrainingSessionDao trainingSessionDao;

    @Autowired
    private NutritionPlanDao nutritionPlanDao;

    @Autowired
    private RestPlanDao restPlanDao;

    @Override
    public DailyPlan getDailyPlan(Long userId, LocalDate date) throws InstanceNotFoundException {
        List<TrainingSession> sessions = trainingSessionDao.findByUserIdAndSessionDateOrderByStartTimeAsc(userId, date);
        Optional<NutritionPlan> nutrition = nutritionPlanDao.findByUserIdAndPlanDate(userId, date);
        Optional<RestPlan> rest = restPlanDao.findByUserIdAndPlanDate(userId, date);

        return new DailyPlan(sessions, nutrition, rest);
    }
    

    @Override
    public TrainingSession createTrainingSession(Long athleteId, Long coachId, LocalDate date, LocalTime startTime,
        TrainingSession.SportType sportType, String objective, String totalDistanceOrDuration, List<TrainingBlock> blocks) 
        throws InstanceNotFoundException, IncorrectRoleException{
        
        Users coach = userDao.findById(coachId)
                .orElseThrow(() -> new InstanceNotFoundException("user", coachId));

        
        if (coach.getRole() != Users.RoleType.COACH) {
            throw new IncorrectRoleException();
        }

        Users athlete = userDao.findById(athleteId)
                .orElseThrow(() -> new InstanceNotFoundException("user", athleteId));
        
        
        if (athlete.getRole() != Users.RoleType.USER) {
            throw new IncorrectRoleException();
        }

        TrainingSession session = new TrainingSession();
        session.setUser(athlete);
        session.setCoach(coach);
        session.setSessionDate(date);
        session.setStartTime(startTime);
        session.setSport(sportType);
        session.setObjective(objective);
        session.setTotalDistanceOrDuration(totalDistanceOrDuration);

        for (TrainingBlock block : blocks) {
            block.setTrainingSession(session);
            session.addBlock(block);
        }
        return trainingSessionDao.save(session);
    }

}

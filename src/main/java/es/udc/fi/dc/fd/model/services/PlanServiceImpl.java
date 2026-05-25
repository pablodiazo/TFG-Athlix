package es.udc.fi.dc.fd.model.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private TrainingBlockDao trainingBlockDao;

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

    @Override
    public NutritionPlan createNutritionPlan(Long athleteId, Long coachId, LocalDate date, Integer targetCalories, Integer proteinGrams, Integer carbsGrams,
        Integer fatGrams, Double hydrationLiters, String guidelines) throws InstanceNotFoundException, IncorrectRoleException, DuplicateInstanceException {
        
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

        Optional<NutritionPlan> existingPlan = nutritionPlanDao.findByUserIdAndPlanDate(athleteId, date);
        if (existingPlan.isPresent()) {
            throw new DuplicateInstanceException("NutritionPlan", athleteId);
        }

        NutritionPlan nutrition = new NutritionPlan();
        nutrition.setUser(athlete);
        nutrition.setCoach(coach);
        nutrition.setPlanDate(date);
        nutrition.setTargetCalories(targetCalories);
        nutrition.setProteinGrams(proteinGrams);
        nutrition.setCarbsGrams(carbsGrams);
        nutrition.setFatGrams(fatGrams);
        nutrition.setHydrationLiters(hydrationLiters);
        nutrition.setGuidelines(guidelines);
        return nutritionPlanDao.save(nutrition);
    }

    @Override
    public RestPlan createRestPlan(Long athleteId, Long coachId, LocalDate date, Double targetSleepHours, String guidelines) throws InstanceNotFoundException, IncorrectRoleException, DuplicateInstanceException {
        
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

        Optional<RestPlan> existingPlan = restPlanDao.findByUserIdAndPlanDate(athleteId, date);
        if (existingPlan.isPresent()) {
            throw new DuplicateInstanceException("RestPlan", athleteId);
        }

        RestPlan rest = new RestPlan();
        rest.setUser(athlete);
        rest.setCoach(coach);
        rest.setPlanDate(date);
        rest.setTargetSleepHours(targetSleepHours);
        rest.setGuidelines(guidelines);
        return restPlanDao.save(rest);
    }

    @Override
    public TrainingBlock updateTrainingBlockDone(Long userId, Long blockId, Double done) throws InstanceNotFoundException, PermissionException {
        
        TrainingBlock block = trainingBlockDao.findById(blockId)
                .orElseThrow(() -> new InstanceNotFoundException("TrainingBlock", blockId));
        
        if (!block.getTrainingSession().getUser().getId().equals(userId)) {
            throw new PermissionException();
        }
        
        block.setDone(done);
        return trainingBlockDao.save(block);
    }

    @Override
    public NutritionPlan updateNutritionPlanDone(Long userId, Long planId, Double done) throws InstanceNotFoundException, PermissionException {
        
        NutritionPlan nutritionPlan = nutritionPlanDao.findById(planId)
                .orElseThrow(() -> new InstanceNotFoundException("NutritionPlan", planId));
        
        if (!nutritionPlan.getUser().getId().equals(userId)) {
            throw new PermissionException();
        }
        
        nutritionPlan.setDone(done);
        return nutritionPlanDao.save(nutritionPlan);
    }

    @Override
    public RestPlan updateRestPlanDone(Long userId, Long planId, Double done) throws InstanceNotFoundException, PermissionException {
        
        RestPlan restPlan = restPlanDao.findById(planId)
                .orElseThrow(() -> new InstanceNotFoundException("RestPlan", planId));
        
        if (!restPlan.getUser().getId().equals(userId)) {
            throw new PermissionException();
        }
        
        restPlan.setDone(done);
        return restPlanDao.save(restPlan);
    }

    @Override
    public List<DailyPlan> getWeeklyPlan(Long userId, LocalDate startDate) throws InstanceNotFoundException {
        
        List<TrainingSession> allSessions = trainingSessionDao.findByUserIdAndSessionDateBetweenOrderByStartTimeAsc(userId, startDate, startDate.plusDays(6));
        List<NutritionPlan> allNutrition = nutritionPlanDao.findByUserIdAndPlanDateBetween(userId, startDate, startDate.plusDays(6));
        List<RestPlan> allRest = restPlanDao.findByUserIdAndPlanDateBetween(userId, startDate, startDate.plusDays(6));

        List<DailyPlan> weeklyPlan = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(startDate.plusDays(6)); date = date.plusDays(1)) {
            final LocalDate currentDate = date;
            
            List<TrainingSession> dailySessions = allSessions.stream()
                .filter(s -> s.getSessionDate().equals(currentDate))
                .collect(Collectors.toList());
                
            Optional<NutritionPlan> dailyNutrition = allNutrition.stream()
                .filter(n -> n.getPlanDate().equals(currentDate))
                .findFirst();
                
            Optional<RestPlan> dailyRest = allRest.stream()
                .filter(r -> r.getPlanDate().equals(currentDate))
                .findFirst();

            weeklyPlan.add(new DailyPlan(dailySessions, dailyNutrition, dailyRest));
        }

        return weeklyPlan;
    }

    @Override
    public TrainingSession rescheduleTrainingSession(Long userId, Long sessionId, LocalDate newDate, LocalTime newStartTime) throws InstanceNotFoundException, PermissionException {
        
        TrainingSession session = trainingSessionDao.findById(sessionId)
                .orElseThrow(() -> new InstanceNotFoundException("TrainingSession", sessionId));
        
        if (!session.getUser().getId().equals(userId)) {
            throw new PermissionException();
        }
        
        session.setSessionDate(newDate);
        session.setStartTime(newStartTime);
        return trainingSessionDao.save(session);
    }

    @Override
    public DailyPlan getAthleteDailyPlan(Long coachId, Long athleteId, LocalDate date) throws InstanceNotFoundException, PermissionException {
        
        Users athlete = userDao.findById(athleteId)
                .orElseThrow(() -> new InstanceNotFoundException("user", athleteId));
        
        if (athlete.getCoachId() == null || !athlete.getCoachId().equals(coachId)) {
            throw new PermissionException();
        }

        List<TrainingSession> sessions = trainingSessionDao.findByUserIdAndSessionDateOrderByStartTimeAsc(athleteId, date);
        Optional<NutritionPlan> nutrition = nutritionPlanDao.findByUserIdAndPlanDate(athleteId, date);
        Optional<RestPlan> rest = restPlanDao.findByUserIdAndPlanDate(athleteId, date);

        return new DailyPlan(sessions, nutrition, rest);
    }

}

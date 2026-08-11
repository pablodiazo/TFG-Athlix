package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.entities.*;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectRoleException;
import es.udc.fi.dc.fd.model.services.exceptions.PermissionException;
import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PlanService {
    DailyPlan getDailyPlan(Long userId, LocalDate date) throws InstanceNotFoundException;

    TrainingSession createTrainingSession(Long athleteId, Long coachId, LocalDate date, LocalTime startTime, TrainingSession.SportType sportType, String objective, String totalDistanceOrDuration, List<TrainingBlock> blocks) throws InstanceNotFoundException, IncorrectRoleException;

    NutritionPlan createNutritionPlan(Long athleteId, Long coachId, LocalDate date, Integer targetCalories, Integer proteinGrams, Integer carbsGrams, Integer fatGrams, Double hydrationLiters, String guidelines) throws InstanceNotFoundException, IncorrectRoleException, DuplicateInstanceException;

    RestPlan createRestPlan(Long athleteId, Long coachId, LocalDate date, Double targetSleepHours, String guidelines) throws InstanceNotFoundException, IncorrectRoleException, DuplicateInstanceException;

    TrainingBlock updateTrainingBlockDone(Long userId, Long blockId, Double done) throws InstanceNotFoundException, PermissionException;
    
    NutritionPlan updateNutritionPlanDone(Long userId, Long planId, Double done) throws InstanceNotFoundException, PermissionException;
    
    RestPlan updateRestPlanDone(Long userId, Long planId, Double done) throws InstanceNotFoundException, PermissionException;

    List<DailyPlan> getWeeklyPlan(Long userId, LocalDate startDate) throws InstanceNotFoundException;

    void rescheduleTrainingSession(Long userId, Long sessionId, LocalDate newDate, LocalTime newStartTime) throws InstanceNotFoundException, PermissionException;

    DailyPlan getAthleteDailyPlan(Long coachId, Long athleteId, LocalDate date) throws InstanceNotFoundException, PermissionException;

    List<DailyPlan> getAthleteWeeklyPlan(Long coachId, Long athleteId, LocalDate startDate) throws InstanceNotFoundException, PermissionException;

    List<Notification> getNotifications(Long coachId);
    
    void markNotificationAsRead(Long coachId, Long notificationId) throws InstanceNotFoundException, PermissionException;

    TrainingSession acceptReadjustment(Long coachId, Long userId, Long notificationId, Long sessionId, LocalDate newDate, LocalTime newStartTime, Boolean reschedule) throws InstanceNotFoundException, PermissionException;

    void denyReadjustment(Long coachId, Long userId, Long notificationId, Long sessionId, LocalDate newDate, LocalTime newStartTime) throws InstanceNotFoundException, PermissionException;

    Double calculateTSS(Long sessionId) throws InstanceNotFoundException, PermissionException;

    void deleteTrainingSession(Long coachId, Long sessionId) throws InstanceNotFoundException, PermissionException;

    TrainingSession updateTrainingSession(Long coachId, Long sessionId, LocalDate date, LocalTime startTime,
        TrainingSession.SportType sportType, String objective, String totalDistanceOrDuration, List<TrainingBlock> blocks) 
        throws InstanceNotFoundException, PermissionException;
    
    void deleteNutritionPlan(Long coachId, Long planId) throws InstanceNotFoundException, PermissionException;

    NutritionPlan updateNutritionPlan(Long coachId, Long planId, LocalDate planDate, Integer targetCalories, Integer proteinGrams, Integer carbsGrams,
        Integer fatGrams, Double hydrationLiters, String guidelines) throws InstanceNotFoundException, PermissionException;

    void deleteRestPlan(Long coachId, Long planId) throws InstanceNotFoundException, PermissionException;

    RestPlan updateRestPlan(Long coachId, Long planId, LocalDate planDate, Double targetSleepHours, String guidelines) throws InstanceNotFoundException, PermissionException;

    List<DailyPlan> getAthleteMonthlyPlan(Long coachId, Long athleteId, LocalDate startDate, LocalDate endDate) 
        throws InstanceNotFoundException, PermissionException;

    List<DailyPlan> getMonthlyPlan(Long userId, LocalDate startDate, LocalDate endDate) throws InstanceNotFoundException;

    TrainingReplanningProposal markSessionAsFailedAndReplan(Long userId, Long sessionId) throws InstanceNotFoundException, PermissionException;

    List<TrainingSession> acceptProposal(Long coachId, Long proposalId) throws InstanceNotFoundException, PermissionException;

    void denyProposal(Long coachId, Long proposalId) throws InstanceNotFoundException, PermissionException;

    TrainingReplanningProposal getPendingProposalBySessionId(Long userId, Long sessionId) throws InstanceNotFoundException, PermissionException;
}

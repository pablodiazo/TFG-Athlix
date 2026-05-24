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

    TrainingSession rescheduleTrainingSession(Long userId, Long sessionId, LocalDate newDate, LocalTime newStartTime) throws InstanceNotFoundException, PermissionException;
}

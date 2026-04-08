package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.entities.*;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectRoleException;
import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PlanService {
    DailyPlan getDailyPlan(Long userId, LocalDate date) throws InstanceNotFoundException;

    TrainingSession createTrainingSession(Long athleteId, Long coachId, LocalDate date, LocalTime startTime, TrainingSession.SportType sportType, String objective, String totalDistanceOrDuration, List<TrainingBlock> blocks) throws InstanceNotFoundException, IncorrectRoleException;

    NutritionPlan createNutritionPlan(Long athleteId, Long coachId, LocalDate date, Integer targetCalories, Integer proteinGrams, Integer carbsGrams, Integer fatGrams, Double hydrationLiters, String guidelines) throws InstanceNotFoundException, IncorrectRoleException, DuplicateInstanceException;
}

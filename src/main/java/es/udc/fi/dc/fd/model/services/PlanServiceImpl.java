package es.udc.fi.dc.fd.model.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import es.udc.fi.dc.fd.model.common.exceptions.*;
import es.udc.fi.dc.fd.model.services.exceptions.*;
import es.udc.fi.dc.fd.model.entities.*;

import es.udc.fi.dc.fd.rest.dtos.ReplanningDtos.*;

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

    @Autowired
    private NotificationDao notificationDao;

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
        TrainingBlock savedBlock = trainingBlockDao.save(block);
        
        checkAndNotifyDailyCompletion(userId, block.getTrainingSession().getSessionDate());

        return savedBlock;
    }

    @Override
    public NutritionPlan updateNutritionPlanDone(Long userId, Long planId, Double done) throws InstanceNotFoundException, PermissionException {
        
        NutritionPlan nutritionPlan = nutritionPlanDao.findById(planId)
                .orElseThrow(() -> new InstanceNotFoundException("NutritionPlan", planId));
        
        if (!nutritionPlan.getUser().getId().equals(userId)) {
            throw new PermissionException();
        }
        
        nutritionPlan.setDone(done);
        NutritionPlan savedPlan = nutritionPlanDao.save(nutritionPlan);

        checkAndNotifyDailyCompletion(userId, savedPlan.getPlanDate());

        return savedPlan;
    }

    @Override
    public RestPlan updateRestPlanDone(Long userId, Long planId, Double done) throws InstanceNotFoundException, PermissionException {
        
        RestPlan restPlan = restPlanDao.findById(planId)
                .orElseThrow(() -> new InstanceNotFoundException("RestPlan", planId));
        
        if (!restPlan.getUser().getId().equals(userId)) {
            throw new PermissionException();
        }
        
        restPlan.setDone(done);
        RestPlan savedPlan = restPlanDao.save(restPlan);

        checkAndNotifyDailyCompletion(userId, savedPlan.getPlanDate());

        return savedPlan;
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
    public void rescheduleTrainingSession(Long userId, Long sessionId, LocalDate newDate, LocalTime newStartTime) throws InstanceNotFoundException, PermissionException {
        
        TrainingSession session = trainingSessionDao.findById(sessionId)
                .orElseThrow(() -> new InstanceNotFoundException("TrainingSession", sessionId));
        
        if (!session.getUser().getId().equals(userId)) {
            throw new PermissionException();
        }

        checkAndNotifyReadjustment(userId, sessionId, session.getSessionDate(), session.getStartTime(), newDate, newStartTime);

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

    @Override
    public List<DailyPlan> getAthleteWeeklyPlan(Long coachId, Long athleteId, LocalDate startDate) throws InstanceNotFoundException, PermissionException {
        
        Users athlete = userDao.findById(athleteId)
                .orElseThrow(() -> new InstanceNotFoundException("user", athleteId));
        
        if (athlete.getCoachId() == null || !athlete.getCoachId().equals(coachId)) {
            throw new PermissionException();
        }

        List<TrainingSession> allSessions = trainingSessionDao.findByUserIdAndSessionDateBetweenOrderByStartTimeAsc(athleteId, startDate, startDate.plusDays(6));
        List<NutritionPlan> allNutrition = nutritionPlanDao.findByUserIdAndPlanDateBetween(athleteId, startDate, startDate.plusDays(6));
        List<RestPlan> allRest = restPlanDao.findByUserIdAndPlanDateBetween(athleteId, startDate, startDate.plusDays(6));

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

    private void checkAndNotifyReadjustment(Long athleteId, Long sessionId, LocalDate date, LocalTime startTime, LocalDate newDate, LocalTime newStartTime) {
        Users athlete = userDao.findById(athleteId).orElse(null);
        if (athlete == null || athlete.getCoachId() == null) {
            return;
        }

        boolean alreadyNotified = notificationDao.existsByUserIdAndAthleteIdAndPlanDateAndType(
                athlete.getCoachId(), athleteId, date, "RESCHEDULE");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", new Locale("es", "ES"));

        String formattedDate = date.format(formatter);
        String formattedNewDate = newDate.format(formatter);

        String beforeDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);
        String afterDate = formattedNewDate.substring(0, 1).toUpperCase() + formattedNewDate.substring(1);

                
        if (!alreadyNotified) {
            String msg = "Tu atleta " + athlete.getFirstName() + " " + athlete.getLastName() + 
                 " quiere cambiar su entrenamiento del día " + beforeDate + " a las " + startTime.toString() + " al día " + afterDate + " a las " + newStartTime.toString() + ".";
                
            Users coach = userDao.findById(athlete.getCoachId()).orElse(null);
            Notification notification = new Notification(coach, athlete, msg, "RESCHEDULE", date);
            notification.setSessionId(sessionId);
            notification.setNewDate(newDate);
            notification.setNewStartTime(newStartTime);
            notificationDao.save(notification);
        }
        
    }

    private void checkAndNotifyDailyCompletion(Long athleteId, LocalDate date) {
        Users athlete = userDao.findById(athleteId).orElse(null);
        if (athlete == null || athlete.getCoachId() == null) {
            return;
        }

        List<TrainingSession> sessions = trainingSessionDao.findByUserIdAndSessionDateOrderByStartTimeAsc(athleteId, date);
        Optional<NutritionPlan> nutrition = nutritionPlanDao.findByUserIdAndPlanDate(athleteId, date);
        Optional<RestPlan> rest = restPlanDao.findByUserIdAndPlanDate(athleteId, date);

        int totalItems = 0;
        double totalDone = 0.0;

        for (TrainingSession session : sessions) {
            for (TrainingBlock block : session.getBlocks()) {
                totalItems++;
                totalDone += (block.getDone() != null ? block.getDone() : 0.0);
            }
        }
        
        if (nutrition.isPresent()) {
            totalItems++;
            totalDone += (nutrition.get().getDone() != null ? nutrition.get().getDone() : 0.0);
        }
        if (rest.isPresent()) {
            totalItems++;
            totalDone += (rest.get().getDone() != null ? rest.get().getDone() : 0.0);
        }

        if (totalItems > 0 && (totalDone / totalItems) > 0.5) {
            boolean alreadyNotified = notificationDao.existsByUserIdAndAthleteIdAndPlanDateAndType(
                athlete.getCoachId(), athleteId, date, "COMPLETION");
                
            if (!alreadyNotified) {
                String msg = "¡Buenas noticias! Tu atleta " + athlete.getFirstName() + " " + athlete.getLastName() + 
                             " ha completado más del 50% de su planificación.";
                
                Users coach = userDao.findById(athlete.getCoachId()).orElse(null);
                Notification notification = new Notification(coach, athlete, msg, "COMPLETION", date);
                notificationDao.save(notification);
            }
        }
    }

    private void notifyAcceptedReadjustment(Long coachId, Long userId, Long sessionId, LocalDate newDate, LocalTime newStartTime) {
        Users coach = userDao.findById(coachId).orElse(null);
        Users user = userDao.findById(userId).orElse(null);
        
        if (coach == null || user == null) {
            return;
        }

        String msg = "Tu entrendador " + coach.getFirstName() + " " + coach.getLastName() + 
                     " ha aceptado la modificación del entrenamiento del día " + newDate.getDayOfMonth() + " a las " + newStartTime.toString() + ".";
        
        Notification notification = new Notification(user, coach, msg, "ACCEPTED_READJUSTMENT", newDate);
        notificationDao.save(notification);
    }

    private void notifyDeniedReadjustment(Long coachId, Long userId, Long sessionId, LocalDate newDate, LocalTime newStartTime) throws InstanceNotFoundException, PermissionException {
        Users coach = userDao.findById(coachId).orElse(null);
        Users user = userDao.findById(userId).orElse(null);
        
        if (coach == null || user == null) {
            return;
        }

        String msg = "Tu entrendador " + coach.getFirstName() + " " + coach.getLastName() + 
                     " ha denegado la modificación del entrenamiento del día " + newDate.getDayOfMonth() + " a las " + newStartTime.toString() + ".";
        
        Notification notification = new Notification(user, coach, msg, "DENIED_READJUSTMENT", newDate);
        notificationDao.save(notification);
    }

    @Override
    public List<Notification> getNotifications(Long userId) {
        return notificationDao.findByUserIdOrderByIdDesc(userId);
    }

    @Override
    public void markNotificationAsRead(Long userId, Long notificationId) throws InstanceNotFoundException, PermissionException {
        Notification notification = notificationDao.findById(notificationId)
                .orElseThrow(() -> new InstanceNotFoundException("Notification", notificationId));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new PermissionException();
        }
        
        notification.setRead(true);
        notificationDao.save(notification);
    }

    @Override
    public TrainingSession acceptReadjustment(Long coachId, Long userId, Long notificationId, Long sessionId, LocalDate newDate, LocalTime newStartTime, Boolean reschedule) throws InstanceNotFoundException, PermissionException {
        
        if(reschedule) {
            Users coach = userDao.findById(coachId)
                .orElseThrow(() -> new InstanceNotFoundException("user", coachId));
        
            if (coach.getRole() != Users.RoleType.COACH) {
                throw new PermissionException();
            }
            
            Users user = userDao.findById(userId)
                    .orElseThrow(() -> new InstanceNotFoundException("user", userId));
            
            if (!user.getRole().equals(Users.RoleType.USER)) {
                throw new PermissionException();
            }

            Notification notification = notificationDao.findById(notificationId)
                .orElseThrow(() -> new InstanceNotFoundException("Notification", notificationId));
            
            notification.setReviewed(true);
            notificationDao.save(notification);

            TrainingSession session = trainingSessionDao.findById(sessionId)
                    .orElseThrow(() -> new InstanceNotFoundException("TrainingSession", sessionId));
            
            if (!session.getUser().getId().equals(userId)) {
                throw new PermissionException();
            }

            if (session.getCoach().getId() != coachId) {
                throw new PermissionException();
            }
            session.setSessionDate(newDate);
            session.setStartTime(newStartTime);

            notifyAcceptedReadjustment(coachId, userId, sessionId, newDate, newStartTime);

            return trainingSessionDao.save(session);

        } else {

            TrainingSession session = trainingSessionDao.findById(sessionId)
                    .orElseThrow(() -> new InstanceNotFoundException("TrainingSession", sessionId));
            return session;

        }
        
    }

    @Override
    public void denyReadjustment(Long coachId, Long userId, Long notificationId, Long sessionId, LocalDate newDate, LocalTime newStartTime) throws InstanceNotFoundException, PermissionException {
        Notification notification = notificationDao.findById(notificationId)
                .orElseThrow(() -> new InstanceNotFoundException("Notification", notificationId));
        
        if (!notification.getAthlete().getId().equals(userId)) {
            throw new PermissionException();
        }
        
        notification.setReviewed(true);
        notificationDao.save(notification);

        notifyDeniedReadjustment(coachId, userId, sessionId, newDate, newStartTime);

    }

    private double parseDurationToMinutes(String input, TrainingSession.SportType sport) {
        if (input == null || input.equals("-") || input.trim().isEmpty()) {
            return 0.0;
        }

        String lowerInput = input.toLowerCase();
        double minutes = 0.0;

        if (lowerInput.contains("h") || lowerInput.contains("min")) {
            java.util.regex.Matcher hMatcher = java.util.regex.Pattern.compile("(\\d+)\\s*h").matcher(lowerInput);
            if (hMatcher.find()) {
                minutes += Double.parseDouble(hMatcher.group(1)) * 60;
            }
            java.util.regex.Matcher mMatcher = java.util.regex.Pattern.compile("(\\d+)\\s*min").matcher(lowerInput);
            if (mMatcher.find()) {
                minutes += Double.parseDouble(mMatcher.group(1));
            }
            return minutes;
        }

        double distanceKm = 0.0;
        if (lowerInput.contains("km")) {
            java.util.regex.Matcher kmMatcher = java.util.regex.Pattern.compile("([\\d.]+)\\s*km").matcher(lowerInput);
            if (kmMatcher.find()) {
                distanceKm = Double.parseDouble(kmMatcher.group(1));
            }
        } else if (lowerInput.contains("m")) {
            java.util.regex.Matcher mMatcher = java.util.regex.Pattern.compile("([\\d.]+)\\s*m").matcher(lowerInput);
            if (mMatcher.find()) {
                distanceKm = Double.parseDouble(mMatcher.group(1)) / 1000.0;
            }
        }

        if (distanceKm > 0) {
            if (sport == TrainingSession.SportType.RUN)return distanceKm * 5.0;
            if (sport == TrainingSession.SportType.BIKE)return distanceKm * 2.0;
            if (sport == TrainingSession.SportType.SWIM)return distanceKm * 17.5;
        }

        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public Double calculateTSS(Long sessionId) throws InstanceNotFoundException, PermissionException {
        TrainingSession session = trainingSessionDao.findById(sessionId)
                .orElseThrow(() -> new InstanceNotFoundException("TrainingSession", sessionId));

        double tss = 0.0;

        for (TrainingBlock block : session.getBlocks()) {
            double intensity = 0.0;
            IntensityZone pace = block.getPace() != null ? block.getPace() : IntensityZone.NONE;

            if (session.getSport() == TrainingSession.SportType.SWIM) {
                switch (pace) {
                    case SUAVE: intensity = 0.60; break;
                    case AER1:  intensity = 0.75; break;
                    case AER2:  intensity = 0.85; break;
                    case AER3:  intensity = 1.00; break;
                    case FUERTE: intensity = 1.15; break;
                    default: intensity = 0.0; break;
                }
            } else if (session.getSport() == TrainingSession.SportType.BIKE) {
                switch (pace) {
                    case Z1: intensity = 0.50; break;
                    case Z2: intensity = 0.65; break;
                    case Z3: intensity = 0.83; break;
                    case Z4: intensity = 1.00; break;
                    case Z5: intensity = 1.13; break;
                    case Z6: intensity = 1.35; break;
                    case Z7: intensity = 1.50; break;
                    default: intensity = 0.0; break;
                }
            } else if (session.getSport() == TrainingSession.SportType.RUN) {
                switch (pace) {
                    case R0:       intensity = 0.55; break;
                    case R1:       intensity = 0.65; break;
                    case R1_PLUS:  intensity = 0.70; break;
                    case R2:       intensity = 0.75; break;
                    case R3:       intensity = 0.85; break;
                    case R3_PLUS:  intensity = 0.92; break;
                    case R4:       intensity = 1.00; break;
                    case R5:       intensity = 1.10; break;
                    case R6:       intensity = 1.25; break;
                    default:       intensity = 0.0;  break;
                }
            }

            double baseMinutes = parseDurationToMinutes(block.getDistanceOrDuration(), session.getSport());
            
            int sets = block.getSets() != null && block.getSets() > 0 ? block.getSets() : 1;
            int reps = block.getReps() != null && block.getReps() > 0 ? block.getReps() : 1;
            double totalBlockMinutes = baseMinutes * sets * reps;
            if (intensity > 0 && totalBlockMinutes > 0) {
                tss += (totalBlockMinutes / 60.0) * (intensity * intensity) * 100.0;
            }
        }

        return tss;
    }

    @Override
    public void deleteTrainingSession(Long coachId, Long sessionId) throws InstanceNotFoundException, PermissionException {
        TrainingSession session = trainingSessionDao.findById(sessionId)
                .orElseThrow(() -> new InstanceNotFoundException("TrainingSession", sessionId));

        if (!session.getCoach().getId().equals(coachId)) {
            throw new PermissionException();
        }

        trainingSessionDao.delete(session);
    }

    @Override
    public TrainingSession updateTrainingSession(Long coachId, Long sessionId, LocalDate date, LocalTime startTime,
        TrainingSession.SportType sportType, String objective, String totalDistanceOrDuration, List<TrainingBlock> blocks) 
        throws InstanceNotFoundException, PermissionException {
        
        TrainingSession session = trainingSessionDao.findById(sessionId)
                .orElseThrow(() -> new InstanceNotFoundException("TrainingSession", sessionId));

        if (!session.getCoach().getId().equals(coachId)) {
            throw new PermissionException();
        }

        session.setSessionDate(date);
        session.setStartTime(startTime);
        session.setSport(sportType);
        session.setObjective(objective);
        session.setTotalDistanceOrDuration(totalDistanceOrDuration);

        if (session.getBlocks() != null && !session.getBlocks().isEmpty()) {
            trainingBlockDao.deleteAll(session.getBlocks());
            session.getBlocks().clear();
        }

        for (TrainingBlock block : blocks) {
            block.setTrainingSession(session);
            session.addBlock(block);
        }
        
        return trainingSessionDao.save(session);
    }

    @Override
    public void deleteNutritionPlan(Long coachId, Long planId) throws InstanceNotFoundException, PermissionException {
        NutritionPlan plan = nutritionPlanDao.findById(planId)
                .orElseThrow(() -> new InstanceNotFoundException("NutritionPlan", planId));
        
        if (!plan.getCoach().getId().equals(coachId)) {
            throw new PermissionException();
        }

        nutritionPlanDao.delete(plan);
    }

    @Override
    public NutritionPlan updateNutritionPlan(Long coachId, Long planId, LocalDate planDate, Integer targetCalories, Integer proteinGrams, Integer carbsGrams,
        Integer fatGrams, Double hydrationLiters, String guidelines) throws InstanceNotFoundException, PermissionException {
        
        NutritionPlan plan = nutritionPlanDao.findById(planId)
                .orElseThrow(() -> new InstanceNotFoundException("NutritionPlan", planId));
        
        if (!plan.getCoach().getId().equals(coachId)) {
            throw new PermissionException();
        }

        plan.setPlanDate(planDate);;
        plan.setTargetCalories(targetCalories);
        plan.setProteinGrams(proteinGrams);
        plan.setCarbsGrams(carbsGrams);
        plan.setFatGrams(fatGrams);
        plan.setHydrationLiters(hydrationLiters);
        plan.setGuidelines(guidelines);
        
        return nutritionPlanDao.save(plan);
    }

    @Override
    public void deleteRestPlan(Long coachId, Long planId) throws InstanceNotFoundException, PermissionException {
        RestPlan plan = restPlanDao.findById(planId)
                .orElseThrow(() -> new InstanceNotFoundException("RestPlan", planId));
        
        if (!plan.getCoach().getId().equals(coachId)) {
            throw new PermissionException();
        }

        restPlanDao.delete(plan);
    }

    @Override
    public RestPlan updateRestPlan(Long coachId, Long planId, LocalDate planDate, Double targetSleepHours, String guidelines) throws InstanceNotFoundException, PermissionException {
        
        RestPlan plan = restPlanDao.findById(planId)
                .orElseThrow(() -> new InstanceNotFoundException("RestPlan", planId));
        
        if (!plan.getCoach().getId().equals(coachId)) {
            throw new PermissionException();
        }

        plan.setPlanDate(planDate);;
        plan.setTargetSleepHours(targetSleepHours);
        plan.setGuidelines(guidelines);
        
        return restPlanDao.save(plan);
    }

    @Override
    public List<DailyPlan> getAthleteMonthlyPlan(Long coachId, Long athleteId, LocalDate startDate, LocalDate endDate) throws InstanceNotFoundException, PermissionException {
        
        Users athlete = userDao.findById(athleteId)
                .orElseThrow(() -> new InstanceNotFoundException("user", athleteId));
        
        if (athlete.getCoachId() == null || !athlete.getCoachId().equals(coachId)) {
            throw new PermissionException();
        }

        List<TrainingSession> allSessions = trainingSessionDao.findByUserIdAndSessionDateBetweenOrderByStartTimeAsc(athleteId, startDate, endDate);
        List<NutritionPlan> allNutrition = nutritionPlanDao.findByUserIdAndPlanDateBetween(athleteId, startDate, endDate);
        List<RestPlan> allRest = restPlanDao.findByUserIdAndPlanDateBetween(athleteId, startDate, endDate);

        List<DailyPlan> monthlyPlan = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
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

            monthlyPlan.add(new DailyPlan(dailySessions, dailyNutrition, dailyRest));
        }

        return monthlyPlan;
    }

    @Override
    public List<DailyPlan> getMonthlyPlan(Long userId, LocalDate startDate, LocalDate endDate) throws InstanceNotFoundException {
        
        if (!userDao.existsById(userId)) {
            throw new InstanceNotFoundException("user", userId);
        }

        List<TrainingSession> allSessions = trainingSessionDao.findByUserIdAndSessionDateBetweenOrderByStartTimeAsc(userId, startDate, endDate);
        List<NutritionPlan> allNutrition = nutritionPlanDao.findByUserIdAndPlanDateBetween(userId, startDate, endDate);
        List<RestPlan> allRest = restPlanDao.findByUserIdAndPlanDateBetween(userId, startDate, endDate);

        List<DailyPlan> monthlyPlan = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
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

            monthlyPlan.add(new DailyPlan(dailySessions, dailyNutrition, dailyRest));
        }

        return monthlyPlan;
    }

    @Override
    public List<TrainingSession> markSessionAsFailedAndReplan(Long userId, Long sessionId) throws InstanceNotFoundException, PermissionException {
        
        TrainingSession failedSession = trainingSessionDao.findById(sessionId)
                .orElseThrow(() -> new InstanceNotFoundException("TrainingSession", sessionId));
        
        if (!failedSession.getUser().getId().equals(userId)) {
            throw new PermissionException();
        }

        double failedTss = calculateTSS(sessionId);

        LocalDate failedDate = failedSession.getSessionDate();
        LocalDate endOfWeek = failedDate.with(java.time.DayOfWeek.SUNDAY);
        List<TrainingSession> futureSessions = trainingSessionDao.findByUserIdAndSessionDateBetweenOrderByStartTimeAsc(
                userId, failedDate.plusDays(1), endOfWeek);

        ContextApiRequest context = new ContextApiRequest(450.0, failedTss); 
        
        List<BlockApiRequest> failedBlocksReq = failedSession.getBlocks().stream()
                .map(b -> new BlockApiRequest(b.getName(), b.getDistanceOrDuration(), b.getPace().name(), b.getSets(), b.getReps(), b.getRest()))
                .collect(Collectors.toList());
        SessionApiRequest failedSessionReq = new SessionApiRequest(
                failedSession.getSessionDate().toString(), failedSession.getSport().name(), failedTss, failedBlocksReq);

        List<SessionApiRequest> adjustableSessionsReq = futureSessions.stream().map(s -> {
            try {
                double tss = calculateTSS(s.getId());
                List<BlockApiRequest> blocksReq = s.getBlocks().stream()
                    .map(b -> new BlockApiRequest(b.getName(), b.getDistanceOrDuration(), b.getPace().name(), b.getSets(), b.getReps(), b.getRest()))
                    .collect(Collectors.toList());
                return new SessionApiRequest(s.getSessionDate().toString(), s.getSport().name(), tss, blocksReq);
            } catch (Exception e) {
                return null;
            }
        }).filter(java.util.Objects::nonNull).collect(Collectors.toList());

        ReplanApiRequest requestPayload = new ReplanApiRequest(context, failedSessionReq, adjustableSessionsReq);

        RestTemplate restTemplate = new RestTemplate();
        String pythonUrl = "http://localhost:8000/api/replan";
        PlanReadjustmentApiResponse aiResponse = restTemplate.postForObject(pythonUrl, requestPayload, PlanReadjustmentApiResponse.class);

        List<TrainingSession> modifiedSessions = new ArrayList<>();
        if (aiResponse != null) {
            modifiedSessions = applyReadjustmentToDatabase(aiResponse, futureSessions, failedSession);
        }

        return modifiedSessions;
    }

    private List<TrainingSession> applyReadjustmentToDatabase(PlanReadjustmentApiResponse aiResponse, List<TrainingSession> futureSessions, TrainingSession failedSession) {
        List<TrainingSession> result = new ArrayList<>();

        for (UpdatedSessionApiResponse updatedResp : aiResponse.getUpdatedSessions()) {
            for (TrainingSession originalSession : futureSessions) {
                if (originalSession.getSessionDate().toString().equals(updatedResp.getDate()) && originalSession.getSport().name().equals(updatedResp.getSport())) {
                    
                    if (originalSession.getBlocks() != null) {
                        trainingBlockDao.deleteAll(originalSession.getBlocks());
                        originalSession.getBlocks().clear();
                    }

                    int blockOrder = 1;
                    for (UpdatedBlockApiResponse uBlock : updatedResp.getUpdatedBlocks()) {
                        TrainingBlock newBlock = new TrainingBlock();
                        newBlock.setBlockOrder(blockOrder++);
                        newBlock.setName(uBlock.getName());
                        newBlock.setDistanceOrDuration(uBlock.getDistanceOrDuration());
                        String rawPace = uBlock.getPace().replace("+", "_PLUS");
                        newBlock.setPace(es.udc.fi.dc.fd.model.entities.IntensityZone.valueOf(rawPace));
                        newBlock.setSets(uBlock.getSets() != null ? uBlock.getSets() : 1);
                        newBlock.setReps(uBlock.getReps() != null ? uBlock.getReps() : 1);
                        newBlock.setRest(uBlock.getRest() != null ? uBlock.getRest() : "");
                        newBlock.setTrainingSession(originalSession);
                        originalSession.addBlock(newBlock);
                    }
                    result.add(trainingSessionDao.save(originalSession));
                }
            }
        }

        if (aiResponse.getRescheduledSession() != null) {
            RescheduledSessionApiResponse resch = aiResponse.getRescheduledSession();
            
            TrainingSession newSession = new TrainingSession();
            newSession.setUser(failedSession.getUser());
            newSession.setCoach(failedSession.getCoach());
            newSession.setSessionDate(LocalDate.parse(resch.getNewDate()));
            newSession.setStartTime(failedSession.getStartTime());
            newSession.setSport(failedSession.getSport());
            newSession.setObjective("Sesión recolocada automáticamente");
            
            int blockOrder = 1;
            for (UpdatedBlockApiResponse rBlock : resch.getBlocks()) {
                TrainingBlock newBlock = new TrainingBlock();
                newBlock.setBlockOrder(blockOrder++);
                newBlock.setName(rBlock.getName());
                newBlock.setDistanceOrDuration(rBlock.getDistanceOrDuration());
                String rawPace = rBlock.getPace().replace("+", "_PLUS");
                newBlock.setPace(es.udc.fi.dc.fd.model.entities.IntensityZone.valueOf(rawPace));
                newBlock.setSets(rBlock.getSets() != null ? rBlock.getSets() : 1);
                newBlock.setReps(rBlock.getReps() != null ? rBlock.getReps() : 1);
                newBlock.setRest(rBlock.getRest() != null ? rBlock.getRest() : "");
                newBlock.setTrainingSession(newSession);
                newSession.addBlock(newBlock);
            }
            result.add(trainingSessionDao.save(newSession));
        }

        return result;
    }

}

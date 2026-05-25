package es.udc.fi.dc.fd.model.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.*;
import es.udc.fi.dc.fd.model.entities.Users.RoleType;
import es.udc.fi.dc.fd.model.services.exceptions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PlanServiceTest {

    @Autowired
    private PlanService planService;

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

    private Users createUser(String userName, RoleType role) {
        Users user = new Users(userName, "password", "firstName", "lastName", 
                userName + "@" + userName + ".com", role, null);
        return userDao.save(user);
    }

    private TrainingSession createAndSaveTrainingSession(Users athlete, Users coach, LocalDate date, String objective) {
        TrainingSession session = new TrainingSession();
        session.setUser(athlete);
        session.setCoach(coach);
        session.setSessionDate(date);
        session.setStartTime(LocalTime.of(7, 0));
        session.setSport(TrainingSession.SportType.SWIM);
        session.setObjective(objective);
        return trainingSessionDao.save(session);
    }

    private NutritionPlan createAndSaveNutritionPlan(Users athlete, Users coach, LocalDate date, int targetCalories) {
        NutritionPlan nutrition = new NutritionPlan();
        nutrition.setUser(athlete);
        nutrition.setCoach(coach);
        nutrition.setPlanDate(date);
        nutrition.setTargetCalories(targetCalories);
        return nutritionPlanDao.save(nutrition);
    }
    
    private RestPlan createAndSaveRestPlan(Users athlete, Users coach, LocalDate date, double targetSleepHours) {
        RestPlan rest = new RestPlan();
        rest.setUser(athlete);
        rest.setCoach(coach);
        rest.setPlanDate(date);
        rest.setTargetSleepHours(targetSleepHours);
        return restPlanDao.save(rest);
    }

    @Test
    public void testGetDailyPlan_WithFullData() throws InstanceNotFoundException {
        Users athlete = createUser("athleteFull", RoleType.USER);
        Users coach = createUser("coachFull", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 17);

        TrainingSession savedSession = createAndSaveTrainingSession(athlete, coach, testDate, "Aeróbico");
        createAndSaveNutritionPlan(athlete, coach, testDate, 3000);
        createAndSaveRestPlan(athlete, coach, testDate, 8.5);

        DailyPlan dailyPlan = planService.getDailyPlan(athlete.getId(), testDate);

        assertEquals(1, dailyPlan.getSessions().size());
        assertEquals(savedSession.getId(), dailyPlan.getSessions().get(0).getId());

        assertTrue(dailyPlan.getNutrition().isPresent());
        assertEquals(Integer.valueOf(3000), dailyPlan.getNutrition().get().getTargetCalories());

        assertTrue(dailyPlan.getRest().isPresent());
        assertEquals(Double.valueOf(8.5), dailyPlan.getRest().get().getTargetSleepHours());
    }

    @Test
    public void testGetDailyPlan_EmptyDay() throws InstanceNotFoundException {
        Users athlete = createUser("athleteEmpty", RoleType.USER);
        LocalDate testDate = LocalDate.of(2026, 3, 18);

        DailyPlan dailyPlan = planService.getDailyPlan(athlete.getId(), testDate);

        assertEquals(0, dailyPlan.getSessions().size());
        assertFalse(dailyPlan.getNutrition().isPresent());
        assertFalse(dailyPlan.getRest().isPresent());
    }

    @Test
    public void testGetDailyPlan_PartialData() throws InstanceNotFoundException {
        Users athlete = createUser("athletePartial", RoleType.USER);
        Users coach = createUser("coachPartial", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 19);

        createAndSaveTrainingSession(athlete, coach, testDate, "Series Pista");

        DailyPlan dailyPlan = planService.getDailyPlan(athlete.getId(), testDate);

        assertEquals(1, dailyPlan.getSessions().size());
        assertFalse(dailyPlan.getNutrition().isPresent());
        assertFalse(dailyPlan.getRest().isPresent());
    }

    @Test
    public void testCreateTrainingSession() throws InstanceNotFoundException, IncorrectRoleException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 20);

        TrainingBlock block = new TrainingBlock();
        block.setBlockOrder(1);
        block.setName("Calentamiento");
        block.setSets(1);
        block.setReps(1);
        block.setDistanceOrDuration("600m");
        block.setPace("0");
        block.setRest("0");

        List<TrainingBlock> blocks = List.of(block);

        TrainingSession savedSession = planService.createTrainingSession(athlete.getId(), coach.getId(), testDate, 
                LocalTime.of(7, 0), TrainingSession.SportType.SWIM, "Aeróbico", "600m", blocks);

        assertEquals(1, savedSession.getBlocks().size());
        assertEquals(Integer.valueOf(1), savedSession.getBlocks().get(0).getBlockOrder());
        assertEquals("Calentamiento", savedSession.getBlocks().get(0).getName());
        assertEquals(Integer.valueOf(1), savedSession.getBlocks().get(0).getSets());
        assertEquals(Integer.valueOf(1), savedSession.getBlocks().get(0).getReps());
        assertEquals("600m", savedSession.getBlocks().get(0).getDistanceOrDuration());
        assertEquals("0", savedSession.getBlocks().get(0).getPace());
        assertEquals("0", savedSession.getBlocks().get(0).getRest());
    }

    @Test
    public void testCreateTrainingSession_WithIncorrectRole() throws InstanceNotFoundException, IncorrectRoleException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users notCoach = createUser("notCoach", RoleType.USER);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        TrainingBlock block = new TrainingBlock();
        block.setBlockOrder(1);
        block.setName("Calentamiento");
        block.setSets(1);
        block.setReps(1);
        block.setDistanceOrDuration("600m");
        block.setPace("0");
        block.setRest("0");

        List<TrainingBlock> blocks = List.of(block);

        assertThrows (IncorrectRoleException.class, () -> {
            planService.createTrainingSession(athlete.getId(), notCoach.getId(), testDate, 
                    LocalTime.of(7, 0), TrainingSession.SportType.SWIM, "Aeróbico", "600m", blocks);
        });
    }

    @Test
    public void testCreateTrainingSession_InstanceNotFound() throws InstanceNotFoundException, IncorrectRoleException {
        Users athlete = createUser("athlete", RoleType.USER);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        TrainingBlock block = new TrainingBlock();
        block.setBlockOrder(1);
        block.setName("Calentamiento");
        block.setSets(1);
        block.setReps(1);
        block.setDistanceOrDuration("600m");
        block.setPace("0");
        block.setRest("0");

        List<TrainingBlock> blocks = List.of(block);

        assertThrows (InstanceNotFoundException.class, () -> {
            planService.createTrainingSession(athlete.getId(), -1L, testDate,
                    LocalTime.of(7, 0), TrainingSession.SportType.SWIM, "Aeróbico", "600m", blocks);
        });
    }

    @Test
    public void testCreateTrainingSession_AthleteNotFound() throws InstanceNotFoundException, IncorrectRoleException {
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        TrainingBlock block = new TrainingBlock();
        block.setBlockOrder(1);
        block.setName("Calentamiento");
        block.setSets(1);
        block.setReps(1);
        block.setDistanceOrDuration("600m");
        block.setPace("0");
        block.setRest("0");

        List<TrainingBlock> blocks = List.of(block);

        assertThrows (InstanceNotFoundException.class, () -> {
            planService.createTrainingSession(-1L,coach.getId(), testDate,
                    LocalTime.of(7, 0), TrainingSession.SportType.SWIM, "Aeróbico", "600m", blocks);
        });
    }

    @Test
    public void testCreateTrainingSession_AthleteWithIncorrectRole() throws InstanceNotFoundException, IncorrectRoleException {
        Users notAthlete = createUser("athlete", RoleType.COACH);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        TrainingBlock block = new TrainingBlock();
        block.setBlockOrder(1);
        block.setName("Calentamiento");
        block.setSets(1);
        block.setReps(1);
        block.setDistanceOrDuration("600m");
        block.setPace("0");
        block.setRest("0");

        List<TrainingBlock> blocks = List.of(block);

        assertThrows (IncorrectRoleException.class, () -> {
            planService.createTrainingSession(notAthlete.getId(), coach.getId(), testDate, 
                    LocalTime.of(7, 0), TrainingSession.SportType.SWIM, "Aeróbico", "600m", blocks);
        });
    }


    @Test
    public void testCreateNutritionPlan_WithIncorrectRole() throws InstanceNotFoundException, IncorrectRoleException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users notCoach = createUser("notCoach", RoleType.USER);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        assertThrows (IncorrectRoleException.class, () -> {
            planService.createNutritionPlan(athlete.getId(), notCoach.getId(), testDate, 3000, 100, 200, 300, 1.0, "guidelines");
        });

    }

    @Test
    public void testCreateNutritionPlan_InstanceNotFound() throws InstanceNotFoundException, IncorrectRoleException {
        Users athlete = createUser("athlete", RoleType.USER);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        assertThrows (InstanceNotFoundException.class, () -> {
            planService.createNutritionPlan(athlete.getId(), -1L, testDate, 3000, 100, 200, 300, 1.0, "guidelines");
        });

    }

    @Test
    public void testCreateNutritionPlan_AthleteNotFound() throws InstanceNotFoundException, IncorrectRoleException {
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        assertThrows (InstanceNotFoundException.class, () -> {
            planService.createNutritionPlan(-1L, coach.getId(), testDate, 3000, 100, 200, 300, 1.0, "guidelines");
        });

    }

    @Test
    public void testCreateNutritionPlan_AthleteWithIncorrectRole() throws InstanceNotFoundException, IncorrectRoleException {
        Users notAthlete = createUser("athlete", RoleType.COACH);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        assertThrows (IncorrectRoleException.class, () -> {
            planService.createNutritionPlan(notAthlete.getId(), coach.getId(), testDate, 3000, 100, 200, 300, 1.0, "guidelines");
        });
    }

    @Test
    public void testCreateNutritionPlan_DuplicateInstance() throws InstanceNotFoundException, IncorrectRoleException, DuplicateInstanceException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        planService.createNutritionPlan(athlete.getId(), coach.getId(), testDate, 3000, 100, 200, 300, 1.0, "guidelines");

        assertThrows (DuplicateInstanceException.class, () -> {
            planService.createNutritionPlan(athlete.getId(), coach.getId(), testDate, 3000, 100, 200, 300, 1.0, "guidelines");
        });
    }

    @Test
    public void testCreateNutritionPlan() throws InstanceNotFoundException, IncorrectRoleException, DuplicateInstanceException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        NutritionPlan nutritionPlan = planService.createNutritionPlan(athlete.getId(), coach.getId(), testDate, 3000, 100, 200, 300, 1.0, "guidelines");

        assertEquals(athlete.getId(), nutritionPlan.getUser().getId());
        assertEquals(coach.getId(), nutritionPlan.getCoach().getId());
        assertEquals(testDate, nutritionPlan.getPlanDate());
        assertEquals(Integer.valueOf(3000), nutritionPlan.getTargetCalories());
        assertEquals(Integer.valueOf(100), nutritionPlan.getProteinGrams());
        assertEquals(Integer.valueOf(200), nutritionPlan.getCarbsGrams());
        assertEquals(Integer.valueOf(300), nutritionPlan.getFatGrams());
        assertEquals(Double.valueOf(1.0), nutritionPlan.getHydrationLiters());
        assertEquals("guidelines", nutritionPlan.getGuidelines());
    }
    
    @Test
    public void testCreateRestPlan_WithIncorrectRole() throws InstanceNotFoundException, IncorrectRoleException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users notCoach = createUser("notCoach", RoleType.USER);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        assertThrows (IncorrectRoleException.class, () -> {
            planService.createRestPlan(athlete.getId(), notCoach.getId(), testDate, 8.45, "guidelines");
        });

    }

    @Test
    public void testCreateRestPlan_InstanceNotFound() throws InstanceNotFoundException, IncorrectRoleException {
        Users athlete = createUser("athlete", RoleType.USER);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        assertThrows (InstanceNotFoundException.class, () -> {
            planService.createRestPlan(athlete.getId(), -1L, testDate, 8.45, "guidelines");
        });

    }

    @Test
    public void testCreateRestPlan_AthleteNotFound() throws InstanceNotFoundException, IncorrectRoleException {
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        assertThrows (InstanceNotFoundException.class, () -> {
            planService.createRestPlan(-1L, coach.getId(), testDate, 8.45, "guidelines");
        });

    }

    @Test
    public void testCreateRestPlan_AthleteWithIncorrectRole() throws InstanceNotFoundException, IncorrectRoleException {
        Users notAthlete = createUser("athlete", RoleType.COACH);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        assertThrows (IncorrectRoleException.class, () -> {
            planService.createRestPlan(notAthlete.getId(), coach.getId(), testDate, 8.45, "guidelines");
        });
    }

    @Test
    public void testCreateRestPlan_DuplicateInstance() throws InstanceNotFoundException, IncorrectRoleException, DuplicateInstanceException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        planService.createRestPlan(athlete.getId(), coach.getId(), testDate, 8.45, "guidelines");

        assertThrows (DuplicateInstanceException.class, () -> {
            planService.createRestPlan(athlete.getId(), coach.getId(), testDate, 8.45, "guidelines");
        });
    }

    @Test
    public void testCreateRestPlan() throws InstanceNotFoundException, IncorrectRoleException, DuplicateInstanceException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 21);

        RestPlan restPlan = planService.createRestPlan(athlete.getId(), coach.getId(), testDate, 8.45, "guidelines");

        assertEquals(athlete.getId(), restPlan.getUser().getId());
        assertEquals(coach.getId(), restPlan.getCoach().getId());
        assertEquals(testDate, restPlan.getPlanDate());
        assertEquals(Double.valueOf(8.45), restPlan.getTargetSleepHours());
        assertEquals("guidelines", restPlan.getGuidelines());
    }

    @Test
    public void testUpdateTrainingBlockDone() throws InstanceNotFoundException, PermissionException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 22);

        TrainingSession session = createAndSaveTrainingSession(athlete, coach, testDate, "Aeróbico");

        TrainingBlock block = new TrainingBlock();
        block.setBlockOrder(1);
        block.setName("Calentamiento");
        block.setSets(1);
        block.setReps(1);
        block.setDistanceOrDuration("600m");
        block.setPace("0");
        block.setRest("0");
        block.setTrainingSession(session);
        trainingBlockDao.save(block);

        TrainingBlock updatedBlock = planService.updateTrainingBlockDone(athlete.getId(), block.getId(), 1.0);

        assertEquals(Double.valueOf(1.0), updatedBlock.getDone());
    }

    @Test
    public void testUpdateTrainingBlockDone_WithNoPermission() throws InstanceNotFoundException, PermissionException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 22);

        TrainingSession session = createAndSaveTrainingSession(athlete, coach, testDate, "Aeróbico");

        TrainingBlock block = new TrainingBlock();
        block.setBlockOrder(1);
        block.setName("Calentamiento");
        block.setSets(1);
        block.setReps(1);
        block.setDistanceOrDuration("600m");
        block.setPace("0");
        block.setRest("0");
        block.setTrainingSession(session);
        trainingBlockDao.save(block);

        assertThrows(PermissionException.class, () -> {
            planService.updateTrainingBlockDone(-1L, block.getId(), 1.0);
        });
    }

    @Test
    public void testUpdateTrainingBlockDone_InstanceNotFound() throws InstanceNotFoundException, PermissionException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 22);

        TrainingSession session = createAndSaveTrainingSession(athlete, coach, testDate, "Aeróbico");

        TrainingBlock block = new TrainingBlock();
        block.setBlockOrder(1);
        block.setName("Calentamiento");
        block.setSets(1);
        block.setReps(1);
        block.setDistanceOrDuration("600m");
        block.setPace("0");
        block.setRest("0");
        block.setTrainingSession(session);
        trainingBlockDao.save(block);

        assertThrows(InstanceNotFoundException.class, () -> {
            planService.updateTrainingBlockDone(athlete.getId(), -1L, 1.0);
        });
    }
    
    @Test
    public void testUpdateNutritionPlanDone() throws InstanceNotFoundException, PermissionException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 22);

        NutritionPlan nutritionPlan = createAndSaveNutritionPlan(athlete, coach, testDate, 3000);


        NutritionPlan updatedPlan = planService.updateNutritionPlanDone(athlete.getId(), nutritionPlan.getId(), 1.0);

        assertEquals(Double.valueOf(1.0), updatedPlan.getDone());
    }

    @Test
    public void testUpdateNutritionPlanDone_WithNoPermission() throws InstanceNotFoundException, PermissionException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 22);

        NutritionPlan nutritionPlan = createAndSaveNutritionPlan(athlete, coach, testDate, 3000);

        assertThrows(PermissionException.class, () -> {
            planService.updateNutritionPlanDone(-1L, nutritionPlan.getId(), 1.0);
        });
    }

    @Test
    public void testUpdateNutritionPlanDone_InstanceNotFound() throws InstanceNotFoundException, PermissionException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 22);

        createAndSaveNutritionPlan(athlete, coach, testDate, 3000);


        assertThrows(InstanceNotFoundException.class, () -> {
            planService.updateNutritionPlanDone(athlete.getId(), -1L, 1.0);
        });
    }

    @Test
    public void testUpdateRestPlanDone() throws InstanceNotFoundException, PermissionException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 22);

        RestPlan restPlan = createAndSaveRestPlan(athlete, coach, testDate, 8.45);


        RestPlan updatedPlan = planService.updateRestPlanDone(athlete.getId(), restPlan.getId(), 1.0);

        assertEquals(Double.valueOf(1.0), updatedPlan.getDone());
    }

    @Test
    public void testUpdateRestPlanDone_WithNoPermission() throws InstanceNotFoundException, PermissionException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 22);

        RestPlan restPlan = createAndSaveRestPlan(athlete, coach, testDate, 8.45);

        assertThrows(PermissionException.class, () -> {
            planService.updateRestPlanDone(-1L, restPlan.getId(), 1.0);
        });
    }

    @Test
    public void testUpdateRestPlanDone_InstanceNotFound() throws InstanceNotFoundException, PermissionException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 22);

        createAndSaveRestPlan(athlete, coach, testDate, 8.45);


        assertThrows(InstanceNotFoundException.class, () -> {
            planService.updateRestPlanDone(athlete.getId(), -1L, 1.0);
        });
    }

    @Test
    public void testGetWeeklyPlan_WithData() throws InstanceNotFoundException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        
        LocalDate startDate = LocalDate.of(2026, 3, 16); 

        createAndSaveTrainingSession(athlete, coach, startDate, "Run Lunes");
        createAndSaveNutritionPlan(athlete, coach, startDate, 2500);

        createAndSaveTrainingSession(athlete, coach, startDate.plusDays(3), "Swim Jueves AM");
        createAndSaveTrainingSession(athlete, coach, startDate.plusDays(3), "Fuerza Jueves PM");
        createAndSaveRestPlan(athlete, coach, startDate.plusDays(3), 8.5);

        createAndSaveNutritionPlan(athlete, coach, startDate.plusDays(6), 4000);
        createAndSaveRestPlan(athlete, coach, startDate.plusDays(6), 10.0);

        List<DailyPlan> weeklyPlan = planService.getWeeklyPlan(athlete.getId(), startDate);

        assertEquals(7, weeklyPlan.size());

        assertEquals(1, weeklyPlan.get(0).getSessions().size());
        assertTrue(weeklyPlan.get(0).getNutrition().isPresent());
        assertFalse(weeklyPlan.get(0).getRest().isPresent());

        assertEquals(0, weeklyPlan.get(1).getSessions().size());
        assertFalse(weeklyPlan.get(1).getNutrition().isPresent());
        assertFalse(weeklyPlan.get(1).getRest().isPresent());

        assertEquals(2, weeklyPlan.get(3).getSessions().size());
        assertFalse(weeklyPlan.get(3).getNutrition().isPresent());
        assertTrue(weeklyPlan.get(3).getRest().isPresent());

        assertEquals(0, weeklyPlan.get(6).getSessions().size());
        assertTrue(weeklyPlan.get(6).getNutrition().isPresent());
        assertTrue(weeklyPlan.get(6).getRest().isPresent());
    }

    @Test
    public void testGetWeeklyPlan_EmptyWeek() throws InstanceNotFoundException {
        Users athlete = createUser("athlete", RoleType.USER);
        LocalDate startDate = LocalDate.of(2026, 3, 16);

        List<DailyPlan> weeklyPlan = planService.getWeeklyPlan(athlete.getId(), startDate);

        assertEquals(7, weeklyPlan.size());

        for (DailyPlan dailyPlan : weeklyPlan) {
            assertEquals(0, dailyPlan.getSessions().size());
            assertFalse(dailyPlan.getNutrition().isPresent());
            assertFalse(dailyPlan.getRest().isPresent());
        }
    }

    @Test
    public void testGetWeeklyPlan_CheckLimits() throws InstanceNotFoundException {
        Users athlete = createUser("athlete", RoleType.USER);
        Users coach = createUser("coach", RoleType.COACH);
        
        LocalDate startDate = LocalDate.of(2026, 3, 16);
        
        createAndSaveTrainingSession(athlete, coach, startDate.minusDays(1), "Domingo Anterior");
        createAndSaveTrainingSession(athlete, coach, startDate.plusDays(7), "Lunes Siguiente");
        
        createAndSaveTrainingSession(athlete, coach, startDate, "Lunes Inicio");
        createAndSaveTrainingSession(athlete, coach, startDate.plusDays(6), "Domingo Fin");

        List<DailyPlan> weeklyPlan = planService.getWeeklyPlan(athlete.getId(), startDate);

        assertEquals(7, weeklyPlan.size());

        assertEquals(1, weeklyPlan.get(0).getSessions().size());
        assertEquals("Lunes Inicio", weeklyPlan.get(0).getSessions().get(0).getObjective());

        assertEquals(0, weeklyPlan.get(1).getSessions().size());
        
        assertEquals(1, weeklyPlan.get(6).getSessions().size());
        assertEquals("Domingo Fin", weeklyPlan.get(6).getSessions().get(0).getObjective());
    }

    @Test
    public void testRescheduleTrainingSession() throws InstanceNotFoundException, PermissionException {
        Users athlete = createUser("athleteReschedule", RoleType.USER);
        Users coach = createUser("coachReschedule", RoleType.COACH);
        LocalDate originalDate = LocalDate.of(2026, 4, 10);
        LocalDate newDate = LocalDate.of(2026, 4, 12);
        LocalTime newStartTime = LocalTime.of(8, 0);

        TrainingSession session = createAndSaveTrainingSession(athlete, coach, originalDate, "Original Obj");

        TrainingSession rescheduledSession = planService.rescheduleTrainingSession(athlete.getId(), session.getId(), newDate, newStartTime);

        assertEquals(newDate, rescheduledSession.getSessionDate());
        assertEquals(newStartTime, rescheduledSession.getStartTime());
    }

    @Test
    public void testRescheduleTrainingSession_NoPermission() throws InstanceNotFoundException, PermissionException {
        Users athlete = createUser("athleteOwner", RoleType.USER);
        Users fakeAthlete = createUser("athleteHacker", RoleType.USER);
        Users coach = createUser("coachReschedule", RoleType.COACH);
        LocalDate originalDate = LocalDate.of(2026, 4, 10);
        LocalDate newDate = LocalDate.of(2026, 4, 12);
        LocalTime newStartTime = LocalTime.of(8, 0);

        TrainingSession session = createAndSaveTrainingSession(athlete, coach, originalDate, "Original Obj");

        assertThrows(PermissionException.class, () -> {
            planService.rescheduleTrainingSession(fakeAthlete.getId(), session.getId(), newDate, newStartTime);
        });
    }

    @Test
    public void testRescheduleTrainingSession_InstanceNotFound() {
        Users athlete = createUser("athleteReschedule", RoleType.USER);
        LocalDate newDate = LocalDate.of(2026, 4, 12);
        LocalTime newStartTime = LocalTime.of(8, 0);


        assertThrows(InstanceNotFoundException.class, () -> {
            planService.rescheduleTrainingSession(athlete.getId(), -1L, newDate, newStartTime);
        });
    }

    @Test
    public void testGetAthleteDailyPlan_Ok() throws InstanceNotFoundException, PermissionException {
        Users coach = createUser("coach", RoleType.COACH);
        Users athlete = createUser("athlete", RoleType.USER);
        
        athlete.setCoachId(coach.getId());
        userDao.save(athlete);

        LocalDate testDate = LocalDate.of(2026, 3, 25);

        createAndSaveTrainingSession(athlete, coach, testDate, "Test Entrenador");

        DailyPlan dailyPlan = planService.getAthleteDailyPlan(coach.getId(), athlete.getId(), testDate);

        assertEquals(1, dailyPlan.getSessions().size());
        assertEquals("Test Entrenador", dailyPlan.getSessions().get(0).getObjective());
    }

    @Test
    public void testGetAthleteDailyPlan_NoPermission_DifferentCoach() {
        Users coach = createUser("coach", RoleType.COACH);
        Users actualCoach = createUser("coachReal", RoleType.COACH);
        Users athlete = createUser("athlete", RoleType.USER);
        
        athlete.setCoachId(actualCoach.getId());
        userDao.save(athlete);

        LocalDate testDate = LocalDate.of(2026, 3, 25);

        assertThrows(PermissionException.class, () -> {
            planService.getAthleteDailyPlan(coach.getId(), athlete.getId(), testDate);
        });
    }

    @Test
    public void testGetAthleteDailyPlan_NoPermission_NoCoachAssigned() {
        Users coach = createUser("coach", RoleType.COACH);
        Users athlete = createUser("athlete", RoleType.USER);
        
        LocalDate testDate = LocalDate.of(2026, 3, 25);

        assertThrows(PermissionException.class, () -> {
            planService.getAthleteDailyPlan(coach.getId(), athlete.getId(), testDate);
        });
    }

    @Test
    public void testGetAthleteDailyPlan_AthleteNotFound() {
        Users coach = createUser("coach", RoleType.COACH);
        LocalDate testDate = LocalDate.of(2026, 3, 25);

        assertThrows(InstanceNotFoundException.class, () -> {
            planService.getAthleteDailyPlan(coach.getId(), -1L, testDate);
        });
    }
}
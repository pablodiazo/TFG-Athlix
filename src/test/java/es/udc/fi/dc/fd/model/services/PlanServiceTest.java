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

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.*;
import es.udc.fi.dc.fd.model.entities.Users.RoleType;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectRoleException;

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
}
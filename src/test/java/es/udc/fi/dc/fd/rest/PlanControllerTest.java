package es.udc.fi.dc.fd.rest;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.udc.fi.dc.fd.model.entities.TrainingSession;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.Users;
import es.udc.fi.dc.fd.model.entities.Users.RoleType;
import es.udc.fi.dc.fd.model.services.PlanService;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectLoginException;
import es.udc.fi.dc.fd.rest.controllers.UserController;
import es.udc.fi.dc.fd.rest.dtos.AuthenticatedUserDto;
import es.udc.fi.dc.fd.rest.dtos.CreateSessionParamsDto;
import es.udc.fi.dc.fd.rest.dtos.LoginParamsDto;
import es.udc.fi.dc.fd.rest.dtos.TrainingBlockDto;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PlanControllerTest {

    @Autowired
	private MockMvc mockMvc;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

    @Autowired
	private UserDao userDao;

	@Autowired
	private UserController userController;


    @Autowired
    private PlanService planService;

    @Autowired
    private ObjectMapper mapper;

    private AuthenticatedUserDto createAuthenticatedUser(String userName, RoleType roleType)
			throws IncorrectLoginException {

		Users user = new Users(userName, "PASSWORD", "newUser", "user", userName + "@test.com", RoleType.USER, null);

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(roleType);

		userDao.save(user);

		LoginParamsDto loginParams = new LoginParamsDto();
		loginParams.setUserName(user.getUserName());
		loginParams.setPassword("PASSWORD");

		return userController.login(loginParams);

	}

    @Test
    public void testGetDailyPlan_Ok() throws Exception {
        AuthenticatedUserDto athlete = createAuthenticatedUser("athleteGet", RoleType.USER);
        AuthenticatedUserDto coach = createAuthenticatedUser("coachGet", RoleType.COACH);
        
        LocalDate testDate = LocalDate.of(2026, 5, 10);
        LocalTime testTime = LocalTime.of(8, 0);

        planService.createTrainingSession(
                athlete.getUserDto().getId(), 
                coach.getUserDto().getId(), 
                testDate, testTime, 
                TrainingSession.SportType.BIKE, 
                "Rodaje suave", "2h", new ArrayList<>()
        );

        mockMvc.perform(get("/plans/daily")
                .param("date", testDate.toString())
                .header("Authorization", "Bearer " + athlete.getServiceToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date", is(testDate.toString())))
                .andExpect(jsonPath("$.sessions[0].sport", is("BIKE")))
                .andExpect(jsonPath("$.sessions[0].objective", is("Rodaje suave")));
    }

    @Test
    public void testGetDailyPlan_Empty() throws Exception {
        AuthenticatedUserDto athlete = createAuthenticatedUser("athleteEmpty", RoleType.USER);
        LocalDate testDate = LocalDate.of(2026, 5, 11);

        mockMvc.perform(get("/plans/daily")
                .param("date", testDate.toString())
                .header("Authorization", "Bearer " + athlete.getServiceToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date", is(testDate.toString())))
                .andExpect(jsonPath("$.sessions.length()", is(0)));
    }

    @Test
    public void testCreateTrainingSession_Ok() throws Exception {
        AuthenticatedUserDto coach = createAuthenticatedUser("coachCreator", RoleType.COACH);
        AuthenticatedUserDto athlete = createAuthenticatedUser("athleteReceiver", RoleType.USER);

        LocalDate testDate = LocalDate.of(2026, 6, 1);
        LocalTime testTime = LocalTime.of(18, 30);

        TrainingBlockDto blockDto = new TrainingBlockDto(null, 1, "Series Pista", 8, 1, "400m", "Z4", "1 min");
        
        CreateSessionParamsDto params = new CreateSessionParamsDto(
                athlete.getUserDto().getId(),
                testDate,
                testTime,
                TrainingSession.SportType.RUN,
                "Series de Velocidad",
                "8km",
                Arrays.asList(blockDto)
        );

        mockMvc.perform(post("/plans/create-training-session")
                .header("Authorization", "Bearer " + coach.getServiceToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sport", is("RUN")))
                .andExpect(jsonPath("$.objective", is("Series de Velocidad")))
                .andExpect(jsonPath("$.blocks[0].name", is("Series Pista")))
                .andExpect(jsonPath("$.blocks[0].sets", is(8)));
    }

    @Test
    public void testCreateTrainingSession_IncorrectRole() throws Exception {
        AuthenticatedUserDto fakeCoach = createAuthenticatedUser("fakeCoach", RoleType.USER);
        AuthenticatedUserDto athlete = createAuthenticatedUser("athleteTarget", RoleType.USER);

        CreateSessionParamsDto params = new CreateSessionParamsDto(
                athlete.getUserDto().getId(),
                LocalDate.now(),
                LocalTime.now(),
                TrainingSession.SportType.SWIM,
                "Intento Ilegal",
                "1000m",
                new ArrayList<>()
        );

        mockMvc.perform(post("/plans/create-training-session")
                .header("Authorization", "Bearer " + fakeCoach.getServiceToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().is4xxClientError());
    }

    
}

package es.udc.fi.dc.fd.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.is;

import es.udc.fi.dc.fd.model.entities.Users;
import es.udc.fi.dc.fd.model.entities.Users.RoleType;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectLoginException;
import es.udc.fi.dc.fd.rest.controllers.UserController;
import es.udc.fi.dc.fd.rest.dtos.AuthenticatedUserDto;
import es.udc.fi.dc.fd.rest.dtos.ChangePasswordParamsDto;
import es.udc.fi.dc.fd.rest.dtos.LoginParamsDto;
import es.udc.fi.dc.fd.rest.dtos.UserDto;

/**
 * The Class UserControllerTest.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerTest {
	
	/** The Constant PASSWORD. */
	private final static String PASSWORD = "password";

	/** The mock mvc. */
	@Autowired
	private MockMvc mockMvc;

	/** The password encoder. */
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	/** The user dao. */
	@Autowired
	private UserDao userDao;

	/** The user controller. */
	@Autowired
	private UserController userController;

	/**
	 * Creates the authenticated user.
	 *
	 * @param userName the user name
	 * @param roleType the role type
	 * @return the authenticated user dto
	 * @throws IncorrectLoginException the incorrect login exception
	 */
	private AuthenticatedUserDto createAuthenticatedUser(String userName, RoleType roleType)
			throws IncorrectLoginException {

		Users user = new Users(userName, PASSWORD, "newUser", "user", "user@test.com", RoleType.USER);

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(roleType);

		userDao.save(user);

		LoginParamsDto loginParams = new LoginParamsDto();
		loginParams.setUserName(user.getUserName());
		loginParams.setPassword(PASSWORD);

		return userController.login(loginParams);

	}

	@Test
    public void testSignUp_Ok() throws Exception {
        UserDto userDto = new UserDto();

        userDto.setUserName("newUserSignUp");
        userDto.setPassword("password123");
        userDto.setFirstName("New");
        userDto.setLastName("User");
        userDto.setEmail("signup@test.com");
		userDto.setRole("USER");

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(post("/users/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.userName", is("newUserSignUp")));
            }



	/**
	 * Test post login ok.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testPostLogin_Ok() throws Exception {

		AuthenticatedUserDto user = createAuthenticatedUser("admin", RoleType.USER);

		LoginParamsDto loginParams = new LoginParamsDto();
		loginParams.setUserName(user.getUserDto().getUserName());
		loginParams.setPassword(PASSWORD);

		ObjectMapper mapper = new ObjectMapper();

		mockMvc.perform(post("/users/login").header("Authorization", "Bearer " + user.getServiceToken())
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(loginParams)))
				.andExpect(status().isOk());

	}

	@Test
	public void testPostLoginFromServiceToken_Ok() throws Exception {

		AuthenticatedUserDto user = createAuthenticatedUser("tokenUser", RoleType.USER);

		mockMvc.perform(post("/users/loginFromServiceToken")
				.header("Authorization", "Bearer " + user.getServiceToken()))
				.andExpect(status().isOk());
	}

	@Test
    public void testLogin_Exceptions() throws Exception {
        LoginParamsDto params = new LoginParamsDto();
        params.setUserName("nonExistentUser");
        params.setPassword("pass");

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isNotFound());

        AuthenticatedUserDto user = createAuthenticatedUser("wrongPassUser", RoleType.USER);
        params.setUserName(user.getUserDto().getUserName());
        params.setPassword("WrongPassword123");

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isNotFound());
	}

	@Test
	public void testPutUpdateProfile_Ok() throws Exception {

		AuthenticatedUserDto user = createAuthenticatedUser("updateUser", RoleType.USER);
		AuthenticatedUserDto maliciousUser = createAuthenticatedUser("malicious", RoleType.USER);

		UserDto updateDto = user.getUserDto();
        updateDto.setFirstName("UpdatedName");
		updateDto.setLastName("UpdatedLastName");
		updateDto.setEmail("updated@test.com");

		ObjectMapper mapper = new ObjectMapper();

		mockMvc.perform(put("/users/{id}", user.getUserDto().getId())
                .header("Authorization", "Bearer " + user.getServiceToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("UpdatedName")));

		mockMvc.perform(put("/users/{id}", user.getUserDto().getId())
                .header("Authorization", "Bearer " + maliciousUser.getServiceToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().is4xxClientError());

		ChangePasswordParamsDto passParams = new ChangePasswordParamsDto();
        passParams.setOldPassword(PASSWORD);
        passParams.setNewPassword("NewPass123");

        mockMvc.perform(post("/users/{id}/changePassword", user.getUserDto().getId())
                .header("Authorization", "Bearer " + maliciousUser.getServiceToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(passParams)))
                .andExpect(status().is4xxClientError());
	}

	@Test
	public void testPostChangePassword_Ok() throws Exception {

		AuthenticatedUserDto user = createAuthenticatedUser("passUser", RoleType.USER);
		Long userId = user.getUserDto().getId();

		ChangePasswordParamsDto changePassParams = new ChangePasswordParamsDto();
		changePassParams.setOldPassword(PASSWORD);
		changePassParams.setNewPassword("newSafePassword");

		ObjectMapper mapper = new ObjectMapper();

		mockMvc.perform(post("/users/" + userId + "/changePassword")
				.header("Authorization", "Bearer " + user.getServiceToken())
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(changePassParams)))
				.andExpect(status().isNoContent());
	}

	@Test
	public void testPostChangePassword_IncorrectOldPassword() throws Exception {

		AuthenticatedUserDto user = createAuthenticatedUser("passUserError", RoleType.USER);
		Long userId = user.getUserDto().getId();

		ChangePasswordParamsDto changePassParams = new ChangePasswordParamsDto();
		changePassParams.setOldPassword("wrongOldPassword");
		changePassParams.setNewPassword("newSafePassword");

		ObjectMapper mapper = new ObjectMapper();

		mockMvc.perform(post("/users/" + userId + "/changePassword")
				.header("Authorization", "Bearer " + user.getServiceToken())
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(changePassParams)))
				.andExpect(status().is4xxClientError());
	}
}

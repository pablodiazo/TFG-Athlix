package es.udc.fi.dc.fd.model.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

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
import es.udc.fi.dc.fd.model.entities.Users;
import es.udc.fi.dc.fd.model.entities.Users.RoleType;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectLoginException;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectPasswordException;

/**
 * The Class UserServiceTest.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

	/** The user service. */
	@Autowired
	private UserService userService;

	private final Long NON_EXISTENT_ID = -1L;

	/**
	 * Creates the user.
	 *
	 * @param userName the user name
	 * @return the user
	 */
	private Users createUser(String userName, RoleType roleType) throws DuplicateInstanceException {
		Users user = new Users(userName, "password", "firstName", "lastName", userName + "@" + userName + ".com", roleType, null);
		return user;
	}

	@Test
    public void testSignUpUser() throws DuplicateInstanceException, InstanceNotFoundException {
        Users user = createUser("user", RoleType.USER);
        userService.signUp(user);

        Users loggedInUser = userService.loginFromId(user.getId());
        assertEquals(user, loggedInUser);
        assertEquals(RoleType.USER, user.getRole());
    }

	@Test
    public void testSignUpCoach() throws DuplicateInstanceException, InstanceNotFoundException {
        Users user = createUser("coach", RoleType.COACH);
        userService.signUp(user);

        Users loggedInUser = userService.loginFromId(user.getId());
        assertEquals(user, loggedInUser);
        assertEquals(RoleType.COACH, user.getRole());
    }

	@Test
    public void testSignUpDuplicatedUserName() throws DuplicateInstanceException {
        Users user1 = createUser("user", RoleType.USER);
        userService.signUp(user1);

        Users user2 = createUser("user", RoleType.USER);
		assertThrows(DuplicateInstanceException.class, () -> userService.signUp(user2));
    }

	@Test
    public void testLogin() throws DuplicateInstanceException, IncorrectLoginException {
        Users user = createUser("user", RoleType.USER);
        userService.signUp(user);

        Users loggedInUser = userService.login("user", "password");
        assertEquals(user, loggedInUser);
    }

    @Test
    public void testLoginWithIncorrectPassword() throws DuplicateInstanceException, IncorrectLoginException {
        Users user = createUser("user", RoleType.USER);
        userService.signUp(user);

        assertThrows(IncorrectLoginException.class, () -> userService.login("user", "incorrectPassword"));
    }

    @Test
    public void testLoginWithNonExistentUserName() throws IncorrectLoginException {
        assertThrows(IncorrectLoginException.class, () -> userService.login("nonExistentUser", "password"));
    }

	@Test
    public void testLoginFromId() throws DuplicateInstanceException, InstanceNotFoundException {
        Users user = createUser("user", RoleType.USER);
        userService.signUp(user);

        Users loggedInUser = userService.loginFromId(user.getId());
        assertEquals(user, loggedInUser);
    }

    @Test
    public void testLoginFromIdWithNonExistentId() throws InstanceNotFoundException {
        assertThrows(InstanceNotFoundException.class, () -> userService.loginFromId(NON_EXISTENT_ID));
    }

	@Test
    public void testUpdateProfile() throws DuplicateInstanceException, InstanceNotFoundException {
        Users user = createUser("user", RoleType.USER);
        userService.signUp(user);

        String newFirstName = "newName";
        String newLastName = "newLastName";
        String newEmail = "newEmail@test.com";
        String newUserName = "newUserName";
        String newRole = "USER";

        userService.updateProfile(user.getId(), newFirstName, newLastName, newEmail, newRole, newUserName);

        Users updatedUser = userService.loginFromId(user.getId());

        assertEquals(newFirstName, updatedUser.getFirstName());
        assertEquals(newLastName, updatedUser.getLastName());
        assertEquals(newEmail, updatedUser.getEmail());
    }

    @Test
    public void testUpdateProfileWithNonExistentId() throws InstanceNotFoundException {
        assertThrows(InstanceNotFoundException.class, () -> userService.updateProfile(NON_EXISTENT_ID, "name", "lastName", "email@test.com", "userName", "USER"));
    }

	@Test
    public void testChangePassword() throws DuplicateInstanceException, InstanceNotFoundException,
            IncorrectPasswordException, IncorrectLoginException {
        
        Users user = createUser("user", RoleType.USER);
        userService.signUp(user);

        userService.changePassword(user.getId(), "password", "newPassword");

        Users loggedInUser = userService.login("user", "newPassword");
        assertEquals(user, loggedInUser);
    }

    @Test
    public void testChangePasswordWithIncorrectPassword() throws DuplicateInstanceException, 
            InstanceNotFoundException, IncorrectPasswordException {
        
        Users user = createUser("user", RoleType.USER);
        userService.signUp(user);

        assertThrows(IncorrectPasswordException.class, () -> userService.changePassword(user.getId(), "incorrectPassword", "newPassword"));
    }

    @Test
    public void testChangePasswordWithNonExistentId() throws InstanceNotFoundException, IncorrectPasswordException {
        assertThrows(InstanceNotFoundException.class, () -> userService.changePassword(NON_EXISTENT_ID, "password", "newPassword"));
    }

    @Test
    public void testGetAthletesByCoach() throws InstanceNotFoundException, DuplicateInstanceException {
        Users coach = createUser("coach", RoleType.COACH);

        userService.signUp(coach);

        List<Users> athletes = userService.getAthletesByCoach(coach.getId());

        assertEquals(0, athletes.size());
    }

}

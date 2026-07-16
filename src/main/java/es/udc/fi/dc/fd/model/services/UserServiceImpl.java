package es.udc.fi.dc.fd.model.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.Users;
import es.udc.fi.dc.fd.model.entities.CoachRequest;
import es.udc.fi.dc.fd.model.entities.CoachRequest.CoachRequestStatus;
import es.udc.fi.dc.fd.model.entities.CoachRequestDao;
import es.udc.fi.dc.fd.model.entities.NotificationDao;
import es.udc.fi.dc.fd.model.entities.Notification;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectLoginException;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectPasswordException;
import es.udc.fi.dc.fd.model.services.exceptions.PermissionException;

/**
 * The Class UserServiceImpl.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

	/** The permission checker. */
	@Autowired
	private PermissionChecker permissionChecker;

	/** The password encoder. */
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	/** The user dao. */
	@Autowired
	private UserDao userDao;

	@Autowired
    private CoachRequestDao coachRequestDao;

	@Autowired
    private NotificationDao notificationDao;

	/**
	 * Sign up.
	 *
	 * @param user the user
	 * @throws DuplicateInstanceException the duplicate instance exception
	 */
	@Override
	public void signUp(Users user) throws DuplicateInstanceException {

		if (userDao.existsByUserName(user.getUserName())) {
			throw new DuplicateInstanceException("project.entities.user", user.getUserName());
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if (user.getRole() == Users.RoleType.COACH) {
			user.setRole(Users.RoleType.COACH);
		} else {
			user.setRole(Users.RoleType.USER); 
    	}

		userDao.save(user);

	}

	/**
	 * Login.
	 *
	 * @param userName the user name
	 * @param password the password
	 * @return the user
	 * @throws IncorrectLoginException the incorrect login exception
	 */
	@Override
	@Transactional(readOnly = true)
	public Users login(String userName, String password) throws IncorrectLoginException {

		Optional<Users> user = userDao.findByUserName(userName);

		if (!user.isPresent()) {
			throw new IncorrectLoginException(userName, password);
		}

		if (!passwordEncoder.matches(password, user.get().getPassword())) {
			throw new IncorrectLoginException(userName, password);
		}

		return user.get();

	}

	/**
	 * Login from id.
	 *
	 * @param id the id
	 * @return the user
	 * @throws InstanceNotFoundException the instance not found exception
	 */
	@Override
	@Transactional(readOnly = true)
	public Users loginFromId(Long id) throws InstanceNotFoundException {
		return permissionChecker.checkUser(id);
	}

	/**
	 * Update profile.
	 *
	 * @param id        the id
	 * @param firstName the first name
	 * @param lastName  the last name
	 * @param email     the email
	 * @return the user
	 * @throws InstanceNotFoundException the instance not found exception
	 */
	@Override
	public Users updateProfile(Long id, String firstName, String lastName, String email, String role, String userName)
			throws InstanceNotFoundException {

		Users user = permissionChecker.checkUser(id);

		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		user.setRole(Users.RoleType.valueOf(role));
		user.setUserName(userName);

		return user;

	}

	/**
	 * Change password.
	 *
	 * @param id          the id
	 * @param oldPassword the old password
	 * @param newPassword the new password
	 * @throws InstanceNotFoundException  the instance not found exception
	 * @throws IncorrectPasswordException the incorrect password exception
	 */
	@Override
	public void changePassword(Long id, String oldPassword, String newPassword)
			throws InstanceNotFoundException, IncorrectPasswordException {

		Users user = permissionChecker.checkUser(id);

		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			throw new IncorrectPasswordException();
		} else {
			user.setPassword(passwordEncoder.encode(newPassword));
		}

	}

	@Override
    public List<Users> getAthletesByCoach(Long coachId) {
        return userDao.findAthletesByCoach(Users.RoleType.USER, coachId);
    }

    @Override
    public CoachRequest sendCoachRequest(Long coachId, String athleteEmail) throws InstanceNotFoundException, DuplicateInstanceException {
        Users coach = userDao.findById(coachId)
            .orElseThrow(() -> new InstanceNotFoundException("user", coachId));
        
        Users athlete = userDao.findByEmail(athleteEmail)
            .orElseThrow(() -> new InstanceNotFoundException("user", athleteEmail));

        if (coach.getId().equals(athlete.getId())) {
            throw new IllegalArgumentException("No puedes invitarte a ti mismo");
        }

		if (athlete.getCoachId() != null && athlete.getCoachId().equals(coach.getId())) {
            throw new DuplicateInstanceException("request", athleteEmail);
        }

        if (coachRequestDao.existsByCoachIdAndAthleteIdAndStatus(coach.getId(), athlete.getId(), CoachRequestStatus.PENDING)) {
            throw new DuplicateInstanceException("request", athleteEmail);
        }

        CoachRequest request = new CoachRequest(coach, athlete, CoachRequestStatus.PENDING);
		coachRequestDao.save(request);

		Notification notification = new Notification();
        notification.setAthlete(coach);
		notification.setUser(athlete);
        notification.setMessage("El entrenador " + athlete.getFirstName() + " " + athlete.getLastName() + " quiere planificar tus entrenamientos.");
        notification.setType("COACH_REQUEST");
        notification.setRead(false);
        notification.setReviewed(false);
        
        notificationDao.save(notification);
        return request;
    }

    @Override
    public List<CoachRequest> getPendingRequests(Long athleteId) throws InstanceNotFoundException {
        if (!userDao.existsById(athleteId)) {
            throw new InstanceNotFoundException("user", athleteId);
        }
        return coachRequestDao.findByAthleteIdAndStatus(athleteId, CoachRequestStatus.PENDING);
    }

    @Override
    public void acceptCoachRequest(Long athleteId, Long requestId) throws InstanceNotFoundException, PermissionException {
        CoachRequest request = coachRequestDao.findById(requestId)
            .orElseThrow(() -> new InstanceNotFoundException("request", requestId));

        if (!request.getAthlete().getId().equals(athleteId)) {
            throw new PermissionException();
        }

        request.setStatus(CoachRequestStatus.ACCEPTED);
        
        Users athlete = request.getAthlete();
        athlete.setCoachId(request.getCoach().getId());
        
        userDao.save(athlete);
        coachRequestDao.save(request);

		Notification notification = new Notification();
        notification.setAthlete(athlete); 
		notification.setUser(request.getCoach());
        notification.setMessage("¡" + request.getCoach().getFirstName() + " " + request.getCoach().getLastName() + " ha aceptado tu solicitud de entrenamiento!");
        notification.setType("COACH_ACCEPTED");
        notification.setRead(false);
        notification.setReviewed(false);
        
        notificationDao.save(notification);
        
    }

    @Override
    public void rejectCoachRequest(Long athleteId, Long requestId) throws InstanceNotFoundException, PermissionException {
        CoachRequest request = coachRequestDao.findById(requestId)
            .orElseThrow(() -> new InstanceNotFoundException("request", requestId));

		Users athlete = userDao.findById(athleteId)
            .orElseThrow(() -> new InstanceNotFoundException("user", athleteId));

        if (!request.getAthlete().getId().equals(athleteId)) {
            throw new PermissionException();
        }

        request.setStatus(CoachRequestStatus.REJECTED);
        coachRequestDao.save(request);

		Notification notification = new Notification();
        notification.setAthlete(athlete);
        notification.setUser(request.getCoach());
        notification.setMessage(request.getCoach().getFirstName() + " " + request.getCoach().getLastName() + " ha rechazado tu solicitud de entrenamiento.");
        notification.setType("COACH_REJECTED");
        notification.setRead(false);
        notification.setReviewed(false);
        
        notificationDao.save(notification);
    }

	@Override
    public List<CoachRequest> getSentRequests(Long coachId) throws InstanceNotFoundException {
        if (!userDao.existsById(coachId)) {
            throw new InstanceNotFoundException("user", coachId);
        }
        return coachRequestDao.findByCoachIdAndStatus(coachId, CoachRequestStatus.PENDING);
    }

}

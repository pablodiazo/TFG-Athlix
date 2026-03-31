package es.udc.fi.dc.fd.model.entities;

import java.util.List;

public interface CustomizedUserDao {

    List<Users> findAthletesByCoach(Users.RoleType role, Long coachId);
    
}

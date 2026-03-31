package es.udc.fi.dc.fd.model.entities;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.List;

public class CustomizedUserDaoImpl implements CustomizedUserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<Users> findAthletesByCoach(Users.RoleType role, Long coachId) {
        String queryString = "SELECT u FROM Users u WHERE u.role = :role AND u.coachId = :coachId";

        Query query = entityManager.createQuery(queryString);
        query.setParameter("role", role);
        query.setParameter("coachId", coachId);

        List<Users> athletes = query.getResultList();

        return athletes;
    }
    
}

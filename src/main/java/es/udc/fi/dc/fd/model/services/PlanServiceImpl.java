package es.udc.fi.dc.fd.model.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.*;

@Service
@Transactional
public class PlanServiceImpl implements PlanService {

    @Autowired
    private TrainingSessionDao trainingSessionDao;

    @Autowired
    private NutritionPlanDao nutritionPlanDao;

    @Autowired
    private RestPlanDao restPlanDao;

    @Override
    public DailyPlan getDailyPlan(Long userId, LocalDate date) throws InstanceNotFoundException {
        List<TrainingSession> sessions = trainingSessionDao.findByUserIdAndSessionDateOrderByStartTimeAsc(userId, date);
        Optional<NutritionPlan> nutrition = nutritionPlanDao.findByUserIdAndPlanDate(userId, date);
        Optional<RestPlan> rest = restPlanDao.findByUserIdAndPlanDate(userId, date);

        return new DailyPlan(sessions, nutrition, rest);
    }
    
}

package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.entities.DailyPlan;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;

import java.time.LocalDate;

public interface PlanService {
    DailyPlan getDailyPlan(Long userId, LocalDate date) throws InstanceNotFoundException;
}

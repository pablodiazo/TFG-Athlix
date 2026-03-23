package es.udc.fi.dc.fd.rest.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.udc.fi.dc.fd.model.services.PlanService;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.DailyPlan;
import es.udc.fi.dc.fd.rest.dtos.DailyPlanDto;
import es.udc.fi.dc.fd.rest.dtos.NutritionPlanDto;
import es.udc.fi.dc.fd.rest.dtos.RestPlanDto;
import es.udc.fi.dc.fd.rest.dtos.TrainingSessionDto;
import es.udc.fi.dc.fd.rest.dtos.TrainingBlockDto;

@RestController
@RequestMapping("/plans")
public class PlanController {

    @Autowired
    private PlanService planService;

    // GET /athlix/plans/daily?date=2026-03-17
    @GetMapping("/daily")
    public DailyPlanDto getDailyPlan(
            @RequestAttribute Long userId, // El ID del atleta logueado (inyectado por el token JWT)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) 
            throws InstanceNotFoundException {
        
        DailyPlan info = planService.getDailyPlan(userId, date);

        List<TrainingSessionDto> sessionDtos = info.getSessions().stream().map(s -> {
            List<TrainingBlockDto> blockDtos = s.getBlocks().stream().map(b -> 
                new TrainingBlockDto(b.getId(), b.getBlockOrder(), b.getName(), b.getSets(), 
                                     b.getReps(), b.getDistanceOrDuration(), b.getPace(), b.getRest())
            ).collect(Collectors.toList());

            return new TrainingSessionDto(s.getId(), s.getSessionDate(), s.getStartTime(), s.getSport().toString(), 
                                          s.getObjective(), s.getTotalDistanceOrDuration(), blockDtos);
        }).collect(Collectors.toList());

        NutritionPlanDto nutritionDto = info.getNutrition().map(n -> 
            new NutritionPlanDto(n.getId(), n.getPlanDate(), n.getTargetCalories(), n.getProteinGrams(), 
                                 n.getCarbsGrams(), n.getFatGrams(), n.getHydrationLiters(), n.getGuidelines())
        ).orElse(null);

        RestPlanDto restDto = info.getRest().map(r -> 
            new RestPlanDto(r.getId(), r.getPlanDate(), r.getTargetSleepHours(), r.getGuidelines())
        ).orElse(null);

        return new DailyPlanDto(date.toString(), sessionDtos, nutritionDto, restDto);
    }
    
}

package es.udc.fi.dc.fd.rest.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.udc.fi.dc.fd.model.services.PlanService;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectRoleException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.DailyPlan;
import es.udc.fi.dc.fd.model.entities.NutritionPlan;
import es.udc.fi.dc.fd.model.entities.TrainingBlock;
import es.udc.fi.dc.fd.model.entities.TrainingSession;
import es.udc.fi.dc.fd.rest.dtos.CreateSessionParamsDto;
import es.udc.fi.dc.fd.rest.dtos.CreateNutritionPlanParamsDto;
import es.udc.fi.dc.fd.rest.dtos.DailyPlanDto;
import es.udc.fi.dc.fd.rest.dtos.NutritionPlanDto;
import es.udc.fi.dc.fd.rest.dtos.RestPlanDto;
import es.udc.fi.dc.fd.rest.dtos.TrainingSessionDto;
import es.udc.fi.dc.fd.rest.dtos.TrainingBlockDto;
import static es.udc.fi.dc.fd.rest.dtos.TrainingSessionConversor.toTrainingSessionDto;
import static es.udc.fi.dc.fd.rest.dtos.PlanConversor.toNutritionPlanDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/plans")
public class PlanController {

    @Autowired
    private PlanService planService;

    // GET /athlix/plans/daily?date=2026-03-17
    @GetMapping("/daily")
    public DailyPlanDto getDailyPlan(
            @RequestAttribute Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) 
            throws InstanceNotFoundException {
        
        DailyPlan info = planService.getDailyPlan(userId, date);

        List<TrainingSessionDto> sessionDtos = info.getSessions().stream().map(s -> {
            List<TrainingBlockDto> blockDtos = s.getBlocks().stream().map(b -> 
                new TrainingBlockDto(b.getId(), b.getBlockOrder(), b.getName(), b.getSets(), 
                                     b.getReps(), b.getDistanceOrDuration(), b.getPace(), b.getRest())
            ).collect(Collectors.toList());

            return new TrainingSessionDto(s.getId(), s.getSessionDate(), s.getStartTime(), s.getSport(), 
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

    @PostMapping("/create-training-session")
    public TrainingSessionDto createTrainingSession(@RequestAttribute Long userId,
        @Validated @RequestBody CreateSessionParamsDto params) throws InstanceNotFoundException, IncorrectRoleException{
        
        List<TrainingBlock> blocks = new ArrayList<>();

        if(params.getBlocks() != null && !params.getBlocks().isEmpty()) {
            for (TrainingBlockDto block : params.getBlocks()) {
                TrainingBlock trainingBlock = new TrainingBlock();
                trainingBlock.setBlockOrder(block.getBlockOrder());
                trainingBlock.setName(block.getName());
                trainingBlock.setSets(block.getSets());
                trainingBlock.setReps(block.getReps());
                trainingBlock.setDistanceOrDuration(block.getDistanceOrDuration());
                trainingBlock.setPace(block.getPace());
                trainingBlock.setRest(block.getRest());
                blocks.add(trainingBlock);
            }
        }
        
        TrainingSession trainingSession = planService.createTrainingSession(params.getAthleteId(), userId, params.getSessionDate(), 
                                                 params.getStartTime(), params.getSport(), params.getObjective(), 
                                                 params.getTotalDistanceOrDuration(), blocks);

        return toTrainingSessionDto(trainingSession);

    }

    @PostMapping("/create-nutrition-plan")
    public NutritionPlanDto createNutritionPlan(@RequestAttribute Long userId,
        @Validated @RequestBody CreateNutritionPlanParamsDto params) throws InstanceNotFoundException, IncorrectRoleException{
        
        NutritionPlan nutritionPlan = planService.createNutritionPlan(params.getAthleteId(), userId, params.getPlanDate(), 
                                                 params.getTargetCalories(), params.getProteinGrams(), params.getCarbsGrams(), 
                                                 params.getFatGrams(), params.getHydrationLiters(), params.getGuidelines());

        return toNutritionPlanDto(nutritionPlan);

    }
    
}

package es.udc.fi.dc.fd.rest.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.udc.fi.dc.fd.model.services.PlanService;
import es.udc.fi.dc.fd.model.services.exceptions.*;
import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.DailyPlan;
import es.udc.fi.dc.fd.model.entities.NutritionPlan;
import es.udc.fi.dc.fd.model.entities.TrainingBlock;
import es.udc.fi.dc.fd.model.entities.TrainingSession;
import es.udc.fi.dc.fd.model.entities.RestPlan;
import es.udc.fi.dc.fd.rest.dtos.CreateSessionParamsDto;
import es.udc.fi.dc.fd.rest.dtos.CreateNutritionPlanParamsDto;
import es.udc.fi.dc.fd.rest.dtos.CreateRestPlanParamsDto;
import es.udc.fi.dc.fd.rest.dtos.DailyPlanDto;
import es.udc.fi.dc.fd.rest.dtos.NotificationDto;
import es.udc.fi.dc.fd.rest.dtos.NutritionPlanDto;
import es.udc.fi.dc.fd.rest.dtos.RescheduleParamsDto;
import es.udc.fi.dc.fd.rest.dtos.RestPlanDto;
import es.udc.fi.dc.fd.rest.dtos.TrainingSessionDto;
import es.udc.fi.dc.fd.rest.dtos.TrainingBlockDto;
import es.udc.fi.dc.fd.rest.dtos.UpdatePlanDoneParamsDto;
import es.udc.fi.dc.fd.rest.dtos.UpdateSessionParamsDto;
import es.udc.fi.dc.fd.rest.dtos.AcceptReadjustmentParamsDto;
import static es.udc.fi.dc.fd.rest.dtos.TrainingSessionConversor.toTrainingSessionDto;
import static es.udc.fi.dc.fd.rest.dtos.TrainingBlockConversor.toTrainingBlockDto;
import static es.udc.fi.dc.fd.rest.dtos.PlanConversor.toNutritionPlanDto;
import static es.udc.fi.dc.fd.rest.dtos.PlanConversor.toRestPlanDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
            throws InstanceNotFoundException, PermissionException {
        
        DailyPlan info = planService.getDailyPlan(userId, date);

        List<TrainingSessionDto> sessionDtos = info.getSessions().stream().map(s -> {
            List<TrainingBlockDto> blockDtos = s.getBlocks().stream().map(b -> 
                new TrainingBlockDto(b.getId(), b.getBlockOrder(), b.getName(), b.getSets(), 
                                     b.getReps(), b.getDistanceOrDuration(), b.getPace(), b.getRest(), b.getDone())
            ).collect(Collectors.toList());

            Double tss=0.0;
                try {
                    tss = planService.calculateTSS(s.getId());
                } catch (InstanceNotFoundException e) {
                    e.printStackTrace();
                } catch (PermissionException e) {
                    e.printStackTrace();
                }

            return new TrainingSessionDto(s.getId(), s.getSessionDate(), s.getStartTime(), s.getSport(), 
                                          s.getObjective(), s.getTotalDistanceOrDuration(), tss, blockDtos);
        }).collect(Collectors.toList());

        NutritionPlanDto nutritionDto = info.getNutrition().map(n -> 
            new NutritionPlanDto(n.getId(), n.getPlanDate(), n.getTargetCalories(), n.getProteinGrams(), 
                                 n.getCarbsGrams(), n.getFatGrams(), n.getHydrationLiters(), n.getGuidelines(), n.getDone())
        ).orElse(null);

        RestPlanDto restDto = info.getRest().map(r -> 
            new RestPlanDto(r.getId(), r.getPlanDate(), r.getTargetSleepHours(), r.getGuidelines(), r.getDone())
        ).orElse(null);

        return new DailyPlanDto(date.toString(), sessionDtos, nutritionDto, restDto);
    }

    // GET /plans/weekly?startDate=2026-03-16
    @GetMapping("/weekly")
    public List<DailyPlanDto> getWeeklyPlan(
            @RequestAttribute Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate)
            throws InstanceNotFoundException, PermissionException {
        
        List<DailyPlan> weeklyInfo = planService.getWeeklyPlan(userId, startDate);
        List<DailyPlanDto> weeklyDtos = new ArrayList<>();

        LocalDate currentDate = startDate;

        for (DailyPlan info : weeklyInfo) {
            
            List<TrainingSessionDto> sessionDtos = info.getSessions().stream().map(s -> {
                List<TrainingBlockDto> blockDtos = s.getBlocks().stream().map(b -> 
                    new TrainingBlockDto(b.getId(), b.getBlockOrder(), b.getName(), b.getSets(), 
                                         b.getReps(), b.getDistanceOrDuration(), b.getPace(), b.getRest(), b.getDone())
                ).collect(Collectors.toList());

                Double tss=0.0;
                try {
                    tss = planService.calculateTSS(s.getId());
                } catch (InstanceNotFoundException e) {
                    e.printStackTrace();
                } catch (PermissionException e) {
                    e.printStackTrace();
                }

                return new TrainingSessionDto(s.getId(), s.getSessionDate(), s.getStartTime(), s.getSport(), 
                                              s.getObjective(), s.getTotalDistanceOrDuration(), tss, blockDtos);
            }).collect(Collectors.toList());

            NutritionPlanDto nutritionDto = info.getNutrition().map(n -> 
                new NutritionPlanDto(n.getId(), n.getPlanDate(), n.getTargetCalories(), n.getProteinGrams(), 
                                     n.getCarbsGrams(), n.getFatGrams(), n.getHydrationLiters(), n.getGuidelines(), n.getDone())
            ).orElse(null);

            RestPlanDto restDto = info.getRest().map(r -> 
                new RestPlanDto(r.getId(), r.getPlanDate(), r.getTargetSleepHours(), r.getGuidelines(), r.getDone())
            ).orElse(null);

            weeklyDtos.add(new DailyPlanDto(currentDate.toString(), sessionDtos, nutritionDto, restDto));
            
            currentDate = currentDate.plusDays(1);
        }

        return weeklyDtos;
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
        @Validated @RequestBody CreateNutritionPlanParamsDto params) throws InstanceNotFoundException, IncorrectRoleException, DuplicateInstanceException{
        
        NutritionPlan nutritionPlan = planService.createNutritionPlan(params.getAthleteId(), userId, params.getPlanDate(), 
                                                 params.getTargetCalories(), params.getProteinGrams(), params.getCarbsGrams(), 
                                                 params.getFatGrams(), params.getHydrationLiters(), params.getGuidelines());

        return toNutritionPlanDto(nutritionPlan);

    }

    @PostMapping("/create-rest-plan")
    public RestPlanDto createRestPlan(@RequestAttribute Long userId,
        @Validated @RequestBody CreateRestPlanParamsDto params) throws InstanceNotFoundException, IncorrectRoleException, DuplicateInstanceException{

        RestPlan restPlan = planService.createRestPlan(params.getAthleteId(), userId, params.getPlanDate(), 
                                                params.getTargetSleepHours(), params.getGuidelines());

        return toRestPlanDto(restPlan);
    }

    @PostMapping("/update-training-block-done")
    public TrainingBlockDto updateTrainingBlockDone(@RequestAttribute Long userId,
        @Validated @RequestBody UpdatePlanDoneParamsDto params) throws InstanceNotFoundException, PermissionException{

        TrainingBlock trainingBlock = planService.updateTrainingBlockDone(userId, params.getPlanId(), params.getDone());

        return toTrainingBlockDto(trainingBlock);
    }

    @PostMapping("/update-nutrition-plan-done")
    public NutritionPlanDto updateNutritionPlanDone(@RequestAttribute Long userId,
        @Validated @RequestBody UpdatePlanDoneParamsDto params) throws InstanceNotFoundException, PermissionException{

        NutritionPlan nutritionPlan = planService.updateNutritionPlanDone(userId, params.getPlanId(), params.getDone());

        return toNutritionPlanDto(nutritionPlan);
    }

    @PostMapping("/update-rest-plan-done")
    public RestPlanDto updateRestPlanDone(@RequestAttribute Long userId,
        @Validated @RequestBody UpdatePlanDoneParamsDto params) throws InstanceNotFoundException, PermissionException{

        RestPlan restPlan = planService.updateRestPlanDone(userId, params.getPlanId(), params.getDone());

        return toRestPlanDto(restPlan);
    }
    
    @PostMapping("/reschedule-training-session")
    public void rescheduleTrainingSession(@RequestAttribute Long userId,
        @Validated @RequestBody RescheduleParamsDto params) throws InstanceNotFoundException, PermissionException{

        planService.rescheduleTrainingSession(userId, params.getSessionId(), params.getNewDate(), params.getNewStartTime());
    }

    // GET /plans/athletes/{athleteId}/daily?date=2026-03-17
    @GetMapping("/athletes/{athleteId}/daily")
    public DailyPlanDto getAthleteDailyPlan(
            @RequestAttribute Long userId,
            @PathVariable Long athleteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) 
            throws InstanceNotFoundException, PermissionException {
        
        DailyPlan info = planService.getAthleteDailyPlan(userId, athleteId, date);

        List<TrainingSessionDto> sessionDtos = info.getSessions().stream().map(s -> {
            List<TrainingBlockDto> blockDtos = s.getBlocks().stream().map(b -> 
                new TrainingBlockDto(b.getId(), b.getBlockOrder(), b.getName(), b.getSets(), 
                                     b.getReps(), b.getDistanceOrDuration(), b.getPace(), b.getRest(), b.getDone())
            ).collect(Collectors.toList());

            Double tss=0.0;
                try {
                    tss = planService.calculateTSS(s.getId());
                } catch (InstanceNotFoundException e) {
                    e.printStackTrace();
                } catch (PermissionException e) {
                    e.printStackTrace();
                }

            return new TrainingSessionDto(s.getId(), s.getSessionDate(), s.getStartTime(), s.getSport(), 
                                          s.getObjective(), s.getTotalDistanceOrDuration(), tss, blockDtos);
        }).collect(Collectors.toList());

        NutritionPlanDto nutritionDto = info.getNutrition().map(n -> 
            new NutritionPlanDto(n.getId(), n.getPlanDate(), n.getTargetCalories(), n.getProteinGrams(), 
                                 n.getCarbsGrams(), n.getFatGrams(), n.getHydrationLiters(), n.getGuidelines(), n.getDone())
        ).orElse(null);

        RestPlanDto restDto = info.getRest().map(r -> 
            new RestPlanDto(r.getId(), r.getPlanDate(), r.getTargetSleepHours(), r.getGuidelines(), r.getDone())
        ).orElse(null);

        return new DailyPlanDto(date.toString(), sessionDtos, nutritionDto, restDto);
    }
    
    @GetMapping("/athletes/{athleteId}/weekly")
    public List<DailyPlanDto> getAthleteWeeklyPlan(
            @RequestAttribute Long userId,
            @PathVariable Long athleteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate)
            throws InstanceNotFoundException, PermissionException {
        
        List<DailyPlan> weeklyInfo = planService.getAthleteWeeklyPlan(userId, athleteId, startDate);
        List<DailyPlanDto> weeklyDtos = new ArrayList<>();

        LocalDate currentDate = startDate;

        for (DailyPlan info : weeklyInfo) {
            
            List<TrainingSessionDto> sessionDtos = info.getSessions().stream().map(s -> {
                List<TrainingBlockDto> blockDtos = s.getBlocks().stream().map(b -> 
                    new TrainingBlockDto(b.getId(), b.getBlockOrder(), b.getName(), b.getSets(), 
                                         b.getReps(), b.getDistanceOrDuration(), b.getPace(), b.getRest(), b.getDone())
                ).collect(Collectors.toList());

                Double tss=0.0;
                try {
                    tss = planService.calculateTSS(s.getId());
                } catch (InstanceNotFoundException e) {
                    e.printStackTrace();
                } catch (PermissionException e) {
                    e.printStackTrace();
                }

                return new TrainingSessionDto(s.getId(), s.getSessionDate(), s.getStartTime(), s.getSport(), 
                                              s.getObjective(), s.getTotalDistanceOrDuration(), tss, blockDtos);
            }).collect(Collectors.toList());

            NutritionPlanDto nutritionDto = info.getNutrition().map(n -> 
                new NutritionPlanDto(n.getId(), n.getPlanDate(), n.getTargetCalories(), n.getProteinGrams(), 
                                     n.getCarbsGrams(), n.getFatGrams(), n.getHydrationLiters(), n.getGuidelines(), n.getDone())
            ).orElse(null);

            RestPlanDto restDto = info.getRest().map(r -> 
                new RestPlanDto(r.getId(), r.getPlanDate(), r.getTargetSleepHours(), r.getGuidelines(), r.getDone())
            ).orElse(null);

            weeklyDtos.add(new DailyPlanDto(currentDate.toString(), sessionDtos, nutritionDto, restDto));
            
            currentDate = currentDate.plusDays(1);
        }

        return weeklyDtos;
    }

    @GetMapping("/notifications")
    public List<NotificationDto> getNotifications(@RequestAttribute Long userId) {
        return planService.getNotifications(userId).stream()
            .map(n -> new NotificationDto(
                n.getId(), 
                n.getAthlete().getId(), 
                n.getSessionId(),
                n.getMessage(), 
                n.getType(), 
                n.getPlanDate().toString(), 
                n.isRead(), 
                n.isReviewed(),
                n.getNewDate() != null ? n.getNewDate().toString() : null, 
                n.getNewStartTime() != null ? n.getNewStartTime().toString() : null
            ))    
            .collect(Collectors.toList());
    }

    @PostMapping("/notifications/{id}/read")
    public void markNotificationAsRead(@RequestAttribute Long userId, @PathVariable Long id) 
            throws InstanceNotFoundException, PermissionException {
        planService.markNotificationAsRead(userId, id);
    }

    @PostMapping("/notifications/{id}/accept")
    public TrainingSessionDto acceptReadjustment(@RequestAttribute Long userId, @PathVariable Long id,
        @Validated @RequestBody AcceptReadjustmentParamsDto params)
            throws InstanceNotFoundException, PermissionException {
        TrainingSession trainingSession = planService.acceptReadjustment(userId, params.getAthleteId(), id, params.getSessionId(), params.getNewDate(), params.getNewStartTime(), params.getReschedule());
    
        return toTrainingSessionDto(trainingSession);
    }

    @PostMapping("/notifications/{id}/deny")
    public void denyReadjustment(@RequestAttribute Long userId, @PathVariable Long id,
        @Validated @RequestBody AcceptReadjustmentParamsDto params)
            throws InstanceNotFoundException, PermissionException {
        planService.denyReadjustment(userId, params.getAthleteId(), id, params.getSessionId(), params.getNewDate(), params.getNewStartTime());
    }
    
    @DeleteMapping("/training-sessions/{id}")
    public void deleteTrainingSession(@RequestAttribute Long userId, @PathVariable Long id) 
            throws InstanceNotFoundException, PermissionException {
        planService.deleteTrainingSession(userId, id);
    }

    @PutMapping("/training-sessions/{id}")
    public TrainingSessionDto updateTrainingSession(@RequestAttribute Long userId, @PathVariable Long id,
        @Validated @RequestBody UpdateSessionParamsDto params) throws InstanceNotFoundException, PermissionException {
        
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
        
        TrainingSession updatedSession = planService.updateTrainingSession(userId, id, params.getSessionDate(), 
                                                 params.getStartTime(), params.getSport(), params.getObjective(), 
                                                 params.getTotalDistanceOrDuration(), blocks);

        Double tss = 0.0;
        try {
            tss = planService.calculateTSS(updatedSession.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        TrainingSessionDto responseDto = toTrainingSessionDto(updatedSession);
        responseDto.setTss(tss); 
        
        return responseDto;
    }

}

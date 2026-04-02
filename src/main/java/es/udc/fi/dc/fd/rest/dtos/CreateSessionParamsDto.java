package es.udc.fi.dc.fd.rest.dtos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import es.udc.fi.dc.fd.model.entities.TrainingSession;

public class CreateSessionParamsDto {
    
    private Long athleteId;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private TrainingSession.SportType sport;
    private String objective;
    private String totalDistanceOrDuration;
    private List<TrainingBlockDto> blocks;

    public CreateSessionParamsDto() {}

    public CreateSessionParamsDto(Long athleteId, LocalDate sessionDate, LocalTime startTime, TrainingSession.SportType sport, 
                                  String objective, String totalDistanceOrDuration, List<TrainingBlockDto> blocks) {
        this.athleteId = athleteId;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.sport = sport;
        this.objective = objective;
        this.totalDistanceOrDuration = totalDistanceOrDuration;
        this.blocks = blocks;
    }

    public Long getAthleteId() { return athleteId; }
    public void setAthleteId(Long athleteId) { this.athleteId = athleteId; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public TrainingSession.SportType getSport() { return sport; }
    public void setSport(TrainingSession.SportType sport) { this.sport = sport; }

    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = objective; }

    public String getTotalDistanceOrDuration() { return totalDistanceOrDuration; }
    public void setTotalDistanceOrDuration(String totalDistanceOrDuration) { this.totalDistanceOrDuration = totalDistanceOrDuration; }

    public List<TrainingBlockDto> getBlocks() { return blocks; }
    public void setBlocks(List<TrainingBlockDto> blocks) { this.blocks = blocks; }

}

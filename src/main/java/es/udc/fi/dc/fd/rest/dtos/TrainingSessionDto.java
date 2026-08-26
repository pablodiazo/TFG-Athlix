package es.udc.fi.dc.fd.rest.dtos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import  es.udc.fi.dc.fd.model.entities.TrainingSession;

public class TrainingSessionDto {
    
    private Long id;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private TrainingSession.SportType sport;
    private String objective;
    private String totalDistanceOrDuration;
    private Double ce;
    private List<TrainingBlockDto> blocks;

    public TrainingSessionDto() {}

    public TrainingSessionDto(Long id, LocalDate sessionDate, LocalTime startTime, TrainingSession.SportType sport, 
                              String objective, String totalDistanceOrDuration, Double ce, List<TrainingBlockDto> blocks) {
        this.id = id;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.sport = sport;
        this.objective = objective;        
        this.totalDistanceOrDuration = totalDistanceOrDuration;
        this.ce = ce;
        this.blocks = blocks;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Double getCe() { return ce; }
    public void setCe(Double ce) { this.ce = ce; }

    public List<TrainingBlockDto> getBlocks() { return blocks; }
    public void setBlocks(List<TrainingBlockDto> blocks) { this.blocks = blocks; }

}
